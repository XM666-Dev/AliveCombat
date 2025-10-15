package com.xm666.alivecombat.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.Packet;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

public class SpamAttackMixin {
    @Mixin(LocalPlayer.class)
    private static class LocalPlayerMixin {
        @Redirect(method = "swing", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V"))
        private void redirectSend(ClientPacketListener instance, Packet<?> packet) {
        }
    }

    @Mixin(Minecraft.class)
    private static class MinecraftMixin {
        @Redirect(method = "startAttack", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/client/Minecraft;missTime:I", ordinal = 1))
        private void redirectMissTime(Minecraft instance, int value) {
        }

        @Redirect(method = "startAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;resetAttackStrengthTicker()V"))
        private void redirectResetAttackStrengthTicker(LocalPlayer instance) {
        }
    }

    @Mixin(MultiPlayerGameMode.class)
    private static class MultiPlayerGameModeMixin {
        @Redirect(method = "stopDestroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;resetAttackStrengthTicker()V"))
        private void redirectResetAttackStrengthTicker(LocalPlayer instance) {
        }
    }
}
