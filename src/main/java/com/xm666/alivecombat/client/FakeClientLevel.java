package com.xm666.alivecombat.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class FakeClientLevel extends ClientLevel {
    public FakeClientLevel(ClientPacketListener connection, ClientLevelData clientLevelData, ResourceKey<Level> dimension, Holder<DimensionType> dimensionType, int viewDistance, int serverSimulationDistance, Supplier<ProfilerFiller> profiler, LevelRenderer levelRenderer, boolean isDebug, long biomeZoomSeed) {
        super(connection, clientLevelData, dimension, dimensionType, viewDistance, serverSimulationDistance, profiler, levelRenderer, isDebug, biomeZoomSeed);
    }

    @SuppressWarnings("DataFlowIssue")
    public FakeClientLevel(Minecraft mc) {
        this(mc.getConnection(), mc.level.getLevelData(), mc.level.dimension(), mc.level.dimensionTypeRegistration(), 0, mc.level.getServerSimulationDistance(), mc::getProfiler, mc.levelRenderer, mc.level.isDebug(), 0L);
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
