package com.xm666.alivecombat.util;

public class TypeUtil {
    public static <T extends Enum<T>> Object tryCast(Object sourceObject, Class<?> targetClass, Object fallbackValue) {
        if (targetClass.isInstance(sourceObject)) {
            return targetClass.cast(sourceObject);
        } else if (Enum.class.isAssignableFrom(targetClass) && sourceObject instanceof String string) {
            return tryParseEnum((Class<T>) targetClass, string, fallbackValue);
        } else if (Number.class.isAssignableFrom(targetClass) && sourceObject instanceof String string) {
            return tryParseNumber(targetClass, string, fallbackValue);
        } else if (Number.class.isAssignableFrom(targetClass) && sourceObject instanceof Number number) {
            return tryCastNumber(targetClass, number, fallbackValue);
        }
        return fallbackValue;
    }

    public static <T extends Enum<T>> Object tryParseEnum(Class<T> enumClass, String value, Object fallbackValue) {
        try {
            return Enum.valueOf(enumClass, value);
        } catch (IllegalArgumentException e) {
            return fallbackValue;
        }
    }

    public static Object tryParseNumber(Class<?> numberClass, String value, Object fallbackValue) {
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
        } catch (NumberFormatException e) {
        }
        return fallbackValue;
    }

    public static Object tryCastNumber(Class<?> numberClass, Number value, Object fallbackValue) {
        if (numberClass == Integer.class) {
            return value.intValue();
        } else if (numberClass == Long.class) {
            return value.longValue();
        } else if (numberClass == Float.class) {
            return value.floatValue();
        } else if (numberClass == Double.class) {
            return value.doubleValue();
        }
        return fallbackValue;
    }
}
