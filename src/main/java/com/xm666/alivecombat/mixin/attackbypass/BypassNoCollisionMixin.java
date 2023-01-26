package com.xm666.alivecombat.mixin.attackbypass;

import com.xm666.alivecombat.AliveCombatConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class BypassNoCollisionMixin {
    @Shadow
    @Final
    Minecraft minecraft;

    public BlockHitResult pick(double p_19908_, float p_19909_, boolean p_19910_, Entity entity) {
        Vec3 vec3 = entity.getEyePosition(p_19909_);
        Vec3 vec31 = entity.getViewVector(p_19909_);
        Vec3 vec32 = vec3.add(vec31.x * p_19908_, vec31.y * p_19908_, vec31.z * p_19908_);
        return entity.getLevel().clip(new ClipContext(vec3, vec32, ClipContext.Block.COLLIDER, p_19910_ ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE, entity));
    }

    @Inject(method = "pick", at = @At("TAIL"))
    public void onPick(float p_109088_, CallbackInfo ci) {
        if (minecraft.hitResult instanceof BlockHitResult) {
            Entity entity = minecraft.getCameraEntity();
            if (entity != null && this.minecraft.level != null) {
                double d0 = minecraft.gameMode.getPickRange();
                BlockHitResult hitResult = pick(d0, p_109088_, false, entity);
                Vec3 vec3 = entity.getEyePosition(p_109088_);
                double d1;
                double atkRange = minecraft.player.getAttackRange();
                d0 = d1 = Math.max(d0, atkRange);

                d1 *= d1;
                if (hitResult != null)
                    d1 = hitResult.getLocation().distanceToSqr(vec3);

                Vec3 vec31 = entity.getViewVector(1.0F);
                Vec3 vec32 = vec3.add(vec31.x * d0, vec31.y * d0, vec31.z * d0);
                AABB aabb = entity.getBoundingBox().expandTowards(vec31.scale(d0)).inflate(1.0D, 1.0D, 1.0D);
                EntityHitResult entityhitresult;
                if (AliveCombatConfig.attackBypass.get("bypassDead"))
                    entityhitresult = ProjectileUtil.getEntityHitResult(entity, vec3, vec32, aabb,
                            p_234237_ -> !p_234237_.isSpectator() && p_234237_.isPickable() && p_234237_.isAlive(), d1);
                else entityhitresult = ProjectileUtil.getEntityHitResult(entity, vec3, vec32, aabb,
                        p_234237_ -> !p_234237_.isSpectator() && p_234237_.isPickable(), d1);
                if (entityhitresult != null) {
                    Entity entity1 = entityhitresult.getEntity();
                    Vec3 vec33 = entityhitresult.getLocation();
                    double d2 = vec3.distanceToSqr(vec33);
                    if (d2 < d1 || hitResult == null) {
                        minecraft.hitResult = entityhitresult;
                        if (entity1 instanceof LivingEntity || entity1 instanceof ItemFrame)
                            minecraft.crosshairPickEntity = entity1;
                    }
                }
            }
        }
    }
}