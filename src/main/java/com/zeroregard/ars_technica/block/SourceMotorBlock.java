package com.zeroregard.ars_technica.block;

import com.simibubi.create.AllItems;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import com.zeroregard.ars_technica.client.gui.SourceMotorScreen;
import com.zeroregard.ars_technica.registry.EntityRegistry;
import com.zeroregard.ars_technica.registry.ItemRegistry;
import net.createmod.catnip.gui.ScreenOpener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class SourceMotorBlock extends DirectionalKineticBlock implements IBE<SourceMotorBlockEntity> {

    public SourceMotorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return AllShapes.MOTOR_BLOCK.get(state.getValue(FACING));
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return Collections.singletonList(new ItemStack(ItemRegistry.SOURCE_MOTOR.get()));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction preferred = getPreferredFacing(context);
        if ((context.getPlayer() != null && context.getPlayer()
                .isShiftKeyDown()) || preferred == null)
            return super.getStateForPlacement(context);
        return defaultBlockState().setValue(FACING, preferred);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (AllItems.WRENCH.isIn(player.getItemInHand(hand)))
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult) {
        if(level.isClientSide()) {
            withBlockEntityDo(level, pos, be -> this.displayScreen(be, player));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.SUCCESS;
    }

    @OnlyIn(value = Dist.CLIENT)
    protected void displayScreen(SourceMotorBlockEntity be, Player player) {
        if (player instanceof LocalPlayer)
            ScreenOpener.open(new SourceMotorScreen(be));
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == state.getValue(FACING);
    }

    @Override
    public Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING)
                .getAxis();
    }

    @Override
    public boolean hideStressImpact() {
        return true;
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, world, pos, oldState, isMoving);
        checkForPower(world, pos);
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, world, pos, block, fromPos, isMoving);
        checkForPower(world, pos);
    }

    private void checkForPower(Level world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof SourceMotorBlockEntity) {
            boolean isPowered = world.hasNeighborSignal(pos);
            ((SourceMotorBlockEntity) blockEntity).setPowered(isPowered);
        }
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    public Class<SourceMotorBlockEntity> getBlockEntityClass() {
        return SourceMotorBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends SourceMotorBlockEntity> getBlockEntityType() {
        return EntityRegistry.SOURCE_MOTOR_BLOCK_ENTITY.get();
    }

}