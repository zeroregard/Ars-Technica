package net.mcreator.ars_technica.mixin.Relay;

import com.hollingsworth.arsnouveau.common.block.Relay;
import net.mcreator.ars_technica.common.blocks.PreciseRelay;
import net.mcreator.ars_technica.common.blocks.PreciseRelayTile;
import net.mcreator.ars_technica.common.helpers.mixins.IArsTechnicaWrenchAdjustable;

import net.mcreator.ars_technica.setup.BlockRegistry;
import net.minecraft.core.BlockPos;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

import static com.simibubi.create.foundation.utility.BlockHelper.copyProperties;

@Mixin(Relay.class)
public abstract class RelayBlockMixin implements IArsTechnicaWrenchAdjustable {
    public void handleWrenching(Level world, BlockPos pos, Player player) {
        if (!world.isClientSide) {
            BlockState oldState = world.getBlockState(pos);
            BlockEntity oldBlockEntity = world.getBlockEntity(pos);

            CompoundTag oldNBT = oldBlockEntity != null ? oldBlockEntity.saveWithoutMetadata() : null;

            PreciseRelay newBlock = BlockRegistry.PRECISE_RELAY.get();
            BlockState newState = newBlock.defaultBlockState();
            newState = copyProperties(oldState, newState);

            BlockEntity newBlockEntity = new PreciseRelayTile(pos, newState);
            world.destroyBlock(pos, false);
            world.removeBlockEntity(pos);

            world.setBlock(pos, newState, Block.UPDATE_ALL);
            world.setBlockEntity(newBlockEntity);

            if (oldNBT != null) {
                newBlockEntity.load(oldNBT);
            }

            newBlockEntity.setChanged();
        }
    }
}