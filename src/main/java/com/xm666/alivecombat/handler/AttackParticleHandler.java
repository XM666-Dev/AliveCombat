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
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;

public class AttackParticleHandler {
    private static final RandomSource random = RandomSource.create();

    private static void sweepAttack() {
        var player = Minecraft.getInstance().player;
        var xOffset = -Mth.sin(player.getYRot() * Mth.DEG_TO_RAD);
        var zOffset = Mth.cos(player.getYRot() * Mth.DEG_TO_RAD);
        player.level().playSound(player, player.getX(), player.getY(), player.getZ(), getSoundEvent(), player.getSoundSource(), 1.0F, 1.0F);
        sendParticles(getParticleType(), player.getX() + xOffset, player.getY(0.5), player.getZ() + zOffset, 0, xOffset, 0.0, zOffset, 0.0);
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

    private static SoundEvent getSoundEvent() {
        var type = "entity.player.attack.sweep";
        return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.tryParse(type));
    }

    private static SimpleParticleType getParticleType() {
        var type = "minecraft:sweep_attack";
        return (SimpleParticleType) BuiltInRegistries.PARTICLE_TYPE.get(ResourceLocation.tryParse(type));
    }

    public static class AttackParticleClient {
        @SubscribeEvent
        public static void onAttackEntity(AttackEntityEvent event) {
            var player = event.getEntity();
            if (!player.isLocalPlayer()) return;

            var weapon = player.getWeaponItem();
            if (!weapon.is(Tags.Items.MELEE_WEAPON_TOOLS)) return;

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
            if (!MixinConfig.ATTACK_PARTICLE_ENABLED.get()) return;

            NeoForge.EVENT_BUS.register(AttackParticleClient.class);
        }
    }
}
