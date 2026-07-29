package com.xm666.alivecombat.handler;

import com.xm666.alivecombat.AliveCombat;
import com.xm666.alivecombat.Config;
import com.xm666.alivecombat.MixinConfig;
import com.xm666.alivecombat.compat.ShoulderSurfingHandler;
import com.xm666.alivecombat.timer.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.common.NeoForge;

public class AutoAttackHandler {
    public static final Timer timer = new Timer(() -> Config.AUTO_ATTACK_DURATION.get().floatValue());
    public static boolean canContinueAttack = true;
    public static boolean disableSwingHand = false;

    public static boolean isAttackReady() {
        var mc = Minecraft.getInstance();
        if (!(mc.hitResult instanceof EntityHitResult)) return false;

        return switch (Config.AUTO_ATTACK_MODE.get()) {
            case CLICK -> {
                var running = timer.isRunning();
                if (running) {
                    timer.stop();
                }
                yield running;
            }
            case PRESS -> {
                if (!mc.options.keyAttack.isDown()) yield false;

                var scale = mc.player.getAttackStrengthScale(0.5F);
                yield Config.AUTO_ATTACK_FAST.get() ? scale > 0.9F : scale >= 1.0F;
            }
        };
    }

    public enum Mode {
        CLICK,
        PRESS
    }

    public static class AutoAttackClient {
        @SubscribeEvent
        public static void onRenderFramePost(RenderFrameEvent.Post event) {
            var mc = Minecraft.getInstance();
            if (mc.player == null) return;

            if (ModList.get().isLoaded("shouldersurfing")) {
                ShoulderSurfingHandler.tick();
            }

            canContinueAttack = false;
            mc.handleKeybinds();
            canContinueAttack = true;
        }

        @SubscribeEvent
        public static void onClickInput(InputEvent.InteractionKeyMappingTriggered event) {
            if (!disableSwingHand) return;

            event.setSwingHand(false);
        }
    }

    @Mod(value = AliveCombat.MODID, dist = Dist.CLIENT)
    public static class AutoAttackConfig {
        public AutoAttackConfig(IEventBus modEventBus) {
            modEventBus.register(AutoAttackConfig.class);
        }

        @SubscribeEvent
        public static void onModConfigLoading(ModConfigEvent.Loading event) {
            if (!MixinConfig.AUTO_ATTACK_ENABLED.get()) return;

            NeoForge.EVENT_BUS.register(AutoAttackClient.class);
        }
    }
}
