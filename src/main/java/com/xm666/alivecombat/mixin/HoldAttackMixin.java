package com.xm666.alivecombat.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.xm666.alivecombat.handler.HoldAttackHandler;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class HoldAttackMixin {
    @Mixin(Minecraft.class)
    public static abstract class MinecraftMixin {
        @Shadow
        public abstract void handleKeybinds();

        @ModifyExpressionValue(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z", ordinal = 0))
        private boolean modifyIsUsingItem(boolean using) {
            if (!using) {
                return false;
            }
            HoldAttackHandler.queueHandleUsing = !HoldAttackHandler.queueHandleUsing;
            return !HoldAttackHandler.queueHandleUsing;
        }

        @Inject(method = "handleKeybinds", at = @At(value = "TAIL"))
        private void handleUsing(CallbackInfo ci) {
            if (HoldAttackHandler.queueHandleUsing) {
                handleKeybinds();
            }
        }
    }
}
