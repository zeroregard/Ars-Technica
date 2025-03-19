package com.zeroregard.ars_technica.item;

import com.simibubi.create.content.equipment.wrench.WrenchItem;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import com.zeroregard.ars_technica.client.item.RunicSpannerRenderer;
import com.zeroregard.ars_technica.helpers.mixin.IArsTechnicaWrenchAdjustable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class RunicSpanner extends WrenchItem {

    public RunicSpanner(Properties properties) {
        super(properties);
    }

    @Nonnull
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();

        if (player != null) {
            Block block = level.getBlockState(pos).getBlock();
            if (block instanceof IArsTechnicaWrenchAdjustable adjustableBlock) {
                adjustableBlock.handleWrenching(level, pos, player);
                return InteractionResult.SUCCESS;
            }
        }
        return super.useOn(context);
    }


    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new RunicSpannerRenderer()));
    }

}