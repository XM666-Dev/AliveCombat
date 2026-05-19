package com.xm666.alivecombat.mixin.alivecombat;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;

@OnlyIn(Dist.CLIENT)
public class AttackParticleMixin {
    @Mixin(ClientPacketListener.class)
    private static class ClientPacketListenerMixin {
        @WrapMethod(method = "handleParticleEvent")
        private void wrapHandleParticleEvent(ClientboundLevelParticlesPacket packet, Operation<Void> original) {
            if (packet.getParticle().getType() == ParticleTypes.SWEEP_ATTACK) return;

            original.call(packet);
        }
    }
}
