package com.zeroregard.ars_technica.mixin;

import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.block.Relay;
import com.zeroregard.ars_technica.Config;
import com.zeroregard.ars_technica.block.PreciseRelay;
import com.zeroregard.ars_technica.block.PreciseRelayTile;
import com.zeroregard.ars_technica.helpers.mixin.IArsTechnicaWrenchAdjustable;
import com.zeroregard.ars_technica.network.ParticleEffectPacket;
import com.zeroregard.ars_technica.registry.BlockRegistry;
import com.zeroregard.ars_technica.registry.ParticleRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;

import static com.simibubi.create.foundation.utility.BlockHelper.copyProperties;

@Mixin(Relay.class)
public abstract class RelayBlockMixin implements IArsTechnicaWrenchAdjustable {
    public void handleWrenching(Level world, BlockPos pos, Player player) {
        if (!world.isClientSide) {
            HolderLookup.Provider registries = world.registryAccess();
            BlockState oldState = world.getBlockState(pos);
            BlockEntity oldBlockEntity = world.getBlockEntity(pos);

            CompoundTag oldNBT = oldBlockEntity != null ? oldBlockEntity.saveWithoutMetadata(registries) : null;

            PreciseRelay newBlock = BlockRegistry.PRECISE_RELAY.get();
            BlockState newState = newBlock.defaultBlockState();
            newState = copyProperties(oldState, newState);

            PreciseRelayTile newBlockEntity = new PreciseRelayTile(pos, newState);
            world.destroyBlock(pos, false);
            world.removeBlockEntity(pos);

            world.setBlock(pos, newState, Block.UPDATE_ALL);
            world.setBlockEntity(newBlockEntity);

            if (oldNBT != null) {
                newBlockEntity.loadWithComponents(oldNBT, registries);
            }

            newBlockEntity.setCooldownTicks(Config.Common.RELAY_MIN_COOLDOWN_VALUE.get());

            newBlockEntity.setChanged();

            for (int i = 0; i < 7; i++) {
                ParticleEffectPacket.send(world, ParticleColor.fromInt(ParticleColor.PURPLE.getColor()), ParticleRegistry.SPIRAL_DUST_TYPE.get(), pos.getCenter());
            }

        }
    }

}