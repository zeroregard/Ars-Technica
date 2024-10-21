package net.mcreator.ars_technica.mixin.Wrench;

import com.hollingsworth.arsnouveau.common.block.ArcanePedestal;
import com.hollingsworth.arsnouveau.common.block.MobJar;
import com.hollingsworth.arsnouveau.common.block.tile.ArcanePedestalTile;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import net.mcreator.ars_technica.common.items.equipment.RunicSpanner;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArcanePedestal.class)
public abstract class PedestalMixin implements IWrenchable {

    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        ItemStack stack = context.getPlayer().getItemInHand(context.getHand());
        if (stack.getItem() instanceof RunicSpanner) {
            Level world = context.getLevel();
            BlockPos pos = context.getClickedPos();
            Player player = context.getPlayer();
            if (world instanceof ServerLevel) {
                simulateBlockMining(state, world, pos, player);
                playRemoveSound(world, pos);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    private void simulateBlockMining(BlockState state, Level world, BlockPos pos, Player player) {
        if (!world.isClientSide) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof ArcanePedestalTile pedestal) {
                var pedestalItem = pedestal.getItem(0);
                if(pedestalItem != null) {
                    pedestalItem = pedestal.removeItem(0, 1);
                    var itemEntity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), pedestalItem);
                    world.addFreshEntity(itemEntity);
                }

                var pedestalAsItem = new ItemStack(state.getBlock().asItem());

                state.getBlock().playerDestroy(world, player, pos, state, blockEntity, pedestalAsItem);
                world.removeBlock(pos, false);
            }
        }
    }
}
