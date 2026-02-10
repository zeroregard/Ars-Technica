package com.zeroregard.ars_technica.mixin;

import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.block.Relay;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
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
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

import static com.simibubi.create.foundation.utility.BlockHelper.copyProperties;

@Mixin(Relay.class)
public abstract class RelayBlockMixin implements IArsTechnicaWrenchAdjustable, IWrenchable {

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        if (player == null)
            return InteractionResult.PASS;
        // Only base Source Relay and PreciseRelay react; other subclasses (e.g. Creative Relay) do nothing
        Class<?> blockClass = ((Object) this).getClass();
        if (blockClass != Relay.class && blockClass != PreciseRelay.class)
            return InteractionResult.PASS;
        ((IArsTechnicaWrenchAdjustable) this).handleWrenching(level, pos, player);
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        return InteractionResult.PASS;
    }

    public void handleWrenching(Level world, BlockPos pos, Player player) {
        // Only the base Source Relay may be converted; subclasses (e.g. Creative Relay) must not
        if (((Object) this).getClass() != Relay.class)
            return;
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