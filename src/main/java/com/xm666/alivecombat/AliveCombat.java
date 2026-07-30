package com.xm666.alivecombat;

import com.mojang.logging.LogUtils;
import com.xm666.alivecombat.handler.AttackParticleHandler;
import com.xm666.alivecombat.handler.AutoAttackHandler;
import com.xm666.alivecombat.handler.InputHandler;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(AliveCombat.MODID)
public class AliveCombat {
    public static final String MODID = "alivecombat";
    public static final Logger LOGGER = LogUtils.getLogger();

    public AliveCombat(FMLJavaModLoadingContext context) {
        this(context.getContainer(), context.getModEventBus());
    }

    public AliveCombat(ModContainer container, IEventBus eventBus) {
        Config.init(container);
        AttackParticleHandler.AttackParticleConfig.init(eventBus);
        AutoAttackHandler.AutoAttackConfig.init(eventBus);
        InputHandler.init(eventBus);
    }
}
