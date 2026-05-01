package com.xm666.alivecombat.timer;

import com.xm666.alivecombat.AliveCombat;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.util.function.Supplier;

public class Timer {
    private static int ticks;

    public Supplier<Float> duration;
    public float end;

    public Timer(Supplier<Float> duration) {
        this.duration = duration;
    }

    private static float tickCount() {
        var mc = Minecraft.getInstance();
        return ticks + mc.getPartialTick();
    }

    public void start() {
        end = tickCount() + duration.get();
    }

    public void stop() {
        end = 0.0F;
    }

    public boolean isRunning() {
        return tickCount() < end;
    }

    @EventBusSubscriber(modid = AliveCombat.MODID, value = Dist.CLIENT)
    private static class TimerClient {
        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Pre event) {
            if (Minecraft.getInstance().isPaused()) return;

            ++ticks;
        }
    }
}
