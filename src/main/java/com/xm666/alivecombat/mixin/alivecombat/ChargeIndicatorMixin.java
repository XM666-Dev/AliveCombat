package com.xm666.alivecombat.mixin.alivecombat;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.xm666.alivecombat.handler.IndicatorHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@OnlyIn(Dist.CLIENT)
public class ChargeIndicatorMixin {
    @Mixin(Gui.class)
    public static class GuiMixin {
        @WrapOperation(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getAttackStrengthScale(F)F"))
        private float wrapAttackStrengthScale(LocalPlayer instance, float adjustTicks, Operation<Float> original) {
            var mc = Minecraft.getInstance();
            var originalScale = original.call(instance, adjustTicks);
            if (mc.crosshairPickEntity instanceof LivingEntity && mc.crosshairPickEntity.isAlive() &&
                    originalScale >= 1.0F && instance.getCurrentItemAttackStrengthDelay() > 5.0F ||
                    originalScale < 1.0F)
                return originalScale;

            return IndicatorHandler.getChargeScale(instance, adjustTicks);
        }
    }
}
