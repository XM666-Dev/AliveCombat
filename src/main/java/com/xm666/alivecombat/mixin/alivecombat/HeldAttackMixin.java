package com.xm666.alivecombat.mixin.alivecombat;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.xm666.alivecombat.handler.HeldAttackHandler;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
public class HeldAttackMixin {
    @Mixin(Minecraft.class)
    public static abstract class MinecraftMixin {
        @Shadow
        public abstract void handleKeybinds();

        @ModifyExpressionValue(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z", ordinal = 0))
        private boolean modifyUsingItem(boolean using) {
            if (!using) return false;

            var mc = Minecraft.getInstance();
            if (HeldAttackHandler.isUsingBlocking(mc.player)) return true;

            HeldAttackHandler.heldAttack = !HeldAttackHandler.heldAttack;
            return !HeldAttackHandler.heldAttack;
        }

        @Inject(method = "handleKeybinds", at = @At(value = "TAIL"))
        private void onHandleKeybinds(CallbackInfo ci) {
            if (!HeldAttackHandler.heldAttack) return;

            handleKeybinds();
        }
    }
}
