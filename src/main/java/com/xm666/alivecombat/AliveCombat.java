package com.xm666.alivecombat;

import com.mojang.logging.LogUtils;
import com.xm666.alivecombat.handler.AttackParticleHandler;
import com.xm666.alivecombat.handler.AutoAttackHandler;
import com.xm666.alivecombat.handler.InputHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(AliveCombat.MODID)
public class AliveCombat {
    public static final String MODID = "alivecombat";
    public static final Logger LOGGER = LogUtils.getLogger();

    public AliveCombat(FMLJavaModLoadingContext context) {
        new Config(context.getContainer());
        new AttackParticleHandler.AttackParticleConfig(context.getModEventBus());
        new AutoAttackHandler.AutoAttackConfig(context.getModEventBus());
        new InputHandler(context.getModEventBus());
    }
}
