package com.xm666.alivecombat.handler;

import com.xm666.alivecombat.AliveCombat;
import com.xm666.alivecombat.client.FakeClientLevel;
import com.xm666.alivecombat.client.FakeLocalPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.util.TriState;

@EventBusSubscriber(modid = AliveCombat.MODID, value = Dist.CLIENT)
public class PassHandler {
    public static boolean passEnabled = true;
    public static boolean passCollisionless = false;
    public static boolean passCollisionlessExtra = true;
    public static boolean passDead = true;
    public static boolean passAlly = true;
    private static FakeClientLevel fakeClientLevel;
    private static FakeLocalPlayer fakeLocalPlayer;

    @SubscribeEvent
    public static void onClientJoin(ClientPlayerNetworkEvent.LoggingIn event) {
        fakeClientLevel = new FakeClientLevel();
        fakeLocalPlayer = new FakeLocalPlayer(fakeClientLevel);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        while (InputHandler.TOGGLE_PASS_MAPPING.get().consumeClick()) {
            passEnabled = !passEnabled;

            var mc = Minecraft.getInstance();
            var component = Component.translatable(passEnabled ? "alivecombat.togglePassOn" : "alivecombat.togglePassOff");
            mc.gui.setOverlayMessage(component, false);
        }
    }

    public static boolean isHoldingTools(Entity entity) {
        if (!(entity instanceof LivingEntity living)) return false;

        var weapon = living.getMainHandItem();
        return weapon.is(Tags.Items.TOOLS);
    }

    public static ClipContext getPassClipContext(Vec3 from, Vec3 to, ClipContext.Block block, ClipContext.Fluid fluid, Entity entity) {
        var collisionContext = CollisionContext.of(entity);
        return new ClipContext(from, to, block, fluid, collisionContext) {
            public VoxelShape getBlockShape(BlockState blockState, BlockGetter level, BlockPos pos) {
                var voxelShape = Block.OUTLINE.get(blockState, level, pos, collisionContext);
                var blockHitResult = level.clipWithInteractionOverride(from, to, pos, voxelShape, blockState);
                var interactionResult = interactsBlock(blockHitResult);
                return interactionResult.consumesAction() ? voxelShape : Block.COLLIDER.get(blockState, level, pos, collisionContext);
            }
        };
    }

    public static HitResult filterHitResult(HitResult hitResult, Vec3 pos) {
        var location = hitResult.getLocation();
        var direction = Direction.getNearest(location.x - pos.x, location.y - pos.y, location.z - pos.z);
        return BlockHitResult.miss(location, direction, BlockPos.containing(location));
    }

    public static boolean interacts(HitResult hitResult) {
        var entityInteractionResult = interactsEntity(hitResult);
        if (entityInteractionResult.consumesAction()) return true;
        if (entityInteractionResult == InteractionResult.FAIL) return false;

        var blockInteractionResult = interactsBlock(hitResult);
        if (blockInteractionResult.consumesAction()) return true;
        if (blockInteractionResult == InteractionResult.FAIL) return false;

        return usesItem().consumesAction();
    }

    private static InteractionResult interactsEntity(HitResult hitResult) {
        if (!(hitResult instanceof EntityHitResult entityHitResult)) return InteractionResult.PASS;

        var target = entityHitResult.getEntity();
        if (!fakeClientLevel.getWorldBorder().isWithinBounds(target.blockPosition())) return InteractionResult.FAIL;

        for (var hand : InteractionHand.values()) {
            var interactionResult = interactAt(fakeLocalPlayer, target, entityHitResult, hand);
            if (!interactionResult.consumesAction()) {
                interactionResult = interact(fakeLocalPlayer, target, hand);
            }
            if (interactionResult.consumesAction()) return interactionResult;
        }

        return InteractionResult.PASS;
    }

    @SuppressWarnings({"DataFlowIssue", "SameParameterValue"})
    private static InteractionResult interactAt(Player player, Entity target, EntityHitResult ray, InteractionHand hand) {
        var mc = Minecraft.getInstance();
        var gameMode = mc.gameMode;
        var vector = ray.getLocation().subtract(target.getX(), target.getY(), target.getZ());
        if (gameMode.getPlayerMode() == GameType.SPECTATOR) return InteractionResult.PASS;

        var cancelResult = CommonHooks.onInteractEntityAt(player, target, ray, hand);
        if (cancelResult != null) return cancelResult;

        return gameMode.getPlayerMode() == GameType.SPECTATOR ? InteractionResult.PASS : target.interactAt(player, vector, hand);
    }

    @SuppressWarnings({"DataFlowIssue", "SameParameterValue"})
    private static InteractionResult interact(Player player, Entity target, InteractionHand hand) {
        var mc = Minecraft.getInstance();
        var gameMode = mc.gameMode;
        return gameMode.getPlayerMode() == GameType.SPECTATOR ? InteractionResult.PASS : player.interactOn(target, hand);
    }

    private static InteractionResult interactsBlock(HitResult hitResult) {
        if (!(hitResult instanceof BlockHitResult blockHitResult)) return InteractionResult.PASS;

        for (var hand : InteractionHand.values()) {
            var interactionResult = useItemOn(fakeLocalPlayer, hand, blockHitResult);
            if (interactionResult.consumesAction() || interactionResult == InteractionResult.FAIL)
                return interactionResult;
        }

        return InteractionResult.PASS;
    }

    @SuppressWarnings("SameParameterValue")
    private static InteractionResult useItemOn(LocalPlayer player, InteractionHand hand, BlockHitResult result) {
        if (!fakeClientLevel.getWorldBorder().isWithinBounds(result.getBlockPos())) return InteractionResult.FAIL;

        return performUseItemOn(player, hand, result);
    }

    @SuppressWarnings("DataFlowIssue")
    private static InteractionResult performUseItemOn(LocalPlayer player, InteractionHand hand, BlockHitResult blockHitResult) {
        var mc = Minecraft.getInstance();
        var gameMode = mc.gameMode;
        var blockPos = blockHitResult.getBlockPos();
        var item = player.getItemInHand(hand);
        var event = CommonHooks.onRightClickBlock(player, hand, blockPos, blockHitResult);
        if (event.isCanceled()) {
            return event.getCancellationResult();
        } else if (gameMode.getPlayerMode() == GameType.SPECTATOR) {
            return InteractionResult.SUCCESS;
        } else {
            var useOnContext = new UseOnContext(player, hand, blockHitResult);
            if (event.getUseItem() != TriState.FALSE) {
                var result = item.onItemUseFirst(useOnContext);
                if (result != InteractionResult.PASS) {
                    return result;
                }
            }

            var direct = !player.getMainHandItem().doesSneakBypassUse(player.level(), blockPos, player) || !player.getOffhandItem().doesSneakBypassUse(player.level(), blockPos, player);
            var active = player.isSecondaryUseActive() && direct;
            if (event.getUseBlock().isTrue() || event.getUseBlock().isDefault() && !active) {
                var blockState = fakeClientLevel.getBlockState(blockPos);
                if (!mc.getConnection().isFeatureEnabled(blockState.getBlock().requiredFeatures())) {
                    return InteractionResult.FAIL;
                }

                var itemInteractionResult = blockState.useItemOn(player.getItemInHand(hand), fakeClientLevel, player, hand, blockHitResult);
                if (itemInteractionResult.consumesAction()) {
                    return itemInteractionResult.result();
                }

                if (itemInteractionResult == ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION && hand == InteractionHand.MAIN_HAND) {
                    var interactionResult = blockState.useWithoutItem(fakeClientLevel, player, blockHitResult);
                    if (interactionResult.consumesAction()) {
                        return interactionResult;
                    }
                }
            }

            if (event.getUseItem().isFalse()) {
                return InteractionResult.PASS;
            } else if (event.getUseItem().isTrue() || !item.isEmpty() && !player.getCooldowns().isOnCooldown(item.getItem())) {
                return item.useOn(useOnContext);
            }
            return InteractionResult.PASS;
        }
    }

    private static InteractionResult usesItem() {
        for (var hand : InteractionHand.values()) {
            var item = fakeLocalPlayer.getItemInHand(hand);
            if (item.isEmpty()) continue;

            var interactionResult = useItem(fakeLocalPlayer, hand);
            if (interactionResult.consumesAction()) return interactionResult;
        }

        return InteractionResult.PASS;
    }

    @SuppressWarnings({"DataFlowIssue", "SameParameterValue"})
    private static InteractionResult useItem(Player player, InteractionHand hand) {
        var mc = Minecraft.getInstance();
        var gameMode = mc.gameMode;
        if (gameMode.getPlayerMode() == GameType.SPECTATOR) return InteractionResult.PASS;

        var item = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(item.getItem())) return InteractionResult.PASS;

        var cancelResult = CommonHooks.onItemRightClick(player, hand);
        if (cancelResult != null) return cancelResult;

        var interactionResultHolder = item.use(fakeClientLevel, player, hand);
        return interactionResultHolder.getResult();
    }
}
