package com.xm666.alivecombat.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

public class NoMissMixin {
    @Mixin(LocalPlayer.class)
    public static class LocalPlayerMixin {
        @Redirect(method = "swing", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V"))
        public void send(ClientPacketListener instance, Packet<?> p_104956_) {
        }
    }

    @Mixin(Minecraft.class)
    public static class MinecraftMixin {
        @Redirect(method = "startAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;resetAttackStrengthTicker()V"))
        public void resetAttackStrengthTicker(LocalPlayer instance) {
        }
    }

    @Mixin(MultiPlayerGameMode.class)
    public static class MultiPlayerGameModeMixin {
        @Redirect(method = "stopDestroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;resetAttackStrengthTicker()V"))
        public void resetAttackStrengthTicker(LocalPlayer instance) {
        }
    }
}
