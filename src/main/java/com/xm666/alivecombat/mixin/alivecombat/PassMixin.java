package com.xm666.alivecombat.mixin.alivecombat;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.xm666.alivecombat.Config;
import com.xm666.alivecombat.handler.PassHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@OnlyIn(Dist.CLIENT)
public class PassMixin {
    private static class PassCollisionlessMixin {
        @Mixin(LocalPlayer.class)
        private static class LocalPlayerMixin {
            @ModifyReceiver(method = "pick(Lnet/minecraft/world/entity/Entity;DDF)Lnet/minecraft/world/phys/HitResult;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/HitResult;getLocation()Lnet/minecraft/world/phys/Vec3;"))
            private static HitResult modifyHitResult(HitResult instance, @Local(argsOnly = true) Entity entity, @Local(argsOnly = true, ordinal = 1) double entityInteractionRange, @Local(argsOnly = true) float partialTick) {
                if (!PassHandler.passEnabled) return instance;

                PassHandler.passCollisionless = true;
                var hitResult = entity.pick(entityInteractionRange, partialTick, false);
                PassHandler.passCollisionless = false;
                return hitResult;
            }
        }

        @Mixin(Entity.class)
        private static class EntityMixin {
            @WrapOperation(method = "pick", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/ClipContext$Block;OUTLINE:Lnet/minecraft/world/level/ClipContext$Block;", opcode = Opcodes.GETSTATIC))
            private ClipContext.Block wrapBlock(Operation<ClipContext.Block> original) {
                var entity = (Entity) (Object) this;
                return entity.level().isClientSide() && PassHandler.passCollisionless ? ClipContext.Block.COLLIDER : original.call();
            }
        }
    }

    private static class PassCollisionlessExtraMixin {
        @Mixin(LocalPlayer.class)
        private static class LocalPlayerMixin {
            @WrapOperation(method = "raycastHitResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;pick(Lnet/minecraft/world/entity/Entity;DDF)Lnet/minecraft/world/phys/HitResult;"))
            private HitResult wrapPick(Entity cameraEntity, double blockRange, double entityRange, float partialTick, Operation<HitResult> original) {
                if (PassHandler.passEnabled) {
                    if (Config.PASS_COLLISIONLESS_HOLDING_TOOL.get() && PassHandler.isHoldingTools(cameraEntity))
                        return original.call(cameraEntity, blockRange, entityRange, partialTick);
                    if (Config.PASS_COLLISIONLESS_INTERACTION_BLOCKED.get()) {
                        var passHitResult = original.call(cameraEntity, blockRange, entityRange, partialTick);
                        if (PassHandler.interacts(passHitResult)) return passHitResult;
                    }
                }

                PassHandler.passCollisionlessExtra = false;
                var originalHitResult = original.call(cameraEntity, blockRange, entityRange, partialTick);
                PassHandler.passCollisionlessExtra = true;
                return originalHitResult;
            }
        }

        @Mixin(Entity.class)
        private static class EntityMixin {
            @WrapOperation(method = "pick", at = @At(value = "NEW", target = "(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/level/ClipContext$Block;Lnet/minecraft/world/level/ClipContext$Fluid;Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/world/level/ClipContext;"))
            private ClipContext wrapClipContext(Vec3 from, Vec3 to, ClipContext.Block block, ClipContext.Fluid fluid, Entity entity, Operation<ClipContext> original) {
                return entity.level().isClientSide() && PassHandler.passCollisionlessExtra ? PassHandler.getPassClipContext(from, to, block, fluid, entity) : original.call(from, to, block, fluid, entity);
            }
        }
    }

    private static class PassDeadMixin {
        @Mixin(LocalPlayer.class)
        private static class LocalPlayerMixin {
            @WrapOperation(method = "raycastHitResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;pick(Lnet/minecraft/world/entity/Entity;DDF)Lnet/minecraft/world/phys/HitResult;"))
            private HitResult wrapPick(Entity cameraEntity, double blockRange, double entityRange, float partialTick, Operation<HitResult> original) {
                var passHitResult = original.call(cameraEntity, blockRange, entityRange, partialTick);
                if (!PassHandler.passEnabled || passHitResult.getType() != HitResult.Type.BLOCK) return passHitResult;

                PassHandler.passDead = false;
                var orignalHitResult = original.call(cameraEntity, blockRange, entityRange, partialTick);
                PassHandler.passDead = true;
                if (orignalHitResult.getType() != HitResult.Type.ENTITY) return passHitResult;

                return PassHandler.filterHitResult(passHitResult, cameraEntity.getEyePosition(partialTick));
            }
        }

        @Mixin(LivingEntity.class)
        private static class LivingEntityMixin {
            @ModifyReturnValue(method = "isPickable", at = @At(value = "RETURN"))
            private boolean modifyPickable(boolean original) {
                var living = (LivingEntity) (Object) this;
                if (!living.level().isClientSide() || !PassHandler.passEnabled || !PassHandler.passDead || !original)
                    return original;

                return living.isAlive();
            }
        }
    }

    private static class PassAllyMixin {
        @Mixin(LocalPlayer.class)
        private static class LocalPlayerMixin {
            @WrapOperation(method = "raycastHitResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;pick(Lnet/minecraft/world/entity/Entity;DDF)Lnet/minecraft/world/phys/HitResult;"))
            private HitResult wrapPick(Entity cameraEntity, double blockRange, double entityRange, float partialTick, Operation<HitResult> original) {
                if (PassHandler.passEnabled) {
                    if (Config.PASS_ALLY_HOLDING_TOOL.get() && PassHandler.isHoldingTools(cameraEntity))
                        return original.call(cameraEntity, blockRange, entityRange, partialTick);
                    if (Config.PASS_ALLY_INTERACTION_BLOCKED.get()) {
                        var passHitResult = original.call(cameraEntity, blockRange, entityRange, partialTick);
                        if (PassHandler.interacts(passHitResult)) return passHitResult;
                    }
                }

                PassHandler.passAlly = false;
                var originalHitResult = original.call(cameraEntity, blockRange, entityRange, partialTick);
                PassHandler.passAlly = true;
                return originalHitResult;
            }
        }

        @Mixin(LivingEntity.class)
        private static class LivingEntityMixin {
            @ModifyReturnValue(method = "isPickable", at = @At(value = "RETURN"))
            private boolean modifyPickable(boolean original) {
                var living = (LivingEntity) (Object) this;
                if (!living.level().isClientSide() || !PassHandler.passEnabled || !PassHandler.passAlly || !original)
                    return original;

                var mc = Minecraft.getInstance();
                var player = mc.player;
                return player != null && !living.isAlliedTo(player);
            }
        }
    }
}
