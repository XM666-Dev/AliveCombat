package com.xm666.alivecombat.handler;

import com.xm666.alivecombat.AliveCombat;
import com.xm666.alivecombat.MixinConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;

public class AttackParticleHandler {
    private static final RandomSource random = RandomSource.create();

    private static boolean canAddParticle(Player player) {
        if (!player.isLocalPlayer()) return false;

        var weapon = player.getWeaponItem();
        if (!weapon.is(Tags.Items.MELEE_WEAPON_TOOLS)) return false;

        var attackStrengthScale = player.getAttackStrengthScale(0.5F);
        return attackStrengthScale > 0.9F;
    }

    private static void addParticle(Player player, Entity target, boolean isCriticalHit, boolean isSprintHit) {
        var mc = Minecraft.getInstance();
        var partialTick = mc.getTimer().getGameTimeDeltaPartialTick(true);
        var boundingBox = target.getBoundingBox();
        var eyePosition = player.getEyePosition(partialTick);
        var viewVector = player.getViewVector(partialTick);
        var entityInteractionRange = player.entityInteractionRange();
        var hitVector = viewVector.scale(entityInteractionRange);
        var hitPosition = eyePosition.add(hitVector);
        var optionalHitPoint = ClipHandler.expandedClip(boundingBox, eyePosition, hitPosition);
        if (optionalHitPoint.isEmpty()) return;

        var x = player.getX();
        var y = player.getY();
        var z = player.getZ();
        var soundEvent = getSoundEvent();
        var soundSource = player.getSoundSource();

        var partialType = getParticleType();
        var hitPoint = optionalHitPoint.get();
        var originalX = x - Mth.sin(player.getYRot() * Mth.DEG_TO_RAD);
        var originalY = player.getY(0.5);
        var originalZ = z + Mth.cos(player.getYRot() * Mth.DEG_TO_RAD);
        var originalPosition = new Vec3(originalX, originalY, originalZ);
        var position = hitPoint.lerp(originalPosition, 0.5);
        var roll = getParticleRoll(isCriticalHit, isSprintHit);
        player.level().playSound(player, x, y, z, soundEvent, soundSource, 1.0F, 1.0F);
        sendParticles(partialType, position.x, position.y, position.z, 0, 0.0, roll, 0.0, 1.0);
    }

    private static SoundEvent getSoundEvent() {
        var location = "entity.player.attack.sweep";
        return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.tryParse(location));
    }

    private static SimpleParticleType getParticleType() {
        var location = "minecraft:sweep_attack";
        return (SimpleParticleType) BuiltInRegistries.PARTICLE_TYPE.get(ResourceLocation.tryParse(location));
    }

    private static double getParticleRoll(boolean isCriticalHit, boolean isSprintHit) {
        if (isCriticalHit) return Mth.PI * 0.5;

        if (isSprintHit) return 0.0;

        return Mth.PI * 0.25;
    }

    private static <T extends ParticleOptions> void sendParticles(T type, double posX, double posY, double posZ, int particleCount, double xOffset, double yOffset, double zOffset, double speed) {
        var mc = Minecraft.getInstance();
        var blockPos = mc.player.blockPosition();
        if (blockPos.closerToCenterThan(new Vec3(posX, posY, posZ), 32.0F)) {
            if (particleCount == 0) {
                var xDist = speed * xOffset;
                var yDist = speed * yOffset;
                var zDist = speed * zOffset;

                mc.level.addParticle(type, false, posX, posY, posZ, xDist, yDist, zDist);
            } else {
                for (var i = 0; i < particleCount; ++i) {
                    var xDist = random.nextGaussian() * xOffset;
                    var yDist = random.nextGaussian() * yOffset;
                    var zDist = random.nextGaussian() * zOffset;
                    var xSpeed = random.nextGaussian() * speed;
                    var ySpeed = random.nextGaussian() * speed;
                    var zSpeed = random.nextGaussian() * speed;

                    mc.level.addParticle(type, false, posX + xDist, posY + yDist, posZ + zDist, xSpeed, ySpeed, zSpeed);
                }
            }
        }
    }

    public static class AttackParticleClient {
        @SubscribeEvent
        public static void onCriticalHit(CriticalHitEvent event) {
            var player = event.getEntity();
            if (!canAddParticle(player)) return;

            addParticle(player, event.getTarget(), event.isVanillaCritical(), event.getEntity().isSprinting());
        }
    }

    @EventBusSubscriber(modid = AliveCombat.MODID, value = Dist.CLIENT)
    public static class AttackParticleConfig {
        @SubscribeEvent
        public static void onModConfigLoading(ModConfigEvent.Loading event) {
            if (!MixinConfig.ATTACK_PARTICLE_ENABLED.get()) return;

            NeoForge.EVENT_BUS.register(AttackParticleClient.class);
        }
    }
}
