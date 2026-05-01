package com.xm666.alivecombat.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class FakeItemCooldowns extends ItemCooldowns {
    @SuppressWarnings("DataFlowIssue")
    @Override
    public boolean isOnCooldown(@NotNull ItemStack stack) {
        return Minecraft.getInstance().player.getCooldowns().isOnCooldown(stack);
    }
}
