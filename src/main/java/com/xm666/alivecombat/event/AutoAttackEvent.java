package com.xm666.alivecombat.event;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class AutoAttackEvent {
    public static byte attackTime;

    @SubscribeEvent
    public static void onPreClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        if (attackTime > 0) attackTime--;
    }
}
