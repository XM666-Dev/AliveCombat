package com.xm666.alivecombat.client;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;

public interface FakeEntity {
    InteractionResult alivecombat$tryInteract(Player player, InteractionHand hand);
}
