package com.xm666.alivecombat;

import com.xm666.alivecombat.handler.AutoAttackHandler;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.EnumValue<AutoAttackHandler.Mode> AUTO_ATTACK_MODE = BUILDER
            .defineEnum("autoAttackMode", AutoAttackHandler.Mode.CLICK);

    public static final ModConfigSpec.DoubleValue AUTO_ATTACK_DURATION = BUILDER
            .defineInRange("autoAttackDuration", 5.0D, 0.0D, Double.MAX_VALUE);

    public static final ModConfigSpec.BooleanValue PASS_COLLISIONLESS_HOLDING_TOOL = BUILDER
            .define("passCollisionlessHoldingTool", true);

    public static final ModConfigSpec.BooleanValue PASS_COLLISIONLESS_INTERACTION_BLOCKED = BUILDER
            .define("passCollisionlessInteractionBlocked", true);

    public static final ModConfigSpec.BooleanValue PASS_ALLY_HOLDING_TOOL = BUILDER
            .define("passAllyHoldingTool", true);

    public static final ModConfigSpec.BooleanValue PASS_ALLY_INTERACTION_BLOCKED = BUILDER
            .define("passAllyInteractionBlocked", true);

    public static final ModConfigSpec.BooleanValue ATTACK_PARTICLE_ENABLED = BUILDER
            .define("attackParticleEnabled", true);

    static final ModConfigSpec SPEC = BUILDER.build();
}
