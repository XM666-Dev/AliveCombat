package com.xm666.alivecombat.mixin.shouldersurfing;

import com.github.exopandora.shouldersurfing.api.client.world.phys.PickContext;
import com.github.exopandora.shouldersurfing.client.world.phys.ObjectPicker;
import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.xm666.alivecombat.Config;
import com.xm666.alivecombat.handler.PassHandler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@OnlyIn(Dist.CLIENT)
public class PassMixin {
    private static class PassCollisionlessMixin {
        @Mixin(ObjectPicker.class)
        private static abstract class ObjectPickerMixin {
            @Shadow
            public abstract BlockHitResult pickBlocks(PickContext context, double interactionRange, float partialTick);

            @ModifyReceiver(method = "pick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/BlockHitResult;getLocation()Lnet/minecraft/world/phys/Vec3;"))
            private BlockHitResult modifyHitResult(BlockHitResult instance, PickContext context, @Local(argsOnly = true) Player player, @Local(name = "interactionRange") double interactionRange, @Local(argsOnly = true) float partialTick) {
                if (!PassHandler.passEnabled) return instance;

                PassHandler.passCollisionless = true;
                var hitResult = pickBlocks(context, interactionRange, partialTick);
                PassHandler.passCollisionless = false;
                return hitResult;
            }
        }

        @Mixin(PickContext.class)
        private static class PickContextMixin {
            @WrapOperation(method = "toClipContext", at = @At(value = "INVOKE", target = "Lcom/github/exopandora/shouldersurfing/api/client/world/phys/PickContext;blockContext()Lnet/minecraft/world/level/ClipContext$Block;"))
            private ClipContext.Block wrapBlock(PickContext instance, Operation<ClipContext.Block> original) {
                return PassHandler.passCollisionless ? ClipContext.Block.COLLIDER : original.call(instance);
            }
        }
    }

    private static class PassCollisionlessExtraMixin {
        @Mixin(ObjectPicker.class)
        private static class ObjectPickerMixin {
            @WrapMethod(method = "pick")
            private HitResult wrapPick(PickContext context, double interactionRangeOverride, float partialTick, Player player, Operation<HitResult> original) {
                if (PassHandler.passEnabled) {
                    if (Config.PASS_COLLISIONLESS_HOLDING_TOOL.get() && PassHandler.isHoldingTools(player))
                        return original.call(context, interactionRangeOverride, partialTick, player);
                    if (Config.PASS_COLLISIONLESS_INTERACTION_BLOCKED.get()) {
                        var passHitResult = original.call(context, interactionRangeOverride, partialTick, player);
                        if (PassHandler.interacts(passHitResult)) return passHitResult;
                    }
                }

                PassHandler.passCollisionlessExtra = false;
                var originalHitResult = original.call(context, interactionRangeOverride, partialTick, player);
                PassHandler.passCollisionlessExtra = true;
                return originalHitResult;
            }
        }

        @Mixin(PickContext.class)
        private static class PickContextMixin {
            @WrapOperation(method = "toClipContext", at = @At(value = "NEW", target = "(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/level/ClipContext$Block;Lnet/minecraft/world/level/ClipContext$Fluid;Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/world/level/ClipContext;"))
            private ClipContext wrapBlock(Vec3 from, Vec3 to, ClipContext.Block block, ClipContext.Fluid fluid, Entity entity, Operation<ClipContext> original) {
                return PassHandler.passCollisionlessExtra ? PassHandler.getPassClipContext(from, to, block, fluid, entity) : original.call(from, to, block, fluid, entity);
            }
        }
    }

    private static class PassDeadMixin {
        @Mixin(ObjectPicker.class)
        private static class ObjectPickerMixin {
            @WrapMethod(method = "pick")
            private HitResult wrapPick(PickContext context, double interactionRangeOverride, float partialTick, Player player, Operation<HitResult> original) {
                var passHitResult = original.call(context, interactionRangeOverride, partialTick, player);
                if (!PassHandler.passEnabled || passHitResult.getType() != HitResult.Type.BLOCK) return passHitResult;

                PassHandler.passDead = false;
                var orignalHitResult = original.call(context, interactionRangeOverride, partialTick, player);
                PassHandler.passDead = true;
                if (orignalHitResult.getType() != HitResult.Type.ENTITY) return passHitResult;

                return PassHandler.filterHitResult(passHitResult, player.getEyePosition(partialTick));
            }
        }
    }

    private static class PassAllyMixin {
        @Mixin(ObjectPicker.class)
        private static class ObjectPickerMixin {
            @WrapMethod(method = "pick")
            private HitResult wrapPick(PickContext context, double interactionRangeOverride, float partialTick, Player player, Operation<HitResult> original) {
                if (PassHandler.passEnabled) {
                    if (Config.PASS_ALLY_HOLDING_TOOL.get() && PassHandler.isHoldingTools(player))
                        return original.call(context, interactionRangeOverride, partialTick, player);
                    if (Config.PASS_ALLY_INTERACTION_BLOCKED.get()) {
                        var passHitResult = original.call(context, interactionRangeOverride, partialTick, player);
                        if (PassHandler.interacts(passHitResult)) return passHitResult;
                    }
                }

                PassHandler.passAlly = false;
                var originalHitResult = original.call(context, interactionRangeOverride, partialTick, player);
                PassHandler.passAlly = true;
                return originalHitResult;
            }
        }
    }
}
