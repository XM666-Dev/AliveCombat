package com.xm666.alivecombat.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.xm666.alivecombat.AliveCombatConfig;
import com.xm666.alivecombat.event.AutoAttackEvent;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

public class AutoAttackMixin {
    @Mixin(Minecraft.class)
    public abstract static class MinecraftMixin {
        @Shadow
        @Nullable
        public HitResult hitResult;

        @Shadow
        @Nullable
        public LocalPlayer player;

        @Shadow
        @Final
        public Options options;

        @Redirect(method = "startAttack", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;missTime:I", ordinal = 0))
        int missTime(Minecraft instance) {
            return 0;
        }

        @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;handleKeybinds()V"))
        void onPick(CallbackInfo ci) {
            if (this.hitResult instanceof EntityHitResult entityHit && this.player.canHit(entityHit.getEntity(), 0)) {
                if (AutoAttackEvent.attackTime > 0 && this.player.getAttackStrengthScale(0) == 1) {
                    AutoAttackEvent.attackTime = 0;
                    KeyMapping keyAttack = this.options.keyAttack;
                    if (keyAttack.clickCount == 0) keyAttack.clickCount = 1;
                }
            } else {
                KeyMapping keyAttack = this.options.keyAttack;
                if (AliveCombatConfig.autoAttack.getByte("keyMode") == 1 ? keyAttack.clickCount > 0 : keyAttack.isDown())
                    AutoAttackEvent.attackTime = AliveCombatConfig.autoAttack.getByte("attackTime");
            }
        }
    }

    @Mixin(GameRenderer.class)
    static class GameRendererMixin {
        @Shadow
        @Final
        Minecraft minecraft;

        @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;pick(F)V", shift = At.Shift.AFTER))
        void onPick(float p_109090_, long p_109091_, PoseStack p_109092_, CallbackInfo ci) {
            KeyMapping keyAttack = minecraft.options.keyAttack;
            if (AliveCombatConfig.autoAttack.getByte("keyMode") == 1 ? keyAttack.clickCount > 0 : keyAttack.isDown())
                AutoAttackEvent.attackTime = AliveCombatConfig.autoAttack.getByte("attackTime");
            if (AutoAttackEvent.attackTime > 0 && minecraft.hitResult instanceof EntityHitResult entityHit)
                if (minecraft.player.canHit(entityHit.getEntity(), 0))
                    if (minecraft.player.getAttackStrengthScale(0) == 1) {
                        AutoAttackEvent.attackTime = 0;
                        if (keyAttack.clickCount == 0) keyAttack.clickCount = 1;
                        minecraft.handleKeybinds();
                    }
        }
    }
}
