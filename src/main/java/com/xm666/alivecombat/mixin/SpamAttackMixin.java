package com.xm666.alivecombat.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

public class SpamAttackMixin {
    @Mixin(Minecraft.class)
    public static class MinecraftMixin {
        @ModifyExpressionValue(method = "startAttack", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;missTime:I", ordinal = 0))
        int getMissTime(int original) {
            return 0;
        }

        @Redirect(method = "startAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;resetAttackStrengthTicker()V"))
        void resetAttackStrengthTicker(LocalPlayer instance) {
        }
    }

    @Mixin(LocalPlayer.class)
    public static class LocalPlayerMixin {
        @Redirect(method = "swing", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V"))
        void send(ClientPacketListener instance, Packet packet) {
        }
    }

    @Mixin(MultiPlayerGameMode.class)
    public static class MultiPlayerGameModeMixin {
        @Redirect(method = "stopDestroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;resetAttackStrengthTicker()V"))
        void resetAttackStrengthTicker(LocalPlayer instance) {
        }
    }
}
