package com.xm666.alivecombat.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

public class FastIndicatorMixin {
    @Mixin(Gui.class)
    public static class GuiMixin {
        @ModifyVariable(method = "renderCrosshair", at = @At(value = "LOAD", ordinal = 2), name = "f")
        float modifyVariable(float value) {
            var mc = Minecraft.getInstance();
            if (mc.player == null) return value;
            return value + mc.getPartialTick() / mc.player.getCurrentItemAttackStrengthDelay();
        }
    }
}
