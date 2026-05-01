package com.xm666.alivecombat.handler;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;

import java.util.Optional;

public class IndicatorHandler {
    public static float getChargeScale(LivingEntity living, float adjustTicks) {
        if (!living.isUsingItem()) return 1.0F;

        var item = living.getUseItem();
        var optionalChargeDuration = getChargeDuration(item, living);
        if (optionalChargeDuration.isEmpty()) return 1.0F;

        var usingTicks = item.getUseDuration() - living.getUseItemRemainingTicks();
        var chargeDuration = optionalChargeDuration.get();
        return Mth.clamp((usingTicks + adjustTicks) / chargeDuration, 0.0F, 1.0F);
    }

    private static Optional<Integer> getChargeDuration(ItemStack stack, LivingEntity living) {
        return Optional.ofNullable(
                switch (stack.getItem()) {
                    case BowItem ignored -> 20;
                    case CrossbowItem ignored -> CrossbowItem.getChargeDuration(stack);
                    case TridentItem ignored -> 10;
                    default -> null;
                });
    }
}
