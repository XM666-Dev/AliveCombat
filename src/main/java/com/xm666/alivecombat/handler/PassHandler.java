package com.xm666.alivecombat.handler;

import com.xm666.alivecombat.AliveCombat;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
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
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.Tags;

@EventBusSubscriber(modid = AliveCombat.MODID, value = Dist.CLIENT)
public class PassHandler {
    public static boolean passEnabled = true;
    public static boolean passCollisionless = false;
    public static boolean passCollisionlessExtra = true;
    public static boolean passDead = true;
    public static boolean passAlly = true;

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
        var stack = entity instanceof LivingEntity living ? living.getMainHandItem() : null;
        return new ClipContext(from, to, block, fluid, collisionContext) {
            public VoxelShape getBlockShape(BlockState blockState, BlockGetter level, BlockPos pos) {
                var voxelShape = Block.OUTLINE.get(blockState, level, pos, collisionContext);
                var blockHitResult = level.clipWithInteractionOverride(from, to, pos, voxelShape, blockState);
                if (interactsBlock(blockHitResult)) return voxelShape;

                if (stack != null && stack.isCorrectToolForDrops(blockState) && isNotAxeForGrass(stack, blockState))
                    return voxelShape;

                return Block.COLLIDER.get(blockState, level, pos, collisionContext);
            }
        };
    }

    private static boolean isNotAxeForGrass(ItemStack stack, BlockState blockState) {
        var tool = stack.get(DataComponents.TOOL);
        if (tool == null) return true;

        var blocks = BuiltInRegistries.BLOCK.getOrCreateTag(BlockTags.MINEABLE_WITH_AXE);
        return tool.rules().stream().allMatch(rule -> rule.blocks() != blocks)
                || !blockState.is(BlockTags.MINEABLE_WITH_AXE)
                || !blockState.is(Blocks.SHORT_GRASS) && !blockState.is(Blocks.TALL_GRASS);
    }

    public static HitResult filterHitResult(HitResult hitResult, Vec3 pos) {
        var location = hitResult.getLocation();
        var direction = Direction.getNearest(location.x - pos.x, location.y - pos.y, location.z - pos.z);
        return BlockHitResult.miss(location, direction, BlockPos.containing(location));
    }

    public static boolean interacts(HitResult hitResult) {
        return interactsEntity(hitResult) || interactsBlock(hitResult) || interactsItem();
    }

    private static boolean interactsEntity(HitResult hitResult) {
        if (!(hitResult instanceof EntityHitResult entityHitResult)) return false;

        var target = entityHitResult.getEntity();
        var targetClass = target.getClass();
        return declaresMethod(targetClass, "interactAt", Player.class, Vec3.class, InteractionHand.class)
                || declaresMethod(targetClass, "interact", Player.class, InteractionHand.class)
                || declaresMethod(targetClass, "mobInteract", Player.class, InteractionHand.class)
                || interactsLivingEntity(target);
    }

    @SuppressWarnings("DataFlowIssue")
    private static boolean interactsLivingEntity(Entity target) {
        if (!(target instanceof LivingEntity)) return false;

        var mc = Minecraft.getInstance();
        var player = mc.player;
        for (var hand : InteractionHand.values()) {
            var stack = player.getItemInHand(hand);
            var item = stack.getItem();
            var itemClass = item.getClass();
            if (declaresMethod(itemClass, "interactLivingEntity", ItemStack.class, Player.class, LivingEntity.class, InteractionHand.class))
                return true;
        }

        return false;
    }

    @SuppressWarnings("DataFlowIssue")
    private static boolean interactsBlock(HitResult hitResult) {
        if (!(hitResult instanceof BlockHitResult blockHitResult)) return false;

        var mc = Minecraft.getInstance();
        var level = mc.level;
        var blockState = level.getBlockState(blockHitResult.getBlockPos());
        var block = blockState.getBlock();
        var blockClass = block.getClass();
        return usesItemFirst()
                || declaresMethod(blockClass, "useItemOn", ItemStack.class, BlockState.class, Level.class, BlockPos.class, Player.class, InteractionHand.class, BlockHitResult.class)
                || declaresMethod(blockClass, "useWithoutItem", BlockState.class, Level.class, BlockPos.class, Player.class, BlockHitResult.class);
        //|| usesItem();
    }

    @SuppressWarnings("DataFlowIssue")
    private static boolean usesItemFirst() {
        var mc = Minecraft.getInstance();
        var player = mc.player;
        for (var hand : InteractionHand.values()) {
            var stack = player.getItemInHand(hand);
            var item = stack.getItem();
            var itemClass = item.getClass();
            if (declaresMethod(itemClass, "onItemUseFirst", ItemStack.class, UseOnContext.class))
                return true;
        }

        return false;
    }

    @SuppressWarnings("DataFlowIssue")
    private static boolean usesItem() {
        var mc = Minecraft.getInstance();
        var player = mc.player;
        for (var hand : InteractionHand.values()) {
            var stack = player.getItemInHand(hand);
            var item = stack.getItem();
            var itemClass = item.getClass();
            if (declaresMethod(itemClass, "useOn", UseOnContext.class))
                return true;
        }

        return false;
    }

    @SuppressWarnings("DataFlowIssue")
    private static boolean interactsItem() {
        var mc = Minecraft.getInstance();
        var player = mc.player;
        for (var hand : InteractionHand.values()) {
            var stack = player.getItemInHand(hand);
            var item = stack.getItem();
            var itemClass = item.getClass();
            if (declaresMethod(itemClass, "use", Level.class, Player.class, InteractionHand.class))
                return true;
        }

        return false;
    }

    private static boolean declaresMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
        try {
            clazz.getDeclaredMethod(name, parameterTypes);
            return true;
        } catch (NoSuchMethodException exception) {
            return false;
        }
    }
}
