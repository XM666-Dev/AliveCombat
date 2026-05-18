package com.xm666.alivecombat;

import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.mclanguageprovider.MinecraftModContainer;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class MixinConfigPlugin implements IMixinConfigPlugin {
    private String mixinPackage;

    @Override
    public void onLoad(String mixinPackage) {
        var container = new MinecraftModContainer(FMLLoader.getLoadingModList().getModFileById(AliveCombat.MODID).getMods().getFirst());
        container.registerConfig(ModConfig.Type.STARTUP, MixinConfig.SPEC);
        this.mixinPackage = mixinPackage;
    }

    @Override
    public String getRefMapperConfig() {
        return "";
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        var toIndex = mixinClassName.lastIndexOf('$');
        var fromIndex = Math.max(
                mixinClassName.lastIndexOf('.', toIndex - 1),
                mixinClassName.lastIndexOf('$', toIndex - 1)
        ) + 1;
        var path = mixinClassName.substring(fromIndex, toIndex);
        path = StringUtils.removeEnd(path, "Mixin");
        path = StringUtils.uncapitalize(path);
        path += "Enabled";

        var packageFromIndex = mixinPackage.length() + 1;
        var packageToIndex = mixinClassName.indexOf('.', packageFromIndex);
        var packageName = mixinClassName.substring(packageFromIndex, packageToIndex);
        if (FMLLoader.getLoadingModList().getModFileById(packageName) == null) return false;

        var value = MixinConfig.SPEC.getValues().<ModConfigSpec.BooleanValue>get(path);
        return value == null || value.get();
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return List.of();
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
