package com.xm666.alivecombat;

import com.mojang.logging.LogUtils;
import com.xm666.alivecombat.handler.AutoAttackHandler;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(AliveCombatMod.MODID)
public class AliveCombatMod {
    public static final String MODID = "alivecombat";
    public static final Logger LOGGER = LogUtils.getLogger();

    public AliveCombatMod(IEventBus modEventBus) {
        AutoAttackHandler.load();
    }
}
