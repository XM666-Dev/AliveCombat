package com.xm666.alivecombat;

import com.xm666.alivecombat.handler.AutoAttackHandler;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.EnumValue<AutoAttackHandler.Mode> AUTO_ATTACK_MODE = BUILDER
            .defineEnum("autoAttackMode", AutoAttackHandler.Mode.CLICK);

    public static final ModConfigSpec.DoubleValue AUTO_ATTACK_DURATION = BUILDER
            .defineInRange("autoAttackDuration", 5.0D, 0.0D, Double.MAX_VALUE);

    public static final ModConfigSpec.BooleanValue FAKE_SWEEP_ENABLED = BUILDER
            .define("fakeSweepEnabled", true);

    static final ModConfigSpec SPEC = BUILDER.build();
}
