package com.xm666.alivecombat.client;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface FakeItem {
    InteractionResultHolder<ItemStack> alivecombat$tryUse(Level level, Player player, InteractionHand usedHand);
}
