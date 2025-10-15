package com.xm666.alivecombat.handler;

import com.xm666.alivecombat.AliveCombat;
import com.xm666.alivecombat.Config;
import com.xm666.alivecombat.util.Timer;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;

public class AutoAttackHandler {
    public static final Timer timer = new Timer();
    public static boolean canContinueAttack = true;

    public static boolean canAutoAttack() {
        var mc = Minecraft.getInstance();
        return mc.crosshairPickEntity != null && switch (Config.AUTO_ATTACK_MODE.get()) {
            case CLICK -> timer.isRunning();
            case PRESS ->
                    mc.options.keyAttack.isDown() && mc.player != null && mc.player.getAttackStrengthScale(0.0F) >= 1.0F;
        };
    }

    public enum Mode {
        CLICK,
        PRESS
    }

    @EventBusSubscriber(modid = AliveCombat.MODID, value = Dist.CLIENT)
    private static class AutoAttackHandlerClient {
        @SubscribeEvent
        static void onModConfigLoading(ModConfigEvent.Loading event) {
            update();
        }

        @SubscribeEvent
        static void onModConfigReloading(ModConfigEvent.Reloading event) {
            update();
        }

        static void update() {
            timer.duration = Config.AUTO_ATTACK_DURATION.get().floatValue();
        }

        @SubscribeEvent
        static void onRenderFramePost(RenderFrameEvent.Post event) {
            canContinueAttack = false;
            var mc = Minecraft.getInstance();
            if (mc.player != null) {
                mc.handleKeybinds();
            }
            canContinueAttack = true;
        }
    }
}
