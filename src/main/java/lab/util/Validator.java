package lab.util;

import lab.util.annotations.*;

import java.lang.reflect.Field;

public class Validator {

    public static boolean validateObject(Object obj) {
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
        return true;
    }
}