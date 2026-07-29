package com.xm666.alivecombat.handler;

import com.xm666.alivecombat.AliveCombat;
import com.xm666.alivecombat.MixinConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class AttackParticleHandler {
    private static final RandomSource random = RandomSource.create();

    private static boolean canAddParticle(Player player) {
        if (!player.isLocalPlayer()) return false;

        var weapon = player.getMainHandItem();
        if (!weapon.is(Tags.Items.TOOLS)) return false;

        var attackStrengthScale = player.getAttackStrengthScale(0.5F);
        return attackStrengthScale > 0.9F;
    }

    private static void addParticle(Player player, Entity target, boolean isCriticalHit, boolean isSprintHit) {
        var mc = Minecraft.getInstance();
        var partialTick = mc.getPartialTick();
        var boundingBox = target.getBoundingBox();
        var eyePosition = player.getEyePosition(partialTick);
        var viewVector = player.getViewVector(partialTick);
        var entityInteractionRange = player.getEntityReach();
        var hitVector = viewVector.scale(entityInteractionRange);
        var hitPosition = eyePosition.add(hitVector);
        var optionalHitPoint = ClipHandler.expandedClip(boundingBox, eyePosition, hitPosition);
        if (optionalHitPoint.isEmpty()) return;

        var partialType = getParticleType();
        var hitPoint = optionalHitPoint.get();
        var lerpedHitPoint = new Vec3(hitPoint.x, (hitPoint.y + target.getY(0.5)) * 0.5, hitPoint.z);
        var position = lerpedHitPoint.subtract(viewVector.scale(0.5));
        var roll = getParticleRoll(isCriticalHit, isSprintHit);
        sendParticles(partialType, position.x, position.y, position.z, 0, 0.0, roll, 0.0, 1.0);
    }

    private static SimpleParticleType getParticleType() {
        var location = "minecraft:sweep_attack";
        return (SimpleParticleType) ForgeRegistries.PARTICLE_TYPES.getValue(ResourceLocation.tryParse(location));
    }

    private static double getParticleRoll(boolean isCriticalHit, boolean isSprintHit) {
        return isCriticalHit || isSprintHit ? -Mth.HALF_PI : 0.0;
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

    @OnlyIn(Dist.CLIENT)
    @Mod(value = AliveCombat.MODID)
    public static class AttackParticleConfig {
        public AttackParticleConfig(IEventBus modEventBus) {
            modEventBus.register(AttackParticleConfig.class);
        }

        @SubscribeEvent
        public static void onModConfigLoading(ModConfigEvent.Loading event) {
            if (!MixinConfig.ATTACK_PARTICLE_ENABLED.get()) return;

            MinecraftForge.EVENT_BUS.register(AttackParticleClient.class);
        }
    }
}
