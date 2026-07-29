package com.xm666.alivecombat.mixin.alivecombat;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.xm666.alivecombat.handler.IndicatorHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@OnlyIn(Dist.CLIENT)
public class SmoothIndicatorMixin {
    @Mixin(Gui.class)
    public static class GuiMixin {
        @ModifyArg(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getAttackStrengthScale(F)F"))
        private float modifyAdjustTicks(float adjustTicks) {
            var mc = Minecraft.getInstance();
            return adjustTicks + mc.getPartialTick();
        }

        @WrapOperation(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V", ordinal = 3))
        private void wrapBlitSprite(GuiGraphics instance, ResourceLocation sprite, int x, int y, int uPosition, int vPosition, int uWidth, int vHeight, Operation<Void> original, @Local float scale) {
            IndicatorHandler.blitSprite(instance, sprite, x, y, uPosition, vPosition, scale * 17.0F, vHeight);
        }
    }
}
