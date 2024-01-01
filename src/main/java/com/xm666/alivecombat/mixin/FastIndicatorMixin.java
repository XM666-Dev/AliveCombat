package com.xm666.alivecombat.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

public class FastIndicatorMixin {
    @Mixin(Gui.class)
    public static class GuiMixin {
        @ModifyExpressionValue(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getAttackStrengthScale(F)F"))
        float getAttackStrengthScale(float original) {
            var mc = Minecraft.getInstance();
            if (mc.player == null) return original;
            return original + mc.getPartialTick() / mc.player.getCurrentItemAttackStrengthDelay();
        }
    }
}
