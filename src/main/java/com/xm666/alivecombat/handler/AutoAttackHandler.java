package com.xm666.alivecombat.handler;

import com.xm666.alivecombat.AliveCombat;
import com.xm666.alivecombat.Config;
import com.xm666.alivecombat.MixinConfig;
import com.xm666.alivecombat.compat.ShoulderSurfingHandler;
import com.xm666.alivecombat.timer.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

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
                yield Config.AUTO_ATTACK_FAST.get() ? scale > 0.9F : scale == 1.0F;
            }
        };
    }

    public enum Mode {
        CLICK,
        PRESS
    }

    public static class AutoAttackClient {
        @SubscribeEvent
        public static void onRenderFramePost(TickEvent.RenderTickEvent event) {
            if (event.phase != TickEvent.Phase.END) return;

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

    @OnlyIn(Dist.CLIENT)
    @Mod(value = AliveCombat.MODID)
    public static class AutoAttackConfig {
        public AutoAttackConfig(IEventBus modEventBus) {
            modEventBus.register(AutoAttackConfig.class);
        }

        @SubscribeEvent
        public static void onModConfigLoading(ModConfigEvent.Loading event) {
            if (!MixinConfig.AUTO_ATTACK_ENABLED.get()) return;

            MinecraftForge.EVENT_BUS.register(AutoAttackClient.class);
        }
    }
}
