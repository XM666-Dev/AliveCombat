package com.xm666.alivecombat.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.xm666.alivecombat.handler.PassAttackHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.function.Predicate;

public class PassAttackMixin {
    public static class PassNoColliderMixin {
        @Mixin(GameRenderer.class)
        public static class GameRendererMixin {
            @ModifyVariable(method = "pick", at = @At(value = "LOAD", ordinal = 1), name = "d1")
            double modifyBlockDistanceSqr(double blockDistanceSqr, @Local(ordinal = 0) float partialTick, @Local(ordinal = 0) Entity cameraEntity, @Local(name = "entityReach") double entityReach, @Local(ordinal = 0) Vec3 eyePosition) {
                var hitResult = PassAttackHandler.pickCollider(cameraEntity, entityReach, partialTick, false);
                return hitResult.getLocation().distanceToSqr(eyePosition);
            }
        }
    }

    public static class PassDeadMixin {
        @Mixin(GameRenderer.class)
        public static class GameRendererMixin {
            @ModifyArg(method = "pick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/ProjectileUtil;getEntityHitResult(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;D)Lnet/minecraft/world/phys/EntityHitResult;"), index = 4)
            Predicate<Entity> modifyPredicate(Predicate<Entity> predicate) {
                return predicate.and(Entity::isAlive);
            }
        }
    }

    public static class PassAllyMixin {
        @Mixin(GameRenderer.class)
        public static class GameRendererMixin {
            @ModifyArg(method = "pick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/ProjectileUtil;getEntityHitResult(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;D)Lnet/minecraft/world/phys/EntityHitResult;"), index = 4)
            Predicate<Entity> modifyPredicate(Predicate<Entity> predicate) {
                var mc = Minecraft.getInstance();
                return predicate.and((entity) -> mc.options.keyShift.isDown() || mc.player == null || !entity.isAlliedTo(mc.player) || mc.options.keyUse.isDown());
            }
        }
    }
}
