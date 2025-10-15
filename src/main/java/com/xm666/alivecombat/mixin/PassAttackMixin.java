package com.xm666.alivecombat.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.xm666.alivecombat.handler.PassAttackHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.function.Predicate;

public class PassAttackMixin {
    private static class PassCollisionlessMixin {
        @Mixin(GameRenderer.class)
        private static class GameRendererMixin {
            @ModifyVariable(method = "pick(Lnet/minecraft/world/entity/Entity;DDF)Lnet/minecraft/world/phys/HitResult;", at = @At(value = "STORE"), name = "d2")
            private double modifyBlockDistanceSquare(double blockDistanceSquare, @Local(argsOnly = true) Entity entity, @Local(argsOnly = true, ordinal = 1) double entityInteractionRange, @Local(argsOnly = true) float partialTick, @Local Vec3 eyePosition) {
                PassAttackHandler.passCollisionless = true;
                var hitresult = entity.pick(entityInteractionRange, partialTick, false);
                PassAttackHandler.passCollisionless = false;
                return hitresult.getLocation().distanceToSqr(eyePosition);
            }
        }

        @Mixin(Entity.class)
        private static class EntityMixin {
            @WrapOperation(method = "pick", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/ClipContext$Block;OUTLINE:Lnet/minecraft/world/level/ClipContext$Block;"))
            private ClipContext.Block wrapBlock(Operation<ClipContext.Block> original) {
                return PassAttackHandler.passCollisionless ? ClipContext.Block.COLLIDER : original.call();
            }
        }
    }

    private static class PassDeadMixin {
        @Mixin(GameRenderer.class)
        private static class GameRendererMixin {
            @ModifyArg(method = "pick(Lnet/minecraft/world/entity/Entity;DDF)Lnet/minecraft/world/phys/HitResult;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/ProjectileUtil;getEntityHitResult(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;D)Lnet/minecraft/world/phys/EntityHitResult;"), index = 4)
            private Predicate<Entity> modifyPredicate(Predicate<Entity> predicate) {
                return predicate.and(Entity::isAlive);
            }
        }
    }

    private static class PassAllyMixin {
        @Mixin(GameRenderer.class)
        private static class GameRendererMixin {
            @ModifyArg(method = "pick(Lnet/minecraft/world/entity/Entity;DDF)Lnet/minecraft/world/phys/HitResult;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/ProjectileUtil;getEntityHitResult(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;D)Lnet/minecraft/world/phys/EntityHitResult;"), index = 4)
            private Predicate<Entity> modifyPredicate(Predicate<Entity> predicate) {
                var player = Minecraft.getInstance().player;
                if (player != null && !player.isShiftKeyDown()) {
                    return predicate.and(entity -> !entity.isAlliedTo(player));
                }
                return predicate;
            }
        }
    }
}
