package com.xm666.alivecombat.mixin.alivecombat;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.xm666.alivecombat.handler.IndicatorHandler;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;
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

        @WrapOperation(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIIIIIII)V"))
        private void wrapBlitSprite(GuiGraphics instance, RenderPipeline pipeline, Identifier sprite, int textureWidth, int textureHeight, int u, int v, int x, int y, int width, int height, Operation<Void> original, @Local float scale) {
            IndicatorHandler.blitSprite(instance, pipeline, sprite, textureWidth, textureHeight, u, v, x, y, scale * 17.0F, height);
        }
    }
}
