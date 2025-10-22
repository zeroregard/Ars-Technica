package com.zeroregard.ars_technica.block;

import com.alexthw.sauce.common.block.FocusEnhancedSpellTurret;
import com.hollingsworth.arsnouveau.api.spell.SpellSchools;
import com.zeroregard.ars_technica.registry.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class TransmutationTurretBlock extends FocusEnhancedSpellTurret {

    public TransmutationTurretBlock(Properties properties) {
        super(properties, SpellSchools.MANIPULATION);
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TransmutationTurretTile(pos, state);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return Collections.singletonList(new ItemStack(this));
    }
}