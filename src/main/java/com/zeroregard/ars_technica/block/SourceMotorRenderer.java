package com.zeroregard.ars_technica.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.zeroregard.ars_technica.client.block.AllPartialModels;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class SourceMotorRenderer extends KineticBlockEntityRenderer<SourceMotorBlockEntity> {
    public SourceMotorRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected SuperByteBuffer getRotatedModel(SourceMotorBlockEntity be, BlockState state) {

        return CachedBuffers.partialFacing(AllPartialModels.ARCANE_SHAFT_HALF, state);
    }

    @Override
    public void renderSafe(SourceMotorBlockEntity be, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay) {

        super.renderSafe(be, partialTicks, poseStack, bufferSource, light, overlay);
        BlockState state = getRenderedBlockState(be);

        if (be.isFueled()) {

            Direction.Axis axis = ((IRotate) be.getBlockState()
                    .getBlock()).getRotationAxis(be.getBlockState());
            float angle = getAngleForBe(be, be.getBlockPos(), axis);
            SuperByteBuffer shaft = getRotatedModel(be, state);
            shaft.light(light);
            shaft.rotateCentered(angle, Direction.get(Direction.AxisDirection.POSITIVE, axis));
            shaft.renderInto(poseStack, bufferSource.getBuffer(RenderType.translucent()));
        }

    }

}