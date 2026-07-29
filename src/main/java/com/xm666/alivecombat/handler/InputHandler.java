package com.xm666.alivecombat.handler;

import com.mojang.blaze3d.platform.InputConstants;
import com.xm666.alivecombat.AliveCombat;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
@Mod(value = AliveCombat.MODID)
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
