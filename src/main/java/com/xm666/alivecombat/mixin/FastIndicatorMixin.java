package com.xm666.alivecombat.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

public class FastIndicatorMixin {
    @Mixin(Gui.class)
    public static class GuiMixin {
        @ModifyArg(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getAttackStrengthScale(F)F"))
        private float modifyAdjustTicks(float adjustTicks) {
            return adjustTicks + Minecraft.getInstance().getPartialTick();
        }
    }
}
