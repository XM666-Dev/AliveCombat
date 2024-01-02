package com.xm666.alivecombat.handler;

import com.xm666.alivecombat.Config;
import com.xm666.alivecombat.util.ClientUtil;
import com.xm666.alivecombat.util.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TickEvent;

public class AutoAttackHandler {
    public static Timer timer = new Timer();

    public static void load() {
        if (Config.autoAttackEnabled) {
            NeoForge.EVENT_BUS.register(AutoAttackHandler.class);
            timer.duration = Config.autoAttackDuration;
        }
    }

    public static boolean canAttack() {
        var mc = Minecraft.getInstance();
        var hasEntity = mc.hitResult != null && mc.hitResult.getType() == HitResult.Type.ENTITY;
        return hasEntity && switch (Config.autoAttackMode) {
            case CLICK -> timer.isStarted();
            case PRESS ->
                    mc.options.keyAttack.isDown() && mc.player != null && mc.player.getAttackStrengthScale(0.0F) >= 1.0F;
        };
    }

    public static void autoAttack() {
        var mc = Minecraft.getInstance();
        if (mc.options.keyAttack.clickCount > 0) {
            timer.start();
        }
        if (canAttack()) {
            ClientUtil.attack();
            timer.stop();
        }
    }

    @SubscribeEvent
    public static void onPreClientTick(TickEvent.ClientTickEvent event) {
        Minecraft.getInstance().gameRenderer.pick(1.0F);
        autoAttack();
    }

    @SubscribeEvent
    public static void onCameraSetup(ViewportEvent.ComputeCameraAngles event) {
        autoAttack();
    }

    public enum Mode {
        CLICK,
        PRESS
    }
}
