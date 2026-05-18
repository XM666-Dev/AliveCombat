package com.xm666.alivecombat.mixin.alivecombat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
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
        private float modifyAdjustTicks(float adjustTicks) {
            return adjustTicks + Minecraft.getInstance().getPartialTick();
        }
    }
}
