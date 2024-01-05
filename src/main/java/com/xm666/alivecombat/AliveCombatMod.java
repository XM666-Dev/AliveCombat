package com.xm666.alivecombat;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(AliveCombatMod.MOD_ID)
public class AliveCombatMod {
    public static final String MOD_ID = "alivecombat";
    public static final Logger LOGGER = LogUtils.getLogger();

    public AliveCombatMod(IEventBus modEventBus) {
    }
}
