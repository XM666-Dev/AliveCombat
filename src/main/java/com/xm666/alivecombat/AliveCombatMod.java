package com.xm666.alivecombat;

import com.xm666.alivecombat.event.AutoAttackEvent;
import com.xm666.alivecombat.event.BloodSplashEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod("alivecombat")
public class AliveCombatMod {
    public AliveCombatMod() {
        if (AliveCombatConfig.autoAttack.getByte("keyMode") != 0)
            MinecraftForge.EVENT_BUS.register(AutoAttackEvent.class);
        MinecraftForge.EVENT_BUS.register(BloodSplashEvent.class);
    }
}
