package com.xm666.alivecombat;

import com.xm666.alivecombat.handler.AutoAttackHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.Locale;

@OnlyIn(Dist.CLIENT)
@Mod(value = AliveCombat.MODID)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.EnumValue<AutoAttackHandler.Mode> AUTO_ATTACK_MODE = BUILDER
            .defineEnum("autoAttackMode", AutoAttackHandler.Mode.CLICK);

    public static final ForgeConfigSpec.DoubleValue AUTO_ATTACK_DURATION = BUILDER
            .defineInRange("autoAttackDuration", 5.0D, 0.0D, Double.MAX_VALUE);

    public static final ForgeConfigSpec.BooleanValue PASS_COLLISIONLESS_HOLDING_TOOL = BUILDER
            .define("passCollisionlessHoldingTool", true);

    public static final ForgeConfigSpec.BooleanValue PASS_COLLISIONLESS_INTERACTION_BLOCKED = BUILDER
            .define("passCollisionlessInteractionBlocked", true);

    public static final ForgeConfigSpec.BooleanValue PASS_ALLY_HOLDING_TOOL = BUILDER
            .define("passAllyHoldingTool", true);

    public static final ForgeConfigSpec.BooleanValue PASS_ALLY_INTERACTION_BLOCKED = BUILDER
            .define("passAllyInteractionBlocked", true);

    private static final ForgeConfigSpec SPEC = BUILDER.build();

    public Config(ModContainer container) {
        registerConfig(ModConfig.Type.CLIENT, SPEC, container);
    }

    public static void registerConfig(ModConfig.Type type, IConfigSpec<?> spec, ModContainer container) {
        registerConfig(type, spec, container, type.extension());
    }

    public static void registerConfig(ModConfig.Type type, IConfigSpec<?> spec, ModContainer container, String extension) {
        var fileName = String.format(Locale.ROOT, "%s-%s.toml", AliveCombat.MODID, extension);
        var config = new ModConfig(type, spec, container, fileName);
        try {
            var method = ConfigTracker.class.getDeclaredMethod("openConfig", ModConfig.class, Path.class);
            method.setAccessible(true);
            method.invoke(ConfigTracker.INSTANCE, config, FMLPaths.CONFIGDIR.get());
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
