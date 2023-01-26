package com.xm666.alivecombat.event;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BloodSplashEvent {
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        if (!(target.getLevel() instanceof ClientLevel)) return;
        DamageSource source = event.getSource();
        if (source.getMsgId().equals("player") && source.getClass() == EntityDamageSource.class) {
            Minecraft minecraft = Minecraft.getInstance();
            Entity attacker = source.getEntity();
            if (attacker != minecraft.player) return;
            if (minecraft.hitResult instanceof EntityHitResult entityHit && entityHit.getEntity() == target) {
                Vec3 vec = entityHit.getLocation();
                for (byte i = 0; i < event.getAmount(); i++) {
                    LogUtils.getLogger().info("" + event.getAmount());
                    minecraft.level.addParticle(ParticleTypes.ITEM_SNOWBALL, vec.x, vec.y, vec.z, 0, 0, 0);
                    //minecraft.level.addDestroyBlockEffect();
                }
            }
        }
    }
}
