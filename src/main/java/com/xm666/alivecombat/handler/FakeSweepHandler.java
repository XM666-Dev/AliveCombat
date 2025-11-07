package com.xm666.alivecombat.handler;

import com.xm666.alivecombat.AliveCombat;
import com.xm666.alivecombat.Config;
import com.xm666.alivecombat.util.Logger;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.ToolActions;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;

import java.util.Objects;
import java.util.stream.Collectors;

public class FakeSweepHandler {
    private static final RandomSource random = RandomSource.create();

    private static boolean canSweep(Entity target) {
        var player = Objects.requireNonNull(Minecraft.getInstance().player);
        float f2 = player.getAttackStrengthScale(0.5F);

        boolean flag = f2 > 0.9F;
        boolean flag1 = player.isSprinting() && flag;

        boolean flag2 = flag && player.fallDistance > 0.0F && !player.onGround() && !player.onClimbable() && !player.isInWater() && !player.hasEffect(MobEffects.BLINDNESS) && !player.isPassenger() && target instanceof LivingEntity && !player.isSprinting();
        CriticalHitEvent critEvent = CommonHooks.fireCriticalHit(player, target, flag2, flag2 ? 1.5F : 1.0F);
        flag2 = critEvent.isCriticalHit();

        boolean flag3 = false;
        double d0 = player.walkDist - player.walkDistO;
        if (flag && !flag2 && !flag1 && player.onGround() && d0 < (double) player.getSpeed()) {
            ItemStack itemstack = player.getItemInHand(InteractionHand.MAIN_HAND);
            flag3 = itemstack.canPerformAction(ToolActions.SWORD_SWEEP);
        }

        return flag3;
    }

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
            sendParticles(getParticleType(), player.getX() + d0, player.getY(0.5), player.getZ() + d1, 0, d0, 0.0, d1, 0.0);
        }
    }

    private static <T extends ParticleOptions> void sendParticles(T type, double posX, double posY, double posZ, int particleCount, double xOffset, double yOffset, double zOffset, double speed) {
        BlockPos blockpos = Objects.requireNonNull(Minecraft.getInstance().player).blockPosition();
        if (blockpos.closerToCenterThan(new Vec3(posX, posY, posZ), 32.0F)) {
            if (particleCount == 0) {
                double d0 = speed * xOffset;
                double d2 = speed * yOffset;
                double d4 = speed * zOffset;

                try {
                    Objects.requireNonNull(Minecraft.getInstance().level).addParticle(type, false, posX, posY, posZ, d0, d2, d4);
                } catch (Throwable throwable) {
                    Logger.warn("Could not spawn particle effect {}", type);
                }
            } else {
                for (int i = 0; i < particleCount; ++i) {
                    double d1 = random.nextGaussian() * xOffset;
                    double d3 = random.nextGaussian() * yOffset;
                    double d5 = random.nextGaussian() * zOffset;
                    double d6 = random.nextGaussian() * speed;
                    double d7 = random.nextGaussian() * speed;
                    double d8 = random.nextGaussian() * speed;

                    try {
                        Objects.requireNonNull(Minecraft.getInstance().level).addParticle(type, false, posX + d1, posY + d3, posZ + d5, d6, d7, d8);
                    } catch (Throwable throwable) {
                        Logger.warn("Could not spawn particle effect {}", type);
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
                if (tags.contains("c:tools/melee_weapon") && !canSweep(event.getTarget())) {
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
