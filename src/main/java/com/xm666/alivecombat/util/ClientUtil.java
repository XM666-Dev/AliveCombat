package com.xm666.alivecombat.util;

import net.minecraft.client.Minecraft;

public class ClientUtil {
    public static void attack() {
        var mc = Minecraft.getInstance();
        if (mc.player != null && !mc.player.isUsingItem()) {
            mc.startAttack();
        }
    }
}
