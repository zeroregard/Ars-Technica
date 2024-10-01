package net.mcreator.ars_technica.common.blocks;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;

import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

public class SourceEngineRenderer extends KineticBlockEntityRenderer<SourceEngineBlockEntity> {


    public SourceEngineRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected SuperByteBuffer getRotatedModel(SourceEngineBlockEntity be, BlockState state) {
        return CachedBufferer.partialFacing(AllPartialModels.SHAFT_HALF, state);
    }

    @Override
    public void renderSafe(SourceEngineBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay) {


        super.renderSafe(blockEntity, partialTicks, poseStack, bufferSource, light, overlay);
    }

}