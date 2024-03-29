package com.xm666.alivecombat.handler;

import com.xm666.alivecombat.AliveCombat;
import com.xm666.alivecombat.Config;
import com.xm666.alivecombat.util.Timer;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.common.NeoForge;

public class AutoAttackHandler {
    public static Timer timer = new Timer();
    public static boolean canContinueAttack;

    public static boolean canAutoAttack() {
        var mc = Minecraft.getInstance();
        return mc.crosshairPickEntity != null && switch (Config.autoAttackMode) {
            case CLICK -> timer.isStarted();
            case PRESS ->
                    mc.options.keyAttack.isDown() && mc.player != null && mc.player.getAttackStrengthScale(0.0F) >= 1.0F;
        };
    }

    @SubscribeEvent
    public static void onCameraSetup(ViewportEvent.ComputeCameraAngles event) {
        canContinueAttack = false;
        Minecraft.getInstance().handleKeybinds();
        canContinueAttack = true;
    }

    public enum Mode {
        CLICK,
        PRESS
    }

    @Mod.EventBusSubscriber(modid = AliveCombat.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class Register {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            if (Config.autoAttackEnabled) {
                NeoForge.EVENT_BUS.register(AutoAttackHandler.class);
                timer.duration = Config.autoAttackDuration;
            }
        }
    }
}
