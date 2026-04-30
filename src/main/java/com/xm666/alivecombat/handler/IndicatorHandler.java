package com.xm666.alivecombat.handler;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;

public class IndicatorHandler {
    public static float getChargeScale(LivingEntity living, float adjustTicks) {
        if (!living.isUsingItem()) return 1.0F;

        var item = living.getUseItem();
        var chargeDuration = getChargeDuration(item, living);
        if (chargeDuration == Integer.MAX_VALUE) return 1.0F;

        var usingTicks = item.getUseDuration() - living.getUseItemRemainingTicks();
        return Mth.clamp((usingTicks + adjustTicks) / chargeDuration, 0.0F, 1.0F);
    }

    private static int getChargeDuration(ItemStack stack, LivingEntity living) {
        switch (stack.getItem()) {
            case BowItem ignored -> {
                return 20;
            }
            case CrossbowItem ignored -> {
                return CrossbowItem.getChargeDuration(stack);
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
