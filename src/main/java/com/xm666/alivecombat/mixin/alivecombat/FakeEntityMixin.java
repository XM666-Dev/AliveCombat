package com.xm666.alivecombat.mixin.alivecombat;

import com.xm666.alivecombat.client.FakeEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.animal.fish.AbstractFish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;

@OnlyIn(Dist.CLIENT)
public class FakeEntityMixin {
    @Mixin(AbstractHorse.class)
    private static class AbstractHorseMixin implements FakeEntity {
        @Override
        public InteractionResult alivecombat$tryInteract(Player player, InteractionHand hand) {
            var entity = (AbstractHorse) (Object) this;
            if (!entity.isVehicle() && !entity.isBaby()) {
                if (!entity.isTamed() || !player.isSecondaryUseActive()) {
                    var itemstack = player.getItemInHand(hand);
                    if (!itemstack.isEmpty()) {
                        var interactionresult = itemstack.interactLivingEntity(player, entity, hand);
                        if (interactionresult.consumesAction()) {
                            return interactionresult;
                        }

                        if (entity.isEquippableInSlot(itemstack, EquipmentSlot.BODY) && !entity.isWearingBodyArmor()) {
                            return InteractionResult.SUCCESS;
                        }
                    }

                }
                return InteractionResult.SUCCESS;
            } else {
                var itemstack = player.getItemInHand(hand);
                if (entity.isFood(itemstack)) {
                    var i = entity.getAge();
                    if (player instanceof ServerPlayer) {
                        if (i == 0 && entity.canFallInLove()) {
                            return InteractionResult.SUCCESS_SERVER;
                        }
                    }

                    if (entity.isBaby()) {
                        return InteractionResult.SUCCESS;
                    }

                    if (entity.level().isClientSide()) {
                        return InteractionResult.CONSUME;
                    }
                }

                return InteractionResult.PASS;
            }
        }
    }

    @Mixin(AbstractFish.class)
    private static class AbstractFishMixin implements FakeEntity {
        @Override
        public InteractionResult alivecombat$tryInteract(Player player, InteractionHand hand) {
            var entity = (AbstractFish) (Object) this;
            var itemstack = player.getItemInHand(hand);
            if (itemstack.getItem() == Items.WATER_BUCKET && entity.isAlive()) {
                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.PASS;
            }
        }
    }
}
