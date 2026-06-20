package com.xm666.alivecombat.handler;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.ItemAbilities;

public class HeldAttackHandler {
    public static boolean heldAttack = false;

    public static boolean isUsingBlocking(Entity entity) {
        if (!(entity instanceof LivingEntity living) || !living.isUsingItem()) return false;

        var stack = living.getUseItem();
        return !stack.isEmpty() && stack.canPerformAction(ItemAbilities.SHIELD_BLOCK);
    }
}
