package com.xm666.alivecombat;

import com.xm666.alivecombat.mixin.AutoAttackMixin;
import com.xm666.alivecombat.mixin.FastIndicatorMixin;
import com.xm666.alivecombat.mixin.PassAttackMixin;
import com.xm666.alivecombat.mixin.SpamAttackMixin;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class MixinPlugin implements IMixinConfigPlugin {
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
        var declaringClassName = mixinClassName.substring(0, mixinClassName.lastIndexOf("$"));
        Class<?> declaringClass;
        try {
            declaringClass = Class.forName(declaringClassName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException();
        }
        if (declaringClass == AutoAttackMixin.class) {
            return Config.autoAttackEnabled;
        } else if (declaringClass == SpamAttackMixin.class) {
            return Config.spamAttackEnabled;
        } else if (declaringClass == FastIndicatorMixin.class) {
            return Config.fastIndicatorEnabled;
        } else if (declaringClass == PassAttackMixin.PassNoColliderMixin.class) {
            return Config.passNoColliderEnabled;
        } else if (declaringClass == PassAttackMixin.PassDeadMixin.class) {
            return Config.passDeadEnabled;
        }
        throw new RuntimeException();
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
