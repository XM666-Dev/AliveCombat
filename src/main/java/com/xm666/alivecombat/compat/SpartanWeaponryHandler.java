package com.xm666.alivecombat.compat;

import net.minecraft.world.item.ItemStack;
import org.xiyu.spartanweaponryunofficial.api.ModToolActions;

public class SpartanWeaponryHandler {
    public static boolean canMeleeBlock(ItemStack stack) {
        return stack.canPerformAction(ModToolActions.MELEE_BLOCK);
    }
}
