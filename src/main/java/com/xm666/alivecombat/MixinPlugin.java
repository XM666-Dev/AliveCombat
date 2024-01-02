package com.xm666.alivecombat;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class MixinPlugin implements IMixinConfigPlugin {
    public static boolean isMixinNamed(String mixinClassName, String name) {
        return mixinClassName.startsWith(AliveCombatMod.class.getPackage().getName() + ".mixin." + name);
    }

    @Override
    public void onLoad(String mixinPackage) {
        Config.load();
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (isMixinNamed(mixinClassName, "SpamAttackMixin")) {
            return Config.spamAttackEnabled;
        }
        if (isMixinNamed(mixinClassName, "FastIndicatorMixin")) {
            return Config.fastIndicatorEnabled;
        }
        return true;
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
