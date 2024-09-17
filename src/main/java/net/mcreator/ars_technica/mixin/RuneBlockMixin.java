package net.mcreator.ars_technica.mixin;

import com.hollingsworth.arsnouveau.common.block.RuneBlock;
import com.hollingsworth.arsnouveau.common.block.tile.RuneTile;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.equipment.wrench.WrenchItem;
import com.simibubi.create.content.schematics.cannon.SchematicannonBlockEntity;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.LangBuilder;
import net.mcreator.ars_technica.ArsTechnicaMod;
import net.mcreator.ars_technica.common.api.IRuneTileModifier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(RuneBlock.class)
public class RuneBlockMixin implements IWrenchable, IHaveGoggleInformation {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    public void useWrench(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack stack = player.getItemInHand(handIn);
        if (stack.getItem() instanceof WrenchItem) {
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
            if (runeTile instanceof IRuneTileModifier modifiableRune) {
                modifiableRune.setCustomTicksUntilCharge(500);
                ArsTechnicaMod.LOGGER.info("Set custom charge of 500 ticks");
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.FAIL;
        }
        return InteractionResult.FAIL;
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        ArsTechnicaMod.LOGGER.info("HELLO GIVE ME TOOLTIPS");
        RuneBlock runeBlock = (RuneBlock) (Object) this;
        Lang.builder("ars_nouveau").add(runeBlock.getName()).forGoggles(tooltip);
        return true;
    }
}