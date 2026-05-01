package com.xm666.alivecombat.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.xm666.alivecombat.handler.AutoAttackHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@OnlyIn(Dist.CLIENT)
public class AutoAttackMixin {
    @Mixin(Minecraft.class)
    private static class MinecraftMixin {
        @ModifyExpressionValue(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;consumeClick()Z", ordinal = 14))
        private boolean modifyConsumeClick(boolean clicked, @Share("autoAttacked") LocalBooleanRef autoAttacked) {
            if (clicked) {
                var mc = Minecraft.getInstance();
                if (!(mc.hitResult instanceof EntityHitResult)) {
                    AutoAttackHandler.timer.start();
                }
                return true;
            }

            if (!autoAttacked.get() && AutoAttackHandler.readyAttack()) {
                autoAttacked.set(true);
                return true;
            }

            return false;
        }

        @WrapWithCondition(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;continueAttack(Z)V"))
        private boolean wrapContinueAttack(Minecraft instance, boolean direction) {
            return AutoAttackHandler.canContinueAttack;
        }
    }

    @Mixin(MultiPlayerGameMode.class)
    private static class MultiPlayerGameModeMixin {
        @Inject(method = "destroyBlock", at = @At(value = "RETURN", ordinal = 4))
        private void onDestroyBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
            AutoAttackHandler.timer.stop();
        }
    }
}
