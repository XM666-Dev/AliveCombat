package com.xm666.alivecombat.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class FakeLocalPlayer extends LocalPlayer {
    @SuppressWarnings("DataFlowIssue")
    public FakeLocalPlayer(ClientLevel clientLevel, Minecraft mc) {
        super(mc, clientLevel, mc.getConnection(), mc.player.getStats(), mc.player.getRecipeBook(), mc.player.isShiftKeyDown(), mc.player.isSprinting());
        this.foodData = new FakeFoodData();
    }

    public FakeLocalPlayer(ClientLevel clientLevel) {
        this(clientLevel, Minecraft.getInstance());
    }

    @SuppressWarnings({"DataFlowIssue"})
    @Override
    public @NotNull ItemStack getItemInHand(@NotNull InteractionHand hand) {
        return new ItemStack(Minecraft.getInstance().player.getItemInHand(hand).getItem());
    }

    @Override
    public @NotNull ItemCooldowns getCooldowns() {
        return new FakeItemCooldowns();
    }
}
