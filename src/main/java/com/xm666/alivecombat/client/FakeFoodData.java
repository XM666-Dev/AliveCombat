package com.xm666.alivecombat.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.food.FoodData;

public class FakeFoodData extends FoodData {
    @SuppressWarnings("DataFlowIssue")
    @Override
    public boolean needsFood() {
        return Minecraft.getInstance().player.getFoodData().needsFood();
    }
}
