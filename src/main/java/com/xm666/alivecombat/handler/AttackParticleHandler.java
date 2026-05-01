package com.xm666.alivecombat.handler;

import com.xm666.alivecombat.AliveCombat;
import com.xm666.alivecombat.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;

public class AttackParticleHandler {
    private static final RandomSource random = RandomSource.create();

    @SuppressWarnings("DataFlowIssue")
    private static boolean canSweep(Entity target) {
        var mc = Minecraft.getInstance();
        var player = mc.player;
        var attackStrengthScale = player.getAttackStrengthScale(0.5F);
        var full = attackStrengthScale > 0.9F;
        var sprint = player.isSprinting() && full;

        var crit = full && player.canCriticalAttack(target);
        var critEvent = CommonHooks.fireCriticalHit(player, target, crit, crit ? 1.5F : 1.0F);

        var sweep = player.isSweepAttack(full, critEvent.isCriticalHit() && critEvent.disableSweep(), sprint);
        var sweepEvent = CommonHooks.fireSweepAttack(player, target, sweep);
        return sweepEvent.isSweeping();
    }

    @SuppressWarnings("DataFlowIssue")
    private static void sweepAttack() {
        var player = Minecraft.getInstance().player;
        var xOffset = -Mth.sin(player.getYRot() * Mth.DEG_TO_RAD);
        var zOffset = Mth.cos(player.getYRot() * Mth.DEG_TO_RAD);
        player.level().playSound(player, player.getX(), player.getY(), player.getZ(), getSoundEvent(), player.getSoundSource(), 1.0F, 1.0F);
        sendParticles(getParticleType(), player.getX() + xOffset, player.getY(0.5), player.getZ() + zOffset, 0, xOffset, 0.0, zOffset, 0.0);
    }

    @SuppressWarnings({"SameParameterValue", "DataFlowIssue"})
    private static <T extends ParticleOptions> void sendParticles(T type, double posX, double posY, double posZ, int particleCount, double xOffset, double yOffset, double zOffset, double speed) {
        var mc = Minecraft.getInstance();
        var blockPos = mc.player.blockPosition();
        if (blockPos.closerToCenterThan(new Vec3(posX, posY, posZ), 32.0F)) {
            if (particleCount == 0) {
                var xDist = speed * xOffset;
                var yDist = speed * yOffset;
                var zDist = speed * zOffset;

                mc.level.addParticle(type, false, false, posX, posY, posZ, xDist, yDist, zDist);
            } else {
                for (var i = 0; i < particleCount; ++i) {
                    var xDist = random.nextGaussian() * xOffset;
                    var yDist = random.nextGaussian() * yOffset;
                    var zDist = random.nextGaussian() * zOffset;
                    var xSpeed = random.nextGaussian() * speed;
                    var ySpeed = random.nextGaussian() * speed;
                    var zSpeed = random.nextGaussian() * speed;

                    mc.level.addParticle(type, false, false, posX + xDist, posY + yDist, posZ + zDist, xSpeed, ySpeed, zSpeed);
                }
            }
        }
    }

    @SuppressWarnings({"DataFlowIssue", "OptionalGetWithoutIsPresent"})
    private static SoundEvent getSoundEvent() {
        var type = "entity.player.attack.sweep";
        return BuiltInRegistries.SOUND_EVENT.get(Identifier.tryParse(type)).get().value();
    }

    @SuppressWarnings({"DataFlowIssue", "OptionalGetWithoutIsPresent"})
    private static SimpleParticleType getParticleType() {
        var type = "minecraft:sweep_attack";
        return (SimpleParticleType) BuiltInRegistries.PARTICLE_TYPE.get(Identifier.tryParse(type)).get().value();
    }

    public static class AttackParticleClient {
        @SubscribeEvent
        public static void onAttackEntity(AttackEntityEvent event) {
            var player = event.getEntity();
            if (!player.level().isClientSide() || canSweep(event.getTarget())) return;

            var weapon = player.getWeaponItem();
            var tags = weapon.getTags();
            var melee = tags.map(TagKey::location).map(Identifier::toString).anyMatch(s -> s.equals("c:tools/melee_weapon"));
            if (!melee) return;

            var attackStrengthScale = player.getAttackStrengthScale(0.5F);
            var full = attackStrengthScale > 0.9F;
            if (!full) return;

            sweepAttack();
        }
    }

    @EventBusSubscriber(modid = AliveCombat.MODID, value = Dist.CLIENT)
    public static class AttackParticleConfig {
        @SubscribeEvent
        public static void onModConfigLoading(ModConfigEvent.Loading event) {
            toggle();
        }

        @SubscribeEvent
        public static void onModConfigReloading(ModConfigEvent.Reloading event) {
            toggle();
        }

        private static void toggle() {
            if (Config.ATTACK_PARTICLE_ENABLED.get()) {
                NeoForge.EVENT_BUS.register(AttackParticleClient.class);
            } else {
                NeoForge.EVENT_BUS.unregister(AttackParticleClient.class);
            }
        }
    }
}
