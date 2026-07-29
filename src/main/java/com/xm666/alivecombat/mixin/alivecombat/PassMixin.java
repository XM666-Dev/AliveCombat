package com.xm666.alivecombat.mixin.alivecombat;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.xm666.alivecombat.Config;
import com.xm666.alivecombat.handler.PassHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@OnlyIn(Dist.CLIENT)
public class PassMixin {
    private static class PassCollisionlessMixin {
        @Mixin(GameRenderer.class)
        private static class GameRendererMixin {
            @ModifyVariable(method = "pick", at = @At(value = "LOAD", ordinal = 1), name = "d1")
            private double modifyDistanceSquare(double distanceSquare, @Local(argsOnly = true) float partialTick, @Local Entity entity, @Local(name = "entityReach") double entityInteractionRange, @Local Vec3 eyePosition) {
                if (!PassHandler.passEnabled) return distanceSquare;

                PassHandler.passCollisionless = true;
                var hitResult = entity.pick(entityInteractionRange, partialTick, false);
                PassHandler.passCollisionless = false;
                return hitResult.getLocation().distanceToSqr(eyePosition);
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
        @Mixin(GameRenderer.class)
        private static class GameRendererMixin {
            @WrapMethod(method = "pick")
            private void wrapPick(float partialTick, Operation<Void> original) {
                var mc = Minecraft.getInstance();
                var entity = mc.getCameraEntity();
                if (entity == null) {
                    original.call(partialTick);
                    return;
                }

                if (PassHandler.passEnabled) {
                    if (Config.PASS_COLLISIONLESS_HOLDING_TOOL.get() && PassHandler.isHoldingTools(entity)) {
                        mc.hitResult = PassHandler.callQueried(partialTick, original);
                        return;
                    }
                    if (Config.PASS_COLLISIONLESS_INTERACTION_BLOCKED.get()) {
                        var passHitResult = PassHandler.callQueried(partialTick, original);
                        if (PassHandler.interacts(passHitResult)) {
                            mc.hitResult = passHitResult;
                            return;
                        }
                    }
                }

                PassHandler.passCollisionlessExtra = false;
                mc.hitResult = PassHandler.callQueried(partialTick, original);
                PassHandler.passCollisionlessExtra = true;
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
        @Mixin(GameRenderer.class)
        private static class GameRendererMixin {
            @WrapMethod(method = "pick")
            private void wrapPick(float partialTick, Operation<Void> original) {
                var mc = Minecraft.getInstance();
                var entity = mc.getCameraEntity();
                if (entity == null) {
                    original.call(partialTick);
                    return;
                }

                var passHitResult = PassHandler.callQueried(partialTick, original);
                if (!PassHandler.passEnabled || passHitResult.getType() != HitResult.Type.BLOCK) {
                    mc.hitResult = passHitResult;
                    return;
                }

                PassHandler.passDead = false;
                var orignalHitResult = PassHandler.callQueried(partialTick, original);
                PassHandler.passDead = true;
                if (orignalHitResult.getType() != HitResult.Type.ENTITY) {
                    mc.hitResult = passHitResult;
                    return;
                }

                mc.hitResult = PassHandler.filterHitResult(passHitResult, entity.getEyePosition(partialTick));
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
        @Mixin(GameRenderer.class)
        private static class GameRendererMixin {
            @WrapMethod(method = "pick")
            private void wrapPick(float partialTick, Operation<Void> original) {
                var mc = Minecraft.getInstance();
                var entity = mc.getCameraEntity();
                if (entity == null) {
                    original.call(partialTick);
                    return;
                }

                if (PassHandler.passEnabled) {
                    if (Config.PASS_ALLY_HOLDING_TOOL.get() && PassHandler.isHoldingTools(entity)) {
                        mc.hitResult = PassHandler.callQueried(partialTick, original);
                        return;
                    }
                    if (Config.PASS_ALLY_INTERACTION_BLOCKED.get()) {
                        var passHitResult = PassHandler.callQueried(partialTick, original);
                        if (PassHandler.interacts(passHitResult)) {
                            mc.hitResult = passHitResult;
                            return;
                        }
                    }
                }

                PassHandler.passAlly = false;
                mc.hitResult = PassHandler.callQueried(partialTick, original);
                PassHandler.passAlly = true;
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
