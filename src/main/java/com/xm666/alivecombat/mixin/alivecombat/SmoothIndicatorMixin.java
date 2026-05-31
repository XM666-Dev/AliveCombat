package com.xm666.alivecombat.mixin.alivecombat;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.xm666.alivecombat.handler.IndicatorHandler;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@OnlyIn(Dist.CLIENT)
public class SmoothIndicatorMixin {
    @Mixin(Gui.class)
    public static class GuiMixin {
        @ModifyArg(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getAttackStrengthScale(F)F"))
        private float modifyAdjustTicks(float adjustTicks, @Local(argsOnly = true) DeltaTracker deltaTracker) {
            return adjustTicks + deltaTracker.getGameTimeDeltaPartialTick(true);
        }

        @WrapOperation(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIIIIIII)V"))
        private void wrapBlitSprite(GuiGraphics instance, ResourceLocation sprite, int textureWidth, int textureHeight, int uPosition, int vPosition, int x, int y, int uWidth, int vHeight, Operation<Void> original, @Local float scale) {
            IndicatorHandler.blitSprite(instance, sprite, textureWidth, textureHeight, uPosition, vPosition, x, y, scale * 17.0F, vHeight);
        }
    }
}
