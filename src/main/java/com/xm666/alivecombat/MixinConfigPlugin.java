package com.xm666.alivecombat;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.moddiscovery.ModInfo;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforgespi.language.IConfigurable;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class MixinConfigPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {
        new ModContainer(new ModInfo(null, new IConfigurable() {
            @Override
            public <T> Optional<T> getConfigElement(String... key) {
                if (key[0].equals("modId")) {
                    return (Optional<T>) Optional.of(AliveCombat.MODID);
                }
                return Optional.empty();
            }

            @Override
            public List<? extends IConfigurable> getConfigList(String... key) {
                return List.of();
            }
        })) {
            @Override
            public @Nullable IEventBus getEventBus() {
                return null;
            }
        }.registerConfig(ModConfig.Type.STARTUP, MixinConfig.SPEC);
    }

    @Override
    public String getRefMapperConfig() {
        return "";
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        var path = mixinClassName.substring(0, mixinClassName.lastIndexOf("$"));
        path = path.substring(path.lastIndexOf("$") + 1);
        path = path.substring(path.lastIndexOf(".") + 1);
        path = StringUtils.removeEnd(path, "Mixin");
        path = StringUtils.uncapitalize(path);
        path += "Enabled";
        return MixinConfig.SPEC.getValues().<ModConfigSpec.BooleanValue>get(path).get();
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
