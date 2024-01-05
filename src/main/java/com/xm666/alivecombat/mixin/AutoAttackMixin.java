package com.xm666.alivecombat.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.xm666.alivecombat.handler.AutoAttackHandler;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

public class AutoAttackMixin {
    @Mixin(Minecraft.class)
    public static abstract class MinecraftMixin {
        @ModifyExpressionValue(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;consumeClick()Z", ordinal = 13))
        boolean modifyExpressionValue(boolean original) {
            if (original) {
                AutoAttackHandler.timer.start();
            }
            if (AutoAttackHandler.canAutoAttack()) {
                AutoAttackHandler.timer.stop();
                return true;
            }
            return original;
        }

        @WrapWithCondition(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;continueAttack(Z)V"))
        boolean canContinueAttack(Minecraft instance, boolean direction) {
            return AutoAttackHandler.canContinueAttack;
        }
    }
}
