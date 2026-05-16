package com.xm666.alivecombat.handler;

import com.mojang.blaze3d.platform.InputConstants;
import com.xm666.alivecombat.AliveCombat;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.util.Lazy;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = AliveCombat.MODID, value = Dist.CLIENT)
public class InputHandler {
    public static final Lazy<KeyMapping> TOGGLE_PASS_MAPPING = Lazy.of(() -> new KeyMapping(
            "key.alivecombat.togglePass",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            KeyMapping.Category.MISC
    ));

    @SubscribeEvent
    public static void registerBindings(RegisterKeyMappingsEvent event) {
        event.register(TOGGLE_PASS_MAPPING.get());
    }
}
