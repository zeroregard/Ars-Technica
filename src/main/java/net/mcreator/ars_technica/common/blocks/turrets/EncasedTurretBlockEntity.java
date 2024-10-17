package net.mcreator.ars_technica.common.blocks.turrets;

import com.hollingsworth.arsnouveau.common.block.tile.BasicSpellTurretTile;
import net.mcreator.ars_technica.setup.EntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;

public class EncasedTurretBlockEntity extends BasicSpellTurretTile {
    public EncasedTurretBlockEntity(BlockPos pos, BlockState state) {
        super(EntityRegistry.ENCASED_TURRET_TILE.get(), pos, state);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {

    }
}