package com.xm666.alivecombat.handler;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.xm666.alivecombat.render.BlitRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import org.joml.Matrix3x2f;

import java.util.Optional;

public class IndicatorHandler {
    private static final TextureAtlas guiSprites = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.GUI);

    public static void blitSprite(
            GuiGraphics guiGraphics,
            RenderPipeline pipeline,
            Identifier sprite,
            float textureWidth,
            float textureHeight,
            float u,
            float v,
            float x,
            float y,
            float width,
            float height
    ) {
        if (width == 0 || height == 0) return;

        var textureAtlasSprite = guiSprites.getSprite(sprite);
        var atlasLocation = textureAtlasSprite.atlasLocation();
        var minU = textureAtlasSprite.getU(u / textureWidth);
        var maxU = textureAtlasSprite.getU((u + width) / textureWidth);
        var minV = textureAtlasSprite.getV(v / textureHeight);
        var maxV = textureAtlasSprite.getV((v + height) / textureHeight);

        var mc = Minecraft.getInstance();
        var textureManager = mc.getTextureManager();
        var texture = textureManager.getTexture(atlasLocation);
        guiGraphics.guiRenderState.submitGuiElement(new BlitRenderState(pipeline, TextureSetup.singleTexture(texture.getTextureView(), texture.getSampler()), new Matrix3x2f(guiGraphics.pose()), x, y, x + width, y + height, minU, maxU, minV, maxV, -1, guiGraphics.peekScissorStack()));
    }

    public static float getChargeScale(LivingEntity living, float adjustTicks) {
        if (!living.isUsingItem()) return 1.0F;

        var item = living.getUseItem();
        var optionalChargeDuration = getChargeDuration(item, living);
        if (optionalChargeDuration.isEmpty()) return 1.0F;

        var usingTicks = item.getUseDuration(living) - living.getUseItemRemainingTicks();
        var chargeDuration = optionalChargeDuration.get();
        return Mth.clamp((usingTicks + adjustTicks) / chargeDuration, 0.0F, 1.0F);
    }

    private static Optional<Integer> getChargeDuration(ItemStack stack, LivingEntity living) {
        return Optional.ofNullable(
                switch (stack.getItem()) {
                    case BowItem ignored -> 20;
                    case CrossbowItem ignored -> CrossbowItem.getChargeDuration(stack, living);
                    case TridentItem ignored -> 10;
                    default -> null;
                });
    }
}
