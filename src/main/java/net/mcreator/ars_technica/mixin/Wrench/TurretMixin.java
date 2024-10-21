package net.mcreator.ars_technica.mixin.Wrench;

import com.hollingsworth.arsnouveau.common.block.BasicSpellTurret;
import com.hollingsworth.arsnouveau.common.block.tile.ArcanePedestalTile;
import com.hollingsworth.arsnouveau.common.block.tile.BasicSpellTurretTile;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import net.mcreator.ars_technica.ArsTechnicaMod;
import net.mcreator.ars_technica.common.items.equipment.RunicSpanner;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BasicSpellTurret.class)
public abstract class TurretMixin implements IWrenchable {
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        ItemStack stack = context.getPlayer().getItemInHand(context.getHand());
        if (stack.getItem() instanceof RunicSpanner) {
            Level world = context.getLevel();
            BlockPos pos = context.getClickedPos();
            Player player = context.getPlayer();
            simulateBlockMining(state, world, pos, player);
            playRemoveSound(world, pos);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    private void simulateBlockMining(BlockState state, Level world, BlockPos pos, Player player) {
        if (!world.isClientSide) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof BasicSpellTurretTile turret) {
                var turretItem = new ItemStack(state.getBlock().asItem());
                state.getBlock().playerDestroy(world, player, pos, state, blockEntity, turretItem);
                world.removeBlock(pos, false);
            }
        }
    }
}