package com.xm666.alivecombat.handler;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class HeldAttackHandler {
    public static boolean heldAttack = false;

    public static boolean isUsingBlocking(Entity entity) {
        if (!(entity instanceof LivingEntity living) || !living.isUsingItem()) return false;

        var stack = living.getUseItem();
        return stack.has(DataComponents.BLOCKS_ATTACKS);
    }
}
