package com.xm666.alivecombat.util;

import net.minecraft.client.Minecraft;

public class Timer {
    public float duration;
    public float stopTime;

    public Timer() {
    }

    public Timer(float duration) {
        this.duration = duration;
    }

    public static float getCurrentTime() {
        var minecraft = Minecraft.getInstance();
        return minecraft.clientTickCount + minecraft.getPartialTick();
    }

    public void start() {
        stopTime = getCurrentTime() + duration;
    }

    public void stop() {
        stopTime = 0.0F;
    }

    public boolean isRunning() {
        return getCurrentTime() < stopTime;
    }
}
