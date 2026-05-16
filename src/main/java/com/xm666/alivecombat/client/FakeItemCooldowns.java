package com.xm666.alivecombat.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;

public class FakeItemCooldowns extends ItemCooldowns {
    @SuppressWarnings("DataFlowIssue")
    @Override
    public boolean isOnCooldown(Item item) {
        return Minecraft.getInstance().player.getCooldowns().isOnCooldown(item);
    }
}
