package com.xm666.alivecombat.util;

public class TypeUtil {
    public static <T> T tryConvertValue(Object value, T defaultValue) {
        return (T) tryConvertValue(value, defaultValue.getClass(), defaultValue);
    }


    public static <T extends Enum<T>> Object tryConvertValue(Object value, Class<?> targetClass, Object defaultValue) {
        if (targetClass.isInstance(value)) {
            return value;
        } else if (isNumberClass(targetClass) && value instanceof Number number) {
            return tryConvertNumber(number, targetClass, defaultValue);
        } else if (isNumberClass(targetClass) && value instanceof String string) {
            return tryParseNumber(string, targetClass, defaultValue);
        } else if (targetClass.isEnum() && value instanceof String string) {
            return tryParseEnum(string, (Class<T>) targetClass, defaultValue);
        } else if (targetClass == String.class) {
            return value.toString();
        }
        return defaultValue;
    }

    public static boolean isNumberClass(Class<?> targetClass) {
        return Number.class.isAssignableFrom(targetClass);
    }

    public static Object tryConvertNumber(Number value, Class<?> numberClass, Object defaultValue) {
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

    public static Object tryParseNumber(String value, Class<?> numberClass, Object defaultValue) {
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
        return defaultValue;
    }

    public static <T extends Enum<T>> Object tryParseEnum(String value, Class<T> enumClass, Object defaultValue) {
        try {
            return Enum.valueOf(enumClass, value);
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }
}
