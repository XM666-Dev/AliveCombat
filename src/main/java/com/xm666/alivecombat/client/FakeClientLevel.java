package com.xm666.alivecombat.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class FakeClientLevel extends ClientLevel {
    @SuppressWarnings("DataFlowIssue")
    public FakeClientLevel(Minecraft mc) {
        super(mc.getConnection(), mc.level.getLevelData(), mc.level.dimension(), mc.level.dimensionTypeRegistration(), 0, mc.level.getServerSimulationDistance(), mc.levelRenderer, mc.level.isDebug(), 0L, mc.level.getSeaLevel());
    }

    public FakeClientLevel() {
        this(Minecraft.getInstance());
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public @NotNull BlockState getBlockState(@NotNull BlockPos pos) {
        return Minecraft.getInstance().level.getBlockState(pos);
    }

    @Override
    public boolean setBlock(@NotNull BlockPos pos, @NotNull BlockState state, int flags, int recursionLeft) {
        return true;
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public @NotNull List<Entity> getEntities(@Nullable Entity entity, @NotNull AABB boundingBox, @NotNull Predicate<? super Entity> predicate) {
        return Minecraft.getInstance().level.getEntities(entity, boundingBox, predicate);
    }

    @Override
    public void addParticle(@NotNull ParticleOptions particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
    }

    @Override
    public void addParticle(@NotNull ParticleOptions p_104714_, boolean p_104715_, boolean p_383197_, double p_104716_, double p_104717_, double p_104718_, double p_104719_, double p_104720_, double p_104721_) {
    }

    @Override
    public void addAlwaysVisibleParticle(@NotNull ParticleOptions particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
    }

    @Override
    public void addAlwaysVisibleParticle(@NotNull ParticleOptions particleData, boolean ignoreRange, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
    }

    @Override
    public void playSeededSound(@Nullable Entity p_393665_, double p_263372_, double p_263404_, double p_263365_, @NotNull Holder<@NotNull SoundEvent> p_263335_, @NotNull SoundSource p_263417_, float p_263416_, float p_263349_, long p_263408_) {
    }

    @Override
    public void playSeededSound(@Nullable Entity p_263536_, @NotNull Entity p_394209_, @NotNull Holder<@NotNull SoundEvent> p_263518_, @NotNull SoundSource p_263487_, float p_263538_, float p_263524_, long p_263509_) {
    }

    @Override
    public void playLocalSound(@NotNull Entity entity, @NotNull SoundEvent sound, @NotNull SoundSource category, float volume, float pitch) {
    }

    @Override
    public void playLocalSound(double x, double y, double z, @NotNull SoundEvent sound, @NotNull SoundSource category, float volume, float pitch, boolean distanceDelay) {
    }
}
