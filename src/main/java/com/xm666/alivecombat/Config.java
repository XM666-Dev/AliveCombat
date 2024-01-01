package com.xm666.alivecombat;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.xm666.alivecombat.handler.AutoAttackHandler;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Config {
    public static Path path = FMLPaths.CONFIGDIR.get().resolve(AliveCombatMod.MODID + ".toml");
    public static CommentedFileConfig config = CommentedFileConfig.of(path);
    public static boolean autoAttackEnabled;
    public static AutoAttackHandler.KeyMode autoAttackKeyMode;
    public static float autoAttackDuration;
    public static boolean spamAttackEnabled;
    public static boolean fastIndicatorEnabled;
    public static boolean fullAttackEnabled;

    public static <T> T get(CommentedConfig config, String path, T value, BiFunction<CommentedConfig, String, T> getter) {
        try {
            if (config.contains(path)) {
                return getter.apply(config, path);
            }
        } catch (Exception ignored) {
        }
        config.set(path, value);
        return value;
    }

    public static <T> T get(CommentedConfig config, String path, T value) {
        return get(config, path, value, CommentedConfig::get);
    }

    public static <T> T getValue(CommentedConfig config, String path, T value, Function<Object, T> function) {
        BiFunction<CommentedConfig, String, T> getter = CommentedConfig::get;
        return get(config, path, value, getter.andThen(function));
    }

    public static CommentedConfig getSubConfig(CommentedConfig config, String path) {
        return get(config, path, config.createSubConfig());
    }

    public static void load() {
        config.load();
        var autoAttackConfig = getSubConfig(config, "autoAttack");
        autoAttackEnabled = get(autoAttackConfig, "enabled", true);
        autoAttackKeyMode = get(autoAttackConfig, "keyMode", AutoAttackHandler.KeyMode.CLICK, (config, path) -> config.getEnum(path, AutoAttackHandler.KeyMode.class));
        autoAttackDuration = getValue(autoAttackConfig, "duration", 5.0F, (value) -> ((Number) value).floatValue());
        //autoAttackDuration = get(autoAttackConfig, "duration", 5.0F);
        spamAttackEnabled = get(config, "spamAttackEnabled", true);
        fastIndicatorEnabled = get(config, "fastIndicatorEnabled", true);
        fullAttackEnabled = get(config, "fullAttackEnabled", true);
        config.save();
    }
}
