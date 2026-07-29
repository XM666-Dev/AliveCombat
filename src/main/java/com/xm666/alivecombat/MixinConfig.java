package com.xm666.alivecombat;

import net.minecraftforge.common.ForgeConfigSpec;

public class MixinConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.BooleanValue AUTO_ATTACK_ENABLED = BUILDER
            .define("autoAttackEnabled", true);

    public static final ForgeConfigSpec.BooleanValue SPAM_ATTACK_ENABLED = BUILDER
            .define("spamAttackEnabled", true);

    public static final ForgeConfigSpec.BooleanValue HELD_ATTACK_ENABLED = BUILDER
            .define("heldAttackEnabled", true);

    public static final ForgeConfigSpec.BooleanValue PASS_COLLISIONLESS_ENABLED = BUILDER
            .define("passCollisionlessEnabled", true);

    public static final ForgeConfigSpec.BooleanValue PASS_COLLISIONLESS_EXTRA_ENABLED = BUILDER
            .define("passCollisionlessExtraEnabled", false);

    public static final ForgeConfigSpec.BooleanValue PASS_DEAD_ENABLED = BUILDER
            .define("passDeadEnabled", true);

    public static final ForgeConfigSpec.BooleanValue PASS_ALLY_ENABLED = BUILDER
            .define("passAllyEnabled", true);

    public static final ForgeConfigSpec.BooleanValue ATTACK_PARTICLE_ENABLED = BUILDER
            .define("attackParticleEnabled", true);

    public static final ForgeConfigSpec.BooleanValue SMOOTH_INDICATOR_ENABLED = BUILDER
            .define("smoothIndicatorEnabled", true);

    public static final ForgeConfigSpec.BooleanValue CHARGE_INDICATOR_ENABLED = BUILDER
            .define("chargeIndicatorEnabled", true);

    public static final ForgeConfigSpec SPEC = BUILDER.build();
}
