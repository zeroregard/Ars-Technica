package net.mcreator.ars_technica.common.blocks;

import com.jozufozu.flywheel.backend.Backend;
import com.mojang.blaze3d.vertex.PoseStack;

import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import net.mcreator.ars_technica.ArsTechnicaMod;
import net.mcreator.ars_technica.client.AllPartialModels;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.joml.Quaternionf;

public class SourceEngineRenderer extends KineticBlockEntityRenderer<SourceEngineBlockEntity> {
    public SourceEngineRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected SuperByteBuffer getRotatedModel(SourceEngineBlockEntity be, BlockState state) {
        return CachedBufferer.partialFacing(AllPartialModels.ARCANE_SHAFT_HALF, state);
    }

    @Override
    public void renderSafe(SourceEngineBlockEntity be, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay) {

        super.renderSafe(be, partialTicks, poseStack, bufferSource, light, overlay);
        BlockState state = getRenderedBlockState(be);

        if (be.isFueled()) {

            Direction.Axis axis = ((IRotate) be.getBlockState()
                    .getBlock()).getRotationAxis(be.getBlockState());
            float angle = getAngleForTe(be, be.getBlockPos(), axis);
            SuperByteBuffer shaft = getRotatedModel(be, state);
            shaft.light(light);
            shaft.rotateCentered(Direction.get(Direction.AxisDirection.POSITIVE, axis), angle);
            shaft.renderInto(poseStack, bufferSource.getBuffer(RenderType.translucent()));
        }

    }

}