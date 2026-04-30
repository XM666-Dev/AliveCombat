package com.xm666.alivecombat.handler;

import com.mojang.blaze3d.platform.InputConstants;
import com.xm666.alivecombat.AliveCombat;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.util.Lazy;
import org.lwjgl.glfw.GLFW;

@Mod(value = AliveCombat.MODID, dist = Dist.CLIENT)
public class InputHandler {
    public static final Lazy<KeyMapping> TOGGLE_PASS_MAPPING = Lazy.of(() -> new KeyMapping(
            "key.alivecombat.togglePass",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            "key.categories.misc"
    ));

    public InputHandler(IEventBus modEventBus) {
        modEventBus.register(InputHandler.class);
    }

    @SubscribeEvent
    public static void registerBindings(RegisterKeyMappingsEvent event) {
        event.register(TOGGLE_PASS_MAPPING.get());
    }
}
