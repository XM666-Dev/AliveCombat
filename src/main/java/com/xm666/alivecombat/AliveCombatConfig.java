package com.xm666.alivecombat;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class AliveCombatConfig implements IMixinConfigPlugin {
    public static CommentedFileConfig config;
    public static CommentedConfig attackBypass;
    public static CommentedConfig autoAttack;

    @Override
    public void onLoad(String mixinPackage) {
        config = CommentedFileConfig.of("config/alivecombat-client.toml");
        config.load();

        config.setComment("noMissCooldown", "Make your attack strength not reset if your attack misses\n使你未命中的攻击不会重置攻击冷却");
        config.add("noMissCooldown", true);

        attackBypass = config.createSubConfig();
        config.add("attackBypass", attackBypass);
        attackBypass = config.get("attackBypass");
        attackBypass.setComment("bypassDead", "Make your attack bypass the mobs playing the death animation\n使你的攻击绕过正在播放死亡动画的生物");
        attackBypass.add("bypassDead", true);
        attackBypass.setComment("bypassNoCollision", "Make your attack bypass grass, and any other no collision blocks\n使你的攻击绕过例如草的无碰撞方块，进行穿草攻击");
        attackBypass.add("bypassNoCollision", true);

        config.setComment("autoAttack", "Press the attack key to automatically attack");
        autoAttack = config.createSubConfig();
        config.add("autoAttack", autoAttack);
        autoAttack = config.get("autoAttack");
        autoAttack.setComment("keyMode", "Key mode of automatic attack. 0=disable, 1=click, 2=hold\n自动攻击的按键模式。0=禁用，1=点击，2=按住");
        autoAttack.add("keyMode", 1);
        autoAttack.setComment("attackTime", "Automatic attack duration, 1=1tick=0.05s\n自动攻击时长，1=1tick=0.05s");
        autoAttack.add("attackTime", 5);

        config.save();
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        boolean flag = (boolean) attackBypass.get("bypassNoCollision") &&
                mixinClassName.equals("com.xm666.alivecombat.mixin.attackbypass.BypassNoCollisionMixin");
        flag |= (boolean) attackBypass.get("bypassDead") &&
                mixinClassName.equals("com.xm666.alivecombat.mixin.attackbypass.BypassDeadMixin");
        flag |= (boolean) config.get("noMissCooldown") && mixinClassName.startsWith("com.xm666.alivecombat.mixin.NoMissMixin$");
        flag |= autoAttack.getByte("keyMode") != 0 && mixinClassName.startsWith("com.xm666.alivecombat.mixin.AutoAttackMixin$");
        return flag;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
