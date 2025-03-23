package com.zeroregard.ars_technica.helpers.mixin.droppers;


import com.hollingsworth.arsnouveau.common.block.tile.MobJarTile;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ContainmentJarDropper implements IDropper {
    public static final ContainmentJarDropper INSTANCE = new ContainmentJarDropper();
    ContainmentJarDropper() {

    }

    @Override
    public void dropItem(Level world, BlockPos pos, BlockState state, Player player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity instanceof MobJarTile && world instanceof ServerLevel serverLevel) {
            var drops = state.getBlock().getDrops(state, serverLevel, pos, blockEntity);

            for (ItemStack drop : drops) {
                if (!drop.isEmpty()) {
                    player.getInventory().add(drop);
                }
            }
        }
    }
}