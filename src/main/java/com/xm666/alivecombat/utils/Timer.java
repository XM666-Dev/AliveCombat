package com.xm666.alivecombat.utils;

import net.minecraft.client.Minecraft;

public class Timer {
    public float stop_time;
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
        stop_time = getCurrentTime() + duration;
    }

    public void stop() {
        stop_time = 0.0F;
    }

    public boolean is_started() {
        return getCurrentTime() < stop_time;
    }

    public boolean is_stopped() {
        return !is_started();
    }
}
