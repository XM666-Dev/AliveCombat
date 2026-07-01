package com.xm666.alivecombat.compat;

import net.neoforged.fml.ModList;

public class SodiumHandler {
    public static float getParticleRollDirection() {
        return isSodiumLoaded() ? 1.0F : -1.0F;
    }

    private static boolean isSodiumLoaded() {
        var list = ModList.get();
        return list.isLoaded("sodium") || list.isLoaded("embeddium");
    }
}
