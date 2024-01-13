package com.xm666.alivecombat;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(AliveCombat.MODID)
public class AliveCombat {
    public static final String MODID = "alivecombat";
    public static final Logger LOGGER = LogUtils.getLogger();

    public AliveCombat(IEventBus modEventBus) {
    }
}
