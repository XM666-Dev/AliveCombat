package com.xm666.alivecombat.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class FullAttackMixin {
    @Mixin(Minecraft.class)
    public static class MinecraftMixin {
        @Inject(method = "startAttack", at = @At("HEAD"), cancellable = true)
        void onStartAttack(CallbackInfoReturnable<Boolean> cir) {
            var mc = Minecraft.getInstance();
            var flag = mc.player != null && mc.player.getAttackStrengthScale(0.0F) != 1.0F;
            if (flag) cir.setReturnValue(false);
        }
    }
}
