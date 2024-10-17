package net.mcreator.ars_technica.common.blocks.turrets;

import java.util.function.Supplier;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.spell.TurretSpellCaster;
import com.hollingsworth.arsnouveau.common.block.BasicSpellTurret;
import com.hollingsworth.arsnouveau.common.block.tile.BasicSpellTurretTile;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.simibubi.create.content.decoration.encasing.EncasedBlock;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import static net.mcreator.ars_technica.setup.BlockRegistry.ANDESITE_ENCASED_TURRET_BLOCK;

public class EncasedTurretBlock extends BasicSpellTurret implements EncasedBlock, IWrenchable {
    public static final BooleanProperty ENCLOSED = BooleanProperty.create("enclosed");

    private final Supplier<Block> casing;

    public EncasedTurretBlock(Properties p_49795_, Supplier<Block> casing) {
        super(p_49795_);
        this.casing = casing;
        this.registerDefaultState(this.stateDefinition.any().setValue(ENCLOSED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ENCLOSED);
    }

    @Override
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        if (context.getLevel().isClientSide)
            return InteractionResult.SUCCESS;
        context.getLevel()
                .levelEvent(2001, context.getClickedPos(), Block.getId(state));
        removeEncasing(state, context.getLevel(), context.getClickedPos());
        return InteractionResult.SUCCESS;
    }

    public void removeEncasing(BlockState state, Level level, BlockPos pos) {

        TurretSpellCaster caster = null;
        BlockEntity oldBlockEntity = level.getBlockEntity(pos);
        if (oldBlockEntity instanceof EncasedTurretBlockEntity blockEntity) {
            caster = (TurretSpellCaster)blockEntity.getSpellCaster();
        }

        BlockState newState = BlockRegistry.BASIC_SPELL_TURRET.defaultBlockState().setValue(BasicSpellTurret.FACING, state.getValue(BasicSpellTurret.FACING));
        level.setBlock(pos, newState, Block.UPDATE_ALL);

        BlockEntity newBlockEntity = level.getBlockEntity(pos);

        if (newBlockEntity instanceof BasicSpellTurretTile blockEntity && caster != null) {
            blockEntity.spellCaster.copyFromCaster(caster);
            blockEntity.spellCaster.setSpell(caster.getSpell().clone());
            blockEntity.updateBlock();
            level.sendBlockUpdated(pos, state, state, 2);
        }

    }

    @Override
    public Block getCasing() {
        return casing.get();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EncasedTurretBlockEntity(pos, state);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return block_shape;
    }

    // minecraft did not understand how to make the lighting work with a perfect box, beats me :D
    static final VoxelShape block_shape = Block.box(0.001D, 0.001D, 0.001D, 15.999D, 15.999D, 15.999D);

}