package com.xm666.alivecombat.handler;

import com.xm666.alivecombat.AliveCombat;
import com.xm666.alivecombat.Config;
import com.xm666.alivecombat.MixinConfig;
import com.xm666.alivecombat.timer.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.common.NeoForge;

public class AutoAttackHandler {
    public static final Timer timer = new Timer(() -> Config.AUTO_ATTACK_DURATION.get().floatValue());
    public static boolean canContinueAttack = true;

    @SuppressWarnings("DataFlowIssue")
    public static boolean readyAttack() {
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
                var adjustTicks = 0.5F * mc.getTimer().getGameTimeDeltaPartialTick(true);
                yield mc.player.getAttackStrengthScale(adjustTicks) > 0.9F && mc.options.keyAttack.isDown();
            }
        };
    }

    public enum Mode {
        CLICK,
        PRESS
    }

    private static class AutoAttackClient {
        @SubscribeEvent
        public static void onRenderFramePost(RenderFrameEvent.Post event) {
            var mc = Minecraft.getInstance();
            if (mc.player == null) return;

            canContinueAttack = false;
            mc.handleKeybinds();
            canContinueAttack = true;
        }
    }

    @EventBusSubscriber(modid = AliveCombat.MODID, value = Dist.CLIENT)
    private static class AutoAttackConfig {
        @SubscribeEvent
        public static void onModConfigLoading(ModConfigEvent.Loading event) {
            if (!MixinConfig.AUTO_ATTACK_ENABLED.get()) return;

            NeoForge.EVENT_BUS.register(AutoAttackClient.class);
        }
    }
}
