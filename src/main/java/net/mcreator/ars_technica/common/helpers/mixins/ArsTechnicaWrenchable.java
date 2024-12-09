package net.mcreator.ars_technica.common.helpers.mixins;


import net.mcreator.ars_technica.common.helpers.mixins.droppers.IDropper;
import net.mcreator.ars_technica.common.items.equipment.RunicSpanner;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class ArsTechnicaWrenchable {
    public static InteractionResult onSneakWrenched(BlockState state, UseOnContext context, IDropper itemDropper) {
        ItemStack stack = context.getPlayer().getItemInHand(context.getHand());
        if (stack.getItem() instanceof RunicSpanner) {
            Level world = context.getLevel();
            BlockPos pos = context.getClickedPos();
            Player player = context.getPlayer();
            Vec3 centerPos = pos.getCenter();

            itemDropper.dropItem(world, pos, state, player);

            if (world instanceof ServerLevel serverLevel) {
                BlockState blockState = world.getBlockState(pos);
                var data = new BlockParticleOption(ParticleTypes.BLOCK, blockState);
                serverLevel.sendParticles(data, centerPos.x, centerPos.y, centerPos.z, 30, 0.25, 0.25, 0.25, 0);
            }

            playRemoveSound(world, pos, state.getSoundType());
            world.removeBlock(pos, false);

            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    private static void playRemoveSound(Level world, BlockPos pos, SoundType soundType) {
        world.playSound(null, pos, soundType.getBreakSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
    }


}
