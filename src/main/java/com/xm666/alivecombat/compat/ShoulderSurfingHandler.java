package com.xm666.alivecombat.compat;

import com.github.exopandora.shouldersurfing.api.client.ShoulderSurfing;
import com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl;
import net.minecraft.client.Minecraft;

public class ShoulderSurfingHandler {
    public static void tick() {
        var mc = Minecraft.getInstance();
        if (mc.screen != null) return;

        var shoulderSurfing = (ShoulderSurfingImpl) ShoulderSurfing.getInstance();
        shoulderSurfing.getInputHandler().tick();
    }

    public static boolean isShoulderSurfing() {
        var shoulderSurfing = ShoulderSurfing.getInstance();
        return shoulderSurfing.isShoulderSurfing();
    }
}
