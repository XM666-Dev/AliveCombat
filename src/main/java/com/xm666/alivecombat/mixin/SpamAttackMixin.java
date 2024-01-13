package com.xm666.alivecombat.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

public class SpamAttackMixin {
    @Mixin(Minecraft.class)
    public static class MinecraftMixin {
        @ModifyExpressionValue(method = "startAttack", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;missTime:I", ordinal = 0))
        int modifyMissTime(int original) {
            return 0;
        }

        @WrapOperation(method = "startAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;resetAttackStrengthTicker()V"))
        void resetAttackStrengthTicker(LocalPlayer instance, Operation<Void> original) {
        }
    }

    @Mixin(LocalPlayer.class)
    public static class LocalPlayerMixin {
        @WrapOperation(method = "swing", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V"))
        void send(ClientPacketListener instance, Packet<?> packet, Operation<Void> original) {
        }
    }

    @Mixin(MultiPlayerGameMode.class)
    public static class MultiPlayerGameModeMixin {
        @WrapOperation(method = "stopDestroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;resetAttackStrengthTicker()V"))
        void resetAttackStrengthTicker(LocalPlayer instance, Operation<Void> original) {
        }
    }
}
