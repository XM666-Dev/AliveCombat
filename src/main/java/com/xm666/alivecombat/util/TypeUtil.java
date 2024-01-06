package com.xm666.alivecombat.util;

public class TypeUtil {
    public static <T> T tryCast(Object sourceObject, T defaultValue) {
        return (T) tryCast(sourceObject, defaultValue.getClass(), defaultValue);
    }

    public static <T extends Enum<T>> Object tryCast(Object sourceObject, Class<?> targetClass, Object defaultValue) {
        if (targetClass.isInstance(sourceObject)) {
            return targetClass.cast(sourceObject);
        } else if (Enum.class.isAssignableFrom(targetClass) && sourceObject instanceof String string) {
            return tryParseEnum((Class<T>) targetClass, string, defaultValue);
        } else if (Number.class.isAssignableFrom(targetClass) && sourceObject instanceof String string) {
            return tryParseNumber(targetClass, string, defaultValue);
        } else if (Number.class.isAssignableFrom(targetClass) && sourceObject instanceof Number number) {
            return tryCastNumber(targetClass, number, defaultValue);
        }
        return defaultValue;
    }

    public static <T extends Enum<T>> Object tryParseEnum(Class<T> enumClass, String value, Object defaultValue) {
        try {
            return Enum.valueOf(enumClass, value);
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }

    public static Object tryParseNumber(Class<?> numberClass, String value, Object defaultValue) {
        try {
            if (numberClass == Integer.class) {
                return Integer.parseInt(value);
            } else if (numberClass == Long.class) {
                return Long.parseLong(value);
            } else if (numberClass == Float.class) {
                return Float.parseFloat(value);
            } else if (numberClass == Double.class) {
                return Double.parseDouble(value);
            }
        } catch (NumberFormatException ignored) {
        }
        return defaultValue;
    }

    public static Object tryCastNumber(Class<?> numberClass, Number value, Object defaultValue) {
        if (numberClass == Integer.class) {
            return value.intValue();
        } else if (numberClass == Long.class) {
            return value.longValue();
        } else if (numberClass == Float.class) {
            return value.floatValue();
        } else if (numberClass == Double.class) {
            return value.doubleValue();
        }
        return defaultValue;
    }
}
