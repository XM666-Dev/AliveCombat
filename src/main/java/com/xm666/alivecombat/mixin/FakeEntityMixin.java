package com.xm666.alivecombat.mixin;

import com.xm666.alivecombat.client.FakeEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
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

                        if (entity.canWearBodyArmor() && entity.isBodyArmorItem(itemstack) && !entity.isWearingBodyArmor()) {
                            return InteractionResult.sidedSuccess(entity.level().isClientSide);
                        }
                    }
                }
                return InteractionResult.sidedSuccess(entity.level().isClientSide);
            } else {
                var itemstack = player.getItemInHand(hand);
                if (entity.isFood(itemstack)) {
                    var i = entity.getAge();
                    if (!entity.level().isClientSide && i == 0 && entity.canFallInLove()) {
                        return InteractionResult.SUCCESS;
                    }

                    if (entity.isBaby()) {
                        return InteractionResult.sidedSuccess(entity.level().isClientSide);
                    }

                    if (entity.level().isClientSide) {
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
                var level = entity.level();
                return InteractionResult.sidedSuccess(level.isClientSide);
            } else {
                return InteractionResult.PASS;
            }
        }
    }
}
