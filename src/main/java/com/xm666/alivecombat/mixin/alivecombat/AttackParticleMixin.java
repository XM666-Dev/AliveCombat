package com.xm666.alivecombat.mixin.alivecombat;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.particle.AttackSweepParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
public class AttackParticleMixin {
    @Mixin(AttackSweepParticle.Provider.class)
    private static class Provider {
        @ModifyReturnValue(method = "createParticle(Lnet/minecraft/core/particles/SimpleParticleType;Lnet/minecraft/client/multiplayer/ClientLevel;DDDDDD)Lnet/minecraft/client/particle/Particle;", at = @At("RETURN"))
        private Particle modifyParticle(Particle original, SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed) {
            original.roll = original.oRoll = (float) ySpeed;
            return original;
        }
    }

    @Mixin(ClientPacketListener.class)
    private static class ClientPacketListenerMixin {
        @WrapMethod(method = "handleParticleEvent")
        private void wrapHandleParticleEvent(ClientboundLevelParticlesPacket packet, Operation<Void> original) {
            if (packet.getParticle().getType() == ParticleTypes.SWEEP_ATTACK) return;

            original.call(packet);
        }
    }

    @Mixin(AttackSweepParticle.class)
    private static class AttackSweepParticleMixin {
        @Unique
        private Quaternionf alivecombat$initialRotation;

        @Inject(method = "<init>", at = @At("TAIL"))
        private void onInit(ClientLevel level, double x, double y, double z, double quadSizeMultiplier, SpriteSet sprites, CallbackInfo ci) {
            var mc = Minecraft.getInstance();
            var camera = mc.gameRenderer.getMainCamera();
            alivecombat$initialRotation = new Quaternionf(camera.rotation());
        }

        @Unique
        public SingleQuadParticle.FacingCameraMode getFacingCameraMode() {
            return (quaternionf, camera, partialTick) -> quaternionf.set(alivecombat$initialRotation);
        }
    }
}
