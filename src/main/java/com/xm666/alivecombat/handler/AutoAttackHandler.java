package com.xm666.alivecombat.handler;

import com.xm666.alivecombat.AliveCombat;
import com.xm666.alivecombat.Config;
import com.xm666.alivecombat.MixinConfig;
import com.xm666.alivecombat.util.Timer;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.common.NeoForge;

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

    private static class AutoAttackHandlerClient {
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

    @Mod(value = AliveCombat.MODID, dist = Dist.CLIENT)
    public static class AutoAttackHandlerConfig {
        public AutoAttackHandlerConfig(IEventBus modEventBus) {
            modEventBus.register(AutoAttackHandlerConfig.class);
        }

        @SubscribeEvent
        static void onModConfigLoading(ModConfigEvent.Loading event) {
            update();
            if (MixinConfig.AUTO_ATTACK_ENABLED.get()) {
                NeoForge.EVENT_BUS.register(AutoAttackHandlerClient.class);
            }
        }

        @SubscribeEvent
        static void onModConfigReloading(ModConfigEvent.Reloading event) {
            update();
        }

        static void update() {
            timer.duration = Config.AUTO_ATTACK_DURATION.get().floatValue();
        }
    }
}
