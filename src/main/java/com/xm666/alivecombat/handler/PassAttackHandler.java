package com.xm666.alivecombat.handler;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class PassAttackHandler {
    public static HitResult pickCollider(Entity entity, double reach, float partialTick, boolean clipFluid) {
        Vec3 vec3 = entity.getEyePosition(partialTick);
        Vec3 vec31 = entity.getViewVector(partialTick);
        Vec3 vec32 = vec3.add(vec31.x * reach, vec31.y * reach, vec31.z * reach);
        return entity.level().clip(new ClipContext(vec3, vec32, ClipContext.Block.COLLIDER, clipFluid ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE, entity));
    }
}
