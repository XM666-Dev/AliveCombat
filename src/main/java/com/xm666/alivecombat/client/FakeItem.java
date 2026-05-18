package com.xm666.alivecombat.client;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public interface FakeItem {
    InteractionResult alivecombat$tryUse(Level level, Player player, InteractionHand hand);
}
