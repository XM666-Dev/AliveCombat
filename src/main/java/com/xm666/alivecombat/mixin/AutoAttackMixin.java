package com.xm666.alivecombat.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.xm666.alivecombat.handler.AutoAttackHandler;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

public class AutoAttackMixin {
    @Mixin(Minecraft.class)
    private static class MinecraftMixin {
        @ModifyExpressionValue(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;consumeClick()Z", ordinal = 13))
        private boolean modifyConsumeClick(boolean clicked, @Share("autoAttacked") LocalBooleanRef autoAttacked) {
            if (clicked) {
                AutoAttackHandler.AutoAttackHandlerConfig.update();
                AutoAttackHandler.timer.start();
            }
            if (AutoAttackHandler.canAutoAttack() && !autoAttacked.get()) {
                AutoAttackHandler.timer.stop();
                clicked = true;
                autoAttacked.set(true);
            }
            return clicked;
        }

        @WrapWithCondition(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;continueAttack(Z)V"))
        private boolean canContinueAttack(Minecraft instance, boolean direction) {
            return AutoAttackHandler.canContinueAttack;
        }
    }
}
