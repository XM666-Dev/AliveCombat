package com.xm666.alivecombat.handler;

import com.xm666.alivecombat.compat.SpartanWeaponryHandler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.ItemAbilities;

public class HeldAttackHandler {
    public static boolean heldAttack = false;

    public static boolean isUsingBlocking(Entity entity) {
        if (!(entity instanceof LivingEntity living) || !living.isUsingItem()) return false;

        var stack = living.getUseItem();
        if (stack.isEmpty()) return false;

        return stack.canPerformAction(ItemAbilities.SHIELD_BLOCK)
                || ModList.get().isLoaded("spartan_weaponry_unofficial")
                && SpartanWeaponryHandler.canMeleeBlock(stack);
    }
}
