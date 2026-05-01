package com.xm666.alivecombat.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class FakeClientLevel extends ClientLevel {
    @SuppressWarnings("DataFlowIssue")
    public FakeClientLevel(Minecraft mc) {
        super(mc.getConnection(), mc.level.getLevelData(), mc.level.dimension(), mc.level.dimensionTypeRegistration(), 0, mc.level.getServerSimulationDistance(), mc::getProfiler, mc.levelRenderer, mc.level.isDebug(), 0L);
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
    public void addParticle(@NotNull ParticleOptions particleData, boolean forceAlwaysRender, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
    }

    @Override
    public void addAlwaysVisibleParticle(@NotNull ParticleOptions particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
    }

    @Override
    public void addAlwaysVisibleParticle(@NotNull ParticleOptions particleData, boolean ignoreRange, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
    }

    @Override
    public void playSeededSound(@Nullable Player var1, double var2, double var4, double var6, @NotNull Holder<SoundEvent> var8, @NotNull SoundSource var9, float var10, float var11, long var12) {
    }

    @Override
    public void playSeededSound(@Nullable Player var1, @NotNull Entity var2, @NotNull Holder<SoundEvent> var3, @NotNull SoundSource var4, float var5, float var6, long var7) {
    }

    @Override
    public void playLocalSound(@NotNull Entity entity, @NotNull SoundEvent sound, @NotNull SoundSource category, float volume, float pitch) {
    }

    @Override
    public void playLocalSound(double x, double y, double z, @NotNull SoundEvent sound, @NotNull SoundSource category, float volume, float pitch, boolean distanceDelay) {
    }
}
