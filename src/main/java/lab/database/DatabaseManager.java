package lab.database;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.lang.reflect.*;
import java.util.*;

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

    public <T extends DBObject> List<T> getObjectList(Class<T> clazz) {
        String query = "SELECT obj FROM " + clazz.getSimpleName() + " obj";
        return em.createQuery(query, clazz).getResultList();
    }

    public <T extends DBObject> T getObjectById(Class<T> clazz, int id) {
        try {
            T obj = em.find(clazz, id);
            if (obj == null) {
                System.err.println(clazz.getSimpleName() + " not found for id: " + id);
            }
            return obj;
        } catch (Exception e) {
            System.err.println("Error while fetching " + clazz.getSimpleName() + " by id: " + id);
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

    public int importObjects(byte[] fileContent) {
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

            int totalCount = 0;
            for (DBObject obj : objects) {
                if (!Validator.validateObject(obj)) {
                    throw new ValidationException("Ошибка валидации");
                }
                totalCount += countAllObjects(obj);
            }

            for (DBObject obj : objects) {
                em.persist(obj);
            }
            System.out.println("COUNT: " + totalCount + "\n\n\n");
            transaction.commit();
            return totalCount;
        } catch (Exception e) {
            System.err.println("Ошибка добавления: " + e.getMessage());
            transaction.rollback();
            return -1;
        }
    }


    private int countAllObjects(Object root) {
        // используем identity set, чтобы корректно обрабатывать циклы
        Set<Object> visited = Collections.newSetFromMap(new IdentityHashMap<>());
        return countRecursive(root, visited);
    }

    private int countRecursive(Object current, Set<Object> visited) {
        if (current == null) return 0;
        if (visited.contains(current)) return 0;
        visited.add(current);

        int count = 0;
        // Если это DBObject — считаем +1
        if (current instanceof DBObject) {
            count = 1;
        }

        Class<?> cls = current.getClass();

        // Если это коллекция — пройтись по элементам
        if (current instanceof Iterable) {
            for (Object el : (Iterable<?>) current) {
                count += countRecursive(el, visited);
            }
            return count;
        }

        // Если это массив
        if (cls.isArray()) {
            int len = Array.getLength(current);
            for (int i = 0; i < len; i++) {
                Object el = Array.get(current, i);
                count += countRecursive(el, visited);
            }
            return count;
        }

        // Иначе — смотреть поля (private тоже)
        while (cls != null && cls != Object.class) {
            for (Field f : cls.getDeclaredFields()) {
                f.setAccessible(true);
                try {
                    Object val = f.get(current);
                    if (val == null) continue;

                    // Если поле само DBObject или коллекция/массив — рекурсивно
                    if (val instanceof DBObject || val instanceof Iterable || val.getClass().isArray()) {
                        count += countRecursive(val, visited);
                    } else {
                        // Можно также рекурсивно спускаться в объекты других типов,
                        // если есть шанс что внутри них лежат DBObject
                        // (опционально, по необходимости)
                    }
                } catch (IllegalAccessException e) {
                    // игнорируем/логируем
                }
            }
            cls = cls.getSuperclass();
        }

        return count;
    }

}
