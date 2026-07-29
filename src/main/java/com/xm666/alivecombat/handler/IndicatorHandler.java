package com.xm666.alivecombat.handler;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;

import java.util.Optional;

public class IndicatorHandler {
    public static void blitSprite(
            GuiGraphics guiGraphics,
            ResourceLocation sprite,
            float textureWidth,
            float textureHeight,
            float uPosition,
            float vPosition,
            float x,
            float y,
            float uWidth,
            float vHeight
    ) {
        if (uWidth == 0.0F || vHeight == 0.0F) return;

        var mc = Minecraft.getInstance();
        var guiSprites = mc.getGuiSprites();
        var textureAtlasSprite = guiSprites.getSprite(sprite);
        var atlasLocation = textureAtlasSprite.atlasLocation();
        var minU = textureAtlasSprite.getU(uPosition / textureWidth);
        var maxU = textureAtlasSprite.getU((uPosition + uWidth) / textureWidth);
        var minV = textureAtlasSprite.getV(vPosition / textureHeight);
        var maxV = textureAtlasSprite.getV((vPosition + vHeight) / textureHeight);
        RenderSystem.setShaderTexture(0, atlasLocation);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        var matrix = guiGraphics.pose().last().pose();
        var buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(matrix, x, y, 0).uv(minU, minV).endVertex();
        buffer.vertex(matrix, x, y + vHeight, 0).uv(minU, maxV).endVertex();
        buffer.vertex(matrix, x + uWidth, y + vHeight, 0).uv(maxU, maxV).endVertex();
        buffer.vertex(matrix, x + uWidth, y, 0).uv(maxU, minV).endVertex();
        BufferUploader.drawWithShader(buffer.end());
    }

    public static float getChargeScale(LivingEntity living, float adjustTicks) {
        if (!living.isUsingItem()) return 1.0F;

        var item = living.getUseItem();
        var optionalChargeDuration = getChargeDuration(item, living);
        if (optionalChargeDuration.isEmpty()) return 1.0F;

        var usingTicks = item.getUseDuration() - living.getUseItemRemainingTicks();
        var chargeDuration = optionalChargeDuration.get();
        return Mth.clamp((usingTicks + adjustTicks) / chargeDuration, 0.0F, 1.0F);
    }

    private static Optional<Integer> getChargeDuration(ItemStack stack, LivingEntity living) {
        return Optional.ofNullable(
                switch (stack.getItem()) {
                    case BowItem ignored -> 20;
                    case CrossbowItem ignored -> CrossbowItem.getChargeDuration(stack);
                    case TridentItem ignored -> 10;
                    default -> null;
                });
    }
}
