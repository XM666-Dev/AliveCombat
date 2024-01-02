package com.xm666.alivecombat.util;

import net.minecraft.client.Minecraft;

public class Timer {
    public float stopTime;
    public float duration;

    public Timer() {
    }

    public Timer(float duration) {
        this.duration = duration;
    }

    public static float getCurrentTime() {
        var mc = Minecraft.getInstance();
        return mc.clientTickCount + mc.getPartialTick();
    }

    public void start() {
        stopTime = getCurrentTime() + duration;
    }

    public void stop() {
        stopTime = 0.0F;
    }

    public boolean isStarted() {
        return getCurrentTime() < stopTime;
    }

    public boolean isStopped() {
        return !isStarted();
    }
}
