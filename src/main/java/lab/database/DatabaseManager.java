package lab.database;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lab.data.Coordinates;
import lab.data.Location;
import lab.data.Movie;
import lab.data.Person;
import lab.util.DBObject;
import lab.util.Validator;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import javax.validation.ValidationException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Stateless
public class DatabaseManager {
    @PersistenceContext(unitName = "PersistenceUnit")
    private EntityManager em;

    public void addObject(DBObject object) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        try {
            if (Validator.validateObject(object)) {
                em.persist(object);
            } else {
                throw new IllegalArgumentException(object.getClass() + " validation failed");
            }
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        }
    }

    public void updateObject(DBObject object) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        try {
            if (Validator.validateObject(object)) {
                if (em.find(object.getClass(), object.getId()) == null) {
                    throw new RuntimeException(object.getClass() + " doesn't exist");
                }
                em.merge(object);
            } else {
                throw new IllegalArgumentException(object.getClass() + "validation failed");
            }
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        }
    }

    public <T extends DBObject> void deleteObject(Class<T> entityClass, int id) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        try {
            DBObject existObject = em.find(entityClass, id);
            if (existObject == null) {
                throw new RuntimeException(entityClass + " doesn't exist");
            }
            em.remove(existObject);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        }
    }


    public List<Movie> getMovieList() {
        return em.createQuery("SELECT m FROM Movie m", Movie.class).getResultList();
    }

    public List<Person> getPersonList() {
        return em.createQuery("SELECT p FROM Person p", Person.class).getResultList();
    }

    public List<Location> getLocationList() {
        return em.createQuery("SELECT l FROM Location l", Location.class).getResultList();
    }

    public List<Coordinates> getCoordinatesList() {
        return em.createQuery("SELECT c FROM Coordinates c", Coordinates.class).getResultList();
    }

    public Movie getMovieById(int id) {
        try {
            Movie movie = em.find(Movie.class, id);
            if (movie == null) {
                System.err.println("Movie not found for id: " + id);
            }
            return movie;
        } catch (Exception e) {
            System.err.println("Error while fetching Movie by id: " + id);
            e.printStackTrace();
            return null;
        }
    }

    public Person getPersonById(int id) {
        try {
            Person person = em.find(Person.class, id);
            if (person == null) {
                System.err.println("Person not found for id: " + id);
            }
            return person;
        } catch (Exception e) {
            System.err.println("Error while fetching Person by id: " + id);
            e.printStackTrace();
            return null;
        }
    }

    public Location getLocationById(int id) {
        try {
            Location location = em.find(Location.class, id);
            if (location == null) {
                System.err.println("Location not found for id: " + id);
            }
            return location;
        } catch (Exception e) {
            System.err.println("Error while fetching Location by id: " + id);
            e.printStackTrace();
            return null;
        }
    }

    public Coordinates getCoordinatesById(int id) {
        try {
            Coordinates coordinates = em.find(Coordinates.class, id);
            if (coordinates == null) {
                System.err.println("Coordinates not found for id: " + id);
            }
            return coordinates;
        } catch (Exception e) {
            System.err.println("Error while fetching Coordinates by id: " + id);
            e.printStackTrace();
            return null;
        }
    }

    public Integer deleteMovieByGoldenPalmCount(int count) {
        try {
            return (Integer) em.createNativeQuery("SELECT delete_movie_by_golden_palm_count(?)")
                    .setParameter(1, count)
                    .getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public List<Movie> getMoviesByNamePrefix(String prefix) {
        try {
            return em.createNativeQuery(
                            "SELECT * FROM get_movies_by_name_prefix(?)", Movie.class)
                    .setParameter(1, prefix)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }


    public List<Movie> findMoviesByGoldenPalmCountGreaterThan(int minCount) {
        try {
            return em.createNativeQuery("SELECT * FROM get_movies_by_golden_palm_count(?)", Movie.class)
                    .setParameter(1, minCount)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }


    public List<Person> findOperatorsWithoutOscars() {
        try {
            List<Person> operators = em.createNativeQuery(
                            "SELECT * FROM get_operators_without_oscars()", Person.class)
                    .getResultList();

            System.out.println("Найдено операторов без фильмов-обладателей Оскара: " + operators.size());
            return operators;

        } catch (Exception e) {
            System.err.println("Ошибка при поиске операторов без оскаров: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public void rewardLongMovies(int minLength, int oscarsToAdd) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.createNativeQuery("SELECT reward_long_movies(?, ?)")
                    .setParameter(1, minLength)
                    .setParameter(2, oscarsToAdd)
                    .executeUpdate();
            transaction.commit();
            System.out.println("Наградили все фильмы длиннее " + minLength + " на " + oscarsToAdd + " Оскаров");
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("Ошибка при награждении фильмов: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public List<Movie> findMoviesByCoordinatesId(int coordinatesId) {
        return em.createQuery("SELECT m FROM Movie m WHERE m.coordinates.id = :coordinatesId", Movie.class)
                .setParameter("coordinatesId", coordinatesId)
                .getResultList();
    }

    public List<Person> findPersonsByLocationId(int locationId) {
        return em.createQuery("SELECT p FROM Person p WHERE p.location.id = :locationId", Person.class)
                .setParameter("locationId", locationId)
                .getResultList();
    }

    public List<Movie> findMoviesByPersonId(int personId) {
        return em.createQuery("SELECT m FROM Movie m WHERE m.director.id = :personId " +
                        "OR m.screenwriter.id = :personId " +
                        "OR m.operator.id = :personId ", Movie.class)
                .setParameter("personId", personId)
                .getResultList();
    }

    public boolean importObjects(byte[] fileContent) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        try {
            ObjectMapper mapper = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            List<DBObject> objects;
            JsonNode root = mapper.readTree(fileContent);
            if (root.isArray()) {
                objects = mapper.readValue(fileContent, new TypeReference<List<DBObject>>() {
                });
            } else if (root.isObject()) {
                DBObject obj = mapper.treeToValue(root, DBObject.class);
                objects = Collections.singletonList(obj);
            } else {
                throw new IOException("Unsupported JSON root type: " + root.getNodeType());
            }

            for (DBObject obj : objects) {
                if (!Validator.validateObject(obj)) {
                    throw new ValidationException("Ошибка валидации");
                }
            }

            for (DBObject obj : objects) {
                em.persist(obj);
            }

            transaction.commit();
            return true;
        } catch (Exception e) {
            System.err.println("Ошибка добавления: " + e.getMessage());
            transaction.rollback();
            return false;
        }
    }
}
