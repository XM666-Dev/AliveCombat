package com.xm666.alivecombat;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.xm666.alivecombat.handler.AutoAttackHandler;
import com.xm666.alivecombat.util.TypeUtil;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

public class Config {
    public static Path path = FMLPaths.CONFIGDIR.get().resolve(AliveCombatMod.MOD_ID + ".toml");
    public static CommentedFileConfig config = CommentedFileConfig.of(path);
    public static boolean autoAttackEnabled;
    public static AutoAttackHandler.Mode autoAttackMode;
    public static float autoAttackDuration;
    public static boolean spamAttackEnabled;
    public static boolean fastIndicatorEnabled;

    public static <T> T get(String path, Object fallbackValue, String comment) {
        var value = TypeUtil.tryCast(config.get(path), fallbackValue.getClass(), fallbackValue);
        config.set(path, value);
        config.setComment(path, comment);
        return (T) value;
    }

    public static void load() {
        config.load();
        autoAttackEnabled = get("autoAttack.enabled", true,
                "Automatically attack the mob you aim at."
        );
        autoAttackMode = get("autoAttack.mode", AutoAttackHandler.Mode.CLICK,
                "CLICK: Click once to trigger automatic attack for a time.\n" +
                        "PRESS: Hold down the attack key to keep automatic attack."
        );
        autoAttackDuration = get("autoAttack.duration", 5.0F,
                "When use CLICK mode, how long does the automatic attack last.\n" +
                        "In units of one tick. One tick is equal to 0.05 seconds."
        );
        spamAttackEnabled = get("spamAttackEnabled", true,
                "Missing an attack won't reset your attack strength."
        );
        fastIndicatorEnabled = get("fastIndicatorEnabled", true,
                "Add frame interpolation effect, make the attack indicator smoother."
        );
        config.save();
    }
}
