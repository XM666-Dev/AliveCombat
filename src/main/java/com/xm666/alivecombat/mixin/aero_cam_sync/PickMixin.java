package com.xm666.alivecombat.mixin.aero_cam_sync;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.playsi.aero_cam_sync.client.config.Config;
import com.playsi.aero_cam_sync.client.utils.CameraController;
import com.playsi.aero_cam_sync.client.utils.LevelClipMixinState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class PickMixin {
    @Mixin(value = GameRenderer.class, priority = 2000)
    private static class GameRendererMixin {
        @TargetHandler(mixin = "com.playsi.aero_cam_sync.mixins.client.GameRendererPickMixin", name = "recalculateTiltedPick")
        @WrapMethod(method = "@MixinSquared:Handler")
        private void wrapTiltedPick(float partialTick, CallbackInfo ci, Operation<Void> original) {
        }

        @WrapOperation(method = "pick(Lnet/minecraft/world/entity/Entity;DDF)Lnet/minecraft/world/phys/HitResult;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getEyePosition(F)Lnet/minecraft/world/phys/Vec3;"))
        private Vec3 wrapEyePosition(Entity instance, float partialTicks, Operation<Vec3> original, @Share("inTilt") LocalBooleanRef inTilt) {
            if (!Config.isLoaded() || !Config.MOD_ENABLED.get() || !CameraController.shouldApplyTilt() || !Config.MODIFY_CAMERA_POS.get())
                return original.call(instance, partialTicks);

            var mc = Minecraft.getInstance();
            if (!mc.options.getCameraType().isFirstPerson() || mc.player == null || mc.level == null)
                return original.call(instance, partialTicks);

            var hitResult = mc.hitResult;
            if (hitResult != null && hitResult.getType() == HitResult.Type.ENTITY)
                return original.call(instance, partialTicks);

            inTilt.set(true);
            return mc.gameRenderer.getMainCamera().getPosition();
        }

        @WrapOperation(method = "pick(Lnet/minecraft/world/entity/Entity;DDF)Lnet/minecraft/world/phys/HitResult;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;pick(DFZ)Lnet/minecraft/world/phys/HitResult;"))
        private HitResult wrapEntityPick(Entity instance, double hitDistance, float partialTicks, boolean hitFluids, Operation<HitResult> original, @Share("inTilt") LocalBooleanRef inTilt) {
            if (!inTilt.get()) return original.call(instance, hitDistance, partialTicks, hitFluids);

            LevelClipMixinState.inTiltedClip = true;
            var result = original.call(instance, hitDistance, partialTicks, hitFluids);
            LevelClipMixinState.inTiltedClip = false;
            return result;
        }
    }
}
