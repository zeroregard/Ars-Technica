package net.mcreator.ars_technica.datagen;

import net.mcreator.ars_technica.common.blocks.SourceEngineBlock;
import net.mcreator.ars_technica.setup.BlockRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModBlockStateProvider extends BlockStateProvider {

    public ModBlockStateProvider(PackOutput output, String modid, ExistingFileHelper exFileHelper) {
        super(output, modid, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        directionalBlockWithCustomModel(BlockRegistry.SOURCE_ENGINE.get());
    }

    private void directionalBlockWithCustomModel(Block block) {
        for (Direction direction : Direction.values()) {
            BlockState state = block.defaultBlockState().setValue(SourceEngineBlock.FACING, direction);
            ModelFile modelFile = getModelFile(state);
            int xRot = getXRotation(state);
            int yRot = getYRotation(state);
            getVariantBuilder(block)
                    .partialState().with(SourceEngineBlock.FACING, direction)
                    .modelForState()
                    .modelFile(modelFile)
                    .rotationX(xRot)
                    .rotationY(yRot)
                    .addModel();
        }
    }

    private ModelFile getModelFile(BlockState state) {
        if (state.getValue(SourceEngineBlock.FACING).getAxis().isVertical()) {
            return models().getExistingFile(modLoc("block/source_engine/block_vertical"));
        } else {
            return models().getExistingFile(modLoc("block/source_engine/block"));
        }
    }

    private int getXRotation(BlockState state) {
        return state.getValue(SourceEngineBlock.FACING) == Direction.DOWN ? 180 : 0;
    }

    private int getYRotation(BlockState state) {
        return state.getValue(SourceEngineBlock.FACING)
                .getAxis()
                .isVertical() ? 0 : horizontalAngle(state.getValue(SourceEngineBlock.FACING));
    }

    protected int horizontalAngle(Direction direction) {
        if (direction.getAxis()
                .isVertical())
            return 0;
        return (int) direction.toYRot();
    }
}
