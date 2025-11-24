CREATE OR REPLACE FUNCTION delete_movie_by_golden_palm_count(target_count INTEGER)
RETURNS INTEGER
LANGUAGE plpgsql
AS $$
DECLARE
    movie_id INTEGER;
BEGIN
    SELECT id INTO movie_id
    FROM movies
    WHERE golden_palm_count = target_count
    LIMIT 1
    FOR UPDATE;

    IF movie_id IS NOT NULL THEN
        DELETE FROM movies
        WHERE id = movie_id;

        RETURN movie_id;
    ELSE
        RETURN -1;
    END IF;
END;
$$;


CREATE OR REPLACE FUNCTION get_movies_by_name_prefix(name_prefix varchar)
RETURNS SETOF movies AS $$
BEGIN
    RETURN QUERY
    SELECT *
    FROM movies
    WHERE name ILIKE '%' || name_prefix || '%'
    ORDER BY name;
END;
$$ LANGUAGE plpgsql;



CREATE OR REPLACE FUNCTION get_movies_by_golden_palm_count(min_count INTEGER)
RETURNS SETOF movies AS $$
BEGIN
    RETURN QUERY
    SELECT *
    FROM movies
    WHERE golden_palm_count > min_count
    ORDER BY golden_palm_count DESC, name;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION get_operators_without_oscars()
RETURNS SETOF persons AS $$
BEGIN
    RETURN QUERY
    SELECT p.*
    FROM persons p
    WHERE p.id IN (
        SELECT DISTINCT m.operator_id
        FROM movies m
        WHERE m.operator_id IS NOT NULL
    )
    AND NOT EXISTS (
        SELECT 1
        FROM movies m
        WHERE m.operator_id = p.id
        AND m.oscars_count > 0
    )
    ORDER BY p.name;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION reward_long_movies(min_length INT, oscars_to_add INT)
RETURNS VOID AS $$
BEGIN
    UPDATE movies
    SET oscars_count = oscars_count + oscars_to_add
    WHERE length > min_length;
END;
$$ LANGUAGE plpgsql;
