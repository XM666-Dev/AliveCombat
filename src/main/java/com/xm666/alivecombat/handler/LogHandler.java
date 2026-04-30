package com.xm666.alivecombat.handler;

import com.xm666.alivecombat.AliveCombat;

public class LogHandler {
    public static <T> void info(T value) {
        AliveCombat.LOGGER.info(String.valueOf(value));
    }

    public static void info(String format, Object... arguments) {
        AliveCombat.LOGGER.info(format, arguments);
    }

    public static <T> void warn(T value) {
        AliveCombat.LOGGER.warn(String.valueOf(value));
    }

    public static void warn(String format, Object... arguments) {
        AliveCombat.LOGGER.warn(format, arguments);
    }
}
