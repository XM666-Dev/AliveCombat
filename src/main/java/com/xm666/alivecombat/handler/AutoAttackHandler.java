package com.xm666.alivecombat.handler;

import com.xm666.alivecombat.Config;
import com.xm666.alivecombat.utils.Timer;
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

    public static boolean getInput() {
        var mc = Minecraft.getInstance();
        return switch (Config.autoAttackKeyMode) {
            case CLICK -> mc.options.keyAttack.clickCount > 0;
            case PRESS -> mc.options.keyAttack.isDown();
        };
    }

    public static void autoAttack() {
        var mc = Minecraft.getInstance();
        var hasInput = getInput();
        var hasTime = timer.is_started();
        var hasEntity = mc.hitResult != null && mc.hitResult.getType() == HitResult.Type.ENTITY;
        if (hasInput) {
            timer.start();
        }
        if (hasTime && hasEntity) {
            mc.startAttack();
        }
    }

    @SubscribeEvent()
    public static void onPreClientTick(TickEvent.ClientTickEvent event) {
        Minecraft.getInstance().gameRenderer.pick(1.0F);
        autoAttack();
    }

    @SubscribeEvent
    public static void onCameraSetup(ViewportEvent.ComputeCameraAngles event) {
        autoAttack();
    }

    public enum KeyMode {
        CLICK,
        PRESS
    }
}
