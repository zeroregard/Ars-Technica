package net.mcreator.ars_technica.mixin;

import com.hollingsworth.arsnouveau.common.block.RuneBlock;
import com.hollingsworth.arsnouveau.common.block.tile.RuneTile;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.gui.ScreenOpener;
import net.mcreator.ars_technica.client.gui.RuneTileScreen;
import net.mcreator.ars_technica.common.items.equipment.RunicSpanner;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RuneBlock.class)
public class RuneBlockMixin implements IWrenchable {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    public void useWrench(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack stack = player.getItemInHand(handIn);
        if (stack.getItem() instanceof RunicSpanner) {
            UseOnContext context = new UseOnContext(player, handIn, hit);
            InteractionResult result = onWrenched(state, context);
            cir.setReturnValue(result);
            cir.cancel();
        }
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (world.getBlockEntity(pos) instanceof RuneTile runeTile) {
            displayScreen(runeTile, context.getPlayer());
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