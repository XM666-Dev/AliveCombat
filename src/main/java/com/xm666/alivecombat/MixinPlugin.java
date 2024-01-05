package com.xm666.alivecombat;

import com.xm666.alivecombat.mixin.FastIndicatorMixin;
import com.xm666.alivecombat.mixin.SpamAttackMixin;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class MixinPlugin implements IMixinConfigPlugin {
    public static boolean isMixinClass(String mixinClassName, Class<?> mixinClass) {
        return mixinClassName.equals(mixinClass.getName());
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
        if (isMixinClass(mixinClassName, SpamAttackMixin.class)) {
            return Config.spamAttackEnabled;
        }
        if (isMixinClass(mixinClassName, FastIndicatorMixin.class)) {
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
