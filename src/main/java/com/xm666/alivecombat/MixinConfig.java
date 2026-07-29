package com.xm666.alivecombat;

import net.neoforged.neoforge.common.ModConfigSpec;

public class MixinConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue AUTO_ATTACK_ENABLED = BUILDER
            .define("autoAttackEnabled", true);

    public static final ModConfigSpec.BooleanValue SPAM_ATTACK_ENABLED = BUILDER
            .define("spamAttackEnabled", true);

    public static final ModConfigSpec.BooleanValue HELD_ATTACK_ENABLED = BUILDER
            .define("heldAttackEnabled", true);

    public static final ModConfigSpec.BooleanValue PASS_COLLISIONLESS_ENABLED = BUILDER
            .define("passCollisionlessEnabled", true);

    public static final ModConfigSpec.BooleanValue PASS_COLLISIONLESS_EXTRA_ENABLED = BUILDER
            .define("passCollisionlessExtraEnabled", false);

    public static final ModConfigSpec.BooleanValue PASS_DEAD_ENABLED = BUILDER
            .define("passDeadEnabled", true);

    public static final ModConfigSpec.BooleanValue PASS_ALLY_ENABLED = BUILDER
            .define("passAllyEnabled", true);

    public static final ModConfigSpec.BooleanValue ATTACK_PARTICLE_ENABLED = BUILDER
            .define("attackParticleEnabled", true);

    public static final ModConfigSpec.BooleanValue SMOOTH_INDICATOR_ENABLED = BUILDER
            .define("smoothIndicatorEnabled", true);

    public static final ModConfigSpec.BooleanValue CHARGE_INDICATOR_ENABLED = BUILDER
            .define("chargeIndicatorEnabled", true);

    public static final ModConfigSpec SPEC = BUILDER.build();
}
