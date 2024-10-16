package net.mcreator.ars_technica.mixin;

import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.spell.TurretSpellCaster;
import com.hollingsworth.arsnouveau.common.block.BasicSpellTurret;
import com.hollingsworth.arsnouveau.common.block.tile.BasicSpellTurretTile;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.decoration.encasing.EncasableBlock;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import net.mcreator.ars_technica.common.blocks.turrets.EncasedTurretBlock;
import net.mcreator.ars_technica.common.blocks.turrets.EncasedTurretBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.mcreator.ars_technica.setup.BlockRegistry.ANDESITE_ENCASED_TURRET_BLOCK;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

@Mixin(BasicSpellTurret.class)
public abstract class BasicSpellTurretMixin implements EncasableBlock {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    public void useCasing(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit,  CallbackInfoReturnable<InteractionResult> cir) {
        if (player.isShiftKeyDown() || !player.mayBuild()) {
            cir.setReturnValue(InteractionResult.PASS);
            cir.cancel();
            return;
        }

        ItemStack heldItem = player.getItemInHand(handIn);
        InteractionResult result = tryEncase(state, worldIn, pos, heldItem, player, handIn, hit);
        if (result.consumesAction()) {
            cir.setReturnValue(result);
            cir.cancel();
        }
    }

    public InteractionResult tryEncase(BlockState state, Level level, BlockPos pos, ItemStack heldItem, Player player, InteractionHand hand,
                                       BlockHitResult ray) {
        if (isEncasingItem(heldItem)) {

            TurretSpellCaster caster = null;
            BlockEntity oldBlockEntity = level.getBlockEntity(pos);
            if (oldBlockEntity instanceof BasicSpellTurretTile basicSpellTurretTile) {
                caster = (TurretSpellCaster)basicSpellTurretTile.getSpellCaster();
            }

            BlockState newState = ANDESITE_ENCASED_TURRET_BLOCK.get().defaultBlockState()
                    .setValue(EncasedTurretBlock.ENCLOSED, true);
            newState = copyProperties(state, newState);
            level.setBlock(pos, newState, Block.UPDATE_ALL);

            BlockEntity newBlockEntity = level.getBlockEntity(pos);

            if (newBlockEntity instanceof EncasedTurretBlockEntity blockEntity && caster != null) {
                blockEntity.spellCaster.copyFromCaster(caster);
                blockEntity.spellCaster.setSpell(caster.getSpell().clone());
                blockEntity.updateBlock();
                level.sendBlockUpdated(pos, state, state, 2);
            }


            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    private BlockState copyProperties(BlockState oldState, BlockState newState) {
        return newState.setValue(BasicSpellTurret.TRIGGERED, oldState.getValue(BasicSpellTurret.TRIGGERED))
                .setValue(WATERLOGGED, oldState.getValue(WATERLOGGED))
                .setValue(BasicSpellTurret.FACING, oldState.getValue(BasicSpellTurret.FACING));
    }

    private boolean isEncasingItem(ItemStack stack) {
        return stack.getItem() == AllBlocks.ANDESITE_CASING.get().asItem();
    }
}