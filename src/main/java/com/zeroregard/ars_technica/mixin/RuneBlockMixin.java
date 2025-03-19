package com.zeroregard.ars_technica.mixin;

import com.hollingsworth.arsnouveau.common.block.RuneBlock;
import com.hollingsworth.arsnouveau.common.block.tile.RuneTile;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.zeroregard.ars_technica.client.gui.RuneTileScreen;
import com.zeroregard.ars_technica.helpers.InteractionHelper;
import com.zeroregard.ars_technica.item.RunicSpanner;
import net.createmod.catnip.gui.ScreenOpener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RuneBlock.class)
public class RuneBlockMixin implements IWrenchable {

    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    public void useWrench(ItemStack stack, BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit, CallbackInfoReturnable<ItemInteractionResult> cir) {
        if (stack.getItem() instanceof RunicSpanner) {
            UseOnContext context = new UseOnContext(player, handIn, hit);
            InteractionResult result = onWrenched(state, context);
            ItemInteractionResult itemResult = InteractionHelper.fromInteractionResult(result);
            cir.setReturnValue(itemResult);
            cir.cancel();
        }
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (world.getBlockEntity(pos) instanceof RuneTile runeTile) {
            if(world.isClientSide()) {
                displayScreen(runeTile, context.getPlayer());
            }
            world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.AMETHYST_BLOCK_STEP, SoundSource.BLOCKS, 0.25f, 1.0f);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    @OnlyIn(value = Dist.CLIENT)
    protected void displayScreen(RuneTile be, Player player) {
        if (player instanceof LocalPlayer)
            ScreenOpener.open(new RuneTileScreen(be));
    }

}