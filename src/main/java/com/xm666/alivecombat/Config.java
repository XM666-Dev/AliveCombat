package com.xm666.alivecombat;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
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
    public static AutoAttackHandler.Mode autoAttackMode;
    public static float autoAttackDuration;
    public static boolean spamAttackEnabled;
    public static boolean fastIndicatorEnabled;

    public static <T> T get(String path, T value, BiFunction<UnmodifiableConfig, String, T> getter) {
        try {
            var result = getter.apply(config, path);
            if (result != null) return result;
        } catch (Exception ignored) {
        }
        config.set(path, value);
        return value;
    }

    public static <T> T get(String path, T value) {
        return get(path, value, UnmodifiableConfig::get);
    }

    public static <T> T get(String path, T value, Function<Object, T> function) {
        BiFunction<UnmodifiableConfig, String, T> getter = UnmodifiableConfig::get;
        return get(path, value, getter.andThen(function));
    }

    public static void load() {
        config.load();
        autoAttackEnabled = get("autoAttack.enabled", true);
        autoAttackMode = get("autoAttack.mode", AutoAttackHandler.Mode.CLICK, (config, path) -> config.getEnum(path, AutoAttackHandler.Mode.class));
        autoAttackDuration = get("autoAttack.duration", 5.0F, (value) -> ((Number) value).floatValue());
        spamAttackEnabled = get("spamAttackEnabled", true);
        fastIndicatorEnabled = get("fastIndicatorEnabled", true);
        config.save();
    }
}
