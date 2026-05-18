package com.xm666.alivecombat.mixin.alivecombat;

import com.xm666.alivecombat.client.FakeItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.WritableBookItem;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;

@OnlyIn(Dist.CLIENT)
public class FakeItemMixin {
    @Mixin(WritableBookItem.class)
    private static class WritableBookItemMixin implements FakeItem {
        @Override
        public InteractionResultHolder<ItemStack> alivecombat$tryUse(Level level, Player player, InteractionHand usedHand) {
            ItemStack itemstack = player.getItemInHand(usedHand);
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemstack);
        }
    }
}
