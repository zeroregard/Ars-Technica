package net.mcreator.ars_technica.mixin.Wrench;

import com.hollingsworth.arsnouveau.common.block.MobJar;
import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import net.mcreator.ars_technica.common.items.equipment.RunicSpanner;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobJar.class)
public abstract class ContainmentJarMixin implements IWrenchable {
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
            if (blockEntity instanceof MobJarTile mobJar) {
                Entity entity = mobJar.getEntity();
                ItemStack jarItem = new ItemStack(state.getBlock().asItem());

                if (entity != null) {
                    CompoundTag entityTag = new CompoundTag();
                    entity.save(entityTag);
                    jarItem.setTag(entityTag);
                }

                state.getBlock().playerDestroy(world, player, pos, state, blockEntity, jarItem);
                world.removeBlock(pos, false);
            }
        }
    }


}
