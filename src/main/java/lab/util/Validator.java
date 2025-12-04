package lab.util;

import lab.data.Movie;
import lab.util.annotations.*;

import java.lang.reflect.Field;

public class Validator {

    public static boolean isValidObject(Object obj) {
        if (obj == null) return false;

        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);
                if (field.isAnnotationPresent(NotNull.class) && value == null) {
                    return false;
                }

                if (field.isAnnotationPresent(NotEmpty.class)) {
                    if (value == null || ((String) value).isEmpty()) {
                        return false;
                    }
                }

                if (field.isAnnotationPresent(MinSize.class)) {
                    if (value == null) return false;

                    MinSize minSizeAnnotation = field.getAnnotation(MinSize.class);
                    int minSize = minSizeAnnotation.value();

                    if (((String) value).length() < minSize) {
                        return false;
                    }
                }

                if (field.isAnnotationPresent(Min.class)) {
                    if (value == null) return false;

                    Min minAnnotation = field.getAnnotation(Min.class);
                    double minValue = minAnnotation.value();

                    if (!(value instanceof Number)) return false;
                    double numericValue = ((Number) value).doubleValue();

                    if (numericValue < minValue) {
                        return false;
                    }
                }

                if (field.isAnnotationPresent(MoreThan.class)) {
                    if (value == null) return false;

                    MoreThan moreThanAnnotation = field.getAnnotation(MoreThan.class);
                    double minValue = moreThanAnnotation.value();

                    if (!(value instanceof Number)) return false;
                    double numericValue = ((Number) value).doubleValue();

                    if (numericValue <= minValue) {
                        return false;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        if (obj.getClass() == Movie.class) {
            Movie movie = (Movie) obj;
            return isValidMovie(movie);
        }

        return true;
    }

    private static boolean isValidMovie(Movie movie) {
        return movie.getTotalBoxOffice() >= movie.getUsaBoxOffice();
    }
}