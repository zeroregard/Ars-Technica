package com.zeroregard.ars_technica.block;

import com.hollingsworth.arsnouveau.common.block.BasicSpellTurret;
import com.zeroregard.ars_technica.registry.ItemRegistry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;

import java.util.Collections;
import java.util.List;

public class TransmutationTurretBlock extends BasicSpellTurret {

    public TransmutationTurretBlock(Properties properties) {
        super(properties);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return Collections.singletonList(new ItemStack(ItemRegistry.TRANSMUTATION_TURRET.get()));
    }
}