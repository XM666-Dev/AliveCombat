package com.xm666.alivecombat.utils;

import com.xm666.alivecombat.AliveCombatMod;

public class Logger {
    public static <T> void log(T value) {
        AliveCombatMod.LOGGER.info(String.valueOf(value));
    }
}
