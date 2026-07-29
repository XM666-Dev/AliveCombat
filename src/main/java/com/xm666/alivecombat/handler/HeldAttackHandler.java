package com.xm666.alivecombat.handler;

import com.xm666.alivecombat.compat.SpartanWeaponryHandler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.fml.ModList;

public class HeldAttackHandler {
    public static boolean heldAttack = false;

    public static boolean isUsingBlocking(Entity entity) {
        if (!(entity instanceof LivingEntity living) || !living.isUsingItem()) return false;

        var stack = living.getUseItem();
        if (stack.isEmpty()) return false;

        return stack.canPerformAction(ToolActions.SHIELD_BLOCK)
                || ModList.get().isLoaded("spartanweaponry")
                && SpartanWeaponryHandler.canMeleeBlock(stack);
    }
}
