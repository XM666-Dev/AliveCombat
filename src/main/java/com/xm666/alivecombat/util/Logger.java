package com.xm666.alivecombat.util;

import com.xm666.alivecombat.AliveCombat;

public class Logger {
    public static <T> void info(T value) {
        AliveCombat.LOGGER.info(String.valueOf(value));
    }
}
