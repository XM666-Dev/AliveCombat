package com.xm666.alivecombat.handler;

import com.xm666.alivecombat.AliveCombat;
import com.xm666.alivecombat.Config;
import com.xm666.alivecombat.util.Logger;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;

import java.util.stream.Collectors;

public class FakeSweepHandler {
    private static final RandomSource random = RandomSource.create();

    private static void sweep() {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            player.level().playSound(player, player.getX(), player.getY(), player.getZ(), getSoundEvent(), player.getSoundSource(), 1.0F, 1.0F);
            sweepAttack();
        }
    }

    private static void sweepAttack() {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            double d0 = -Mth.sin(player.getYRot() * ((float) Math.PI / 180F));
            double d1 = Mth.cos(player.getYRot() * ((float) Math.PI / 180F));
            sendParticles(player.getX() + d0, player.getY(0.5F), player.getZ() + d1, d0, d1);
        }
    }

    private static void sendParticles(double posX, double posY, double posZ, double xOffset, double zOffset) {
        ClientboundLevelParticlesPacket clientboundlevelparticlespacket = new ClientboundLevelParticlesPacket(getParticleType(), false, posX, posY, posZ, (float) xOffset, (float) 0.0, (float) zOffset, (float) 0.0, 0);
        handle(clientboundlevelparticlespacket);
    }

    private static void handle(ClientboundLevelParticlesPacket packet) {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            Level level = player.level();
            if (packet.getCount() == 0) {
                double d0 = packet.getMaxSpeed() * packet.getXDist();
                double d2 = packet.getMaxSpeed() * packet.getYDist();
                double d4 = packet.getMaxSpeed() * packet.getZDist();

                try {
                    level.addParticle(packet.getParticle(), packet.isOverrideLimiter(), packet.getX(), packet.getY(), packet.getZ(), d0, d2, d4);
                } catch (Throwable throwable) {
                    Logger.warn("Could not spawn particle effect {}", packet.getParticle());
                }
            } else {
                for (int i = 0; i < packet.getCount(); i++) {
                    double d1 = random.nextGaussian() * (double) packet.getXDist();
                    double d3 = random.nextGaussian() * (double) packet.getYDist();
                    double d5 = random.nextGaussian() * (double) packet.getZDist();
                    double d6 = random.nextGaussian() * (double) packet.getMaxSpeed();
                    double d7 = random.nextGaussian() * (double) packet.getMaxSpeed();
                    double d8 = random.nextGaussian() * (double) packet.getMaxSpeed();

                    try {
                        level.addParticle(
                                packet.getParticle(),
                                packet.isOverrideLimiter(),
                                packet.getX() + d1,
                                packet.getY() + d3,
                                packet.getZ() + d5,
                                d6,
                                d7,
                                d8
                        );
                    } catch (Throwable throwable) {
                        Logger.warn("Could not spawn particle effect {}", packet.getParticle());
                        return;
                    }
                }
            }
        }
    }

    private static SoundEvent getSoundEvent() {
        var type = "entity.player.attack.sweep";
        return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.tryParse(type));
    }

    private static SimpleParticleType getParticleType() {
        var type = "minecraft:sweep_attack";
        return (SimpleParticleType) BuiltInRegistries.PARTICLE_TYPE.get(ResourceLocation.tryParse(type));
    }

    public static class FakeSweepHandlerClient {
        @SubscribeEvent
        static void onAttackEntity(AttackEntityEvent event) {
            var player = event.getEntity();
            if (player.level().isClientSide) {
                var weaponItem = player.getMainHandItem();
                var tags = weaponItem.getTags().map(TagKey::location).map(ResourceLocation::toString).collect(Collectors.toSet());
                if (tags.contains("c:tools/melee_weapon")) {
                    sweep();
                }
            }
        }
    }

    @Mod(value = AliveCombat.MODID, dist = Dist.CLIENT)
    public static class FakeSweepHandlerConfig {
        public FakeSweepHandlerConfig(IEventBus modEventBus) {
            modEventBus.register(FakeSweepHandlerConfig.class);
        }

        @SubscribeEvent
        static void onModConfigLoading(ModConfigEvent.Loading event) {
            toggle();
        }

        @SubscribeEvent
        static void onModConfigReloading(ModConfigEvent.Reloading event) {
            toggle();
        }

        static void toggle() {
            if (Config.FAKE_SWEEP_ENABLED.get()) {
                NeoForge.EVENT_BUS.register(FakeSweepHandlerClient.class);
            } else {
                NeoForge.EVENT_BUS.unregister(FakeSweepHandlerClient.class);
            }
        }
    }
}
