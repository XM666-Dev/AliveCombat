package com.xm666.alivecombat.compat;

import com.oblivioussp.spartanweaponry.api.ModToolActions;
import net.minecraft.world.item.ItemStack;

public class SpartanWeaponryHandler {
    public static boolean canMeleeBlock(ItemStack stack) {
        return stack.canPerformAction(ModToolActions.MELEE_BLOCK);
    }
}
