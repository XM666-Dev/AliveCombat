package com.xm666.alivecombat.handler;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class IndicatorHandler {
    public static float getChargeScale(LivingEntity living, float adjustTicks) {
        if (!living.isUsingItem()) return 1.0F;

        var item = living.getUseItem();
        var chargeDuration = getChargeDuration(item, living);
        if (chargeDuration == Integer.MAX_VALUE) return 1.0F;

        var usingTicks = item.getUseDuration(living) - living.getUseItemRemainingTicks();
        return Mth.clamp((usingTicks + adjustTicks) / chargeDuration, 0.0F, 1.0F);
    }

    private static int getChargeDuration(ItemStack stack, LivingEntity living) {
        switch (stack.getItem()) {
            case BowItem ignored -> {
                return 20;
            }
            case CrossbowItem ignored -> {
                var f = EnchantmentHelper.modifyCrossbowChargingTime(stack, living, 1.25F);
                return Mth.floor(f * 20.0F);
            }
            case TridentItem ignored -> {
                return 10;
            }
            default -> {
                return Integer.MAX_VALUE;
            }
        }
    }
}
