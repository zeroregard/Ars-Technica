package net.mcreator.ars_technica.common.blocks;

import com.mojang.blaze3d.vertex.PoseStack;

import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
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

    private float getRadiansAngle(SourceEngineBlockEntity be, final BlockPos pos) {
        Direction.Axis axis = ((IRotate) be.getBlockState()
                .getBlock()).getRotationAxis(be.getBlockState());

        float time = AnimationTickHolder.getRenderTime(be.getLevel());
        float offset = getRotationOffsetForPosition(be, pos, axis);
        float angle = ((time * be.getSpeed() * 3f / 10 + offset) % 360) / 180 * (float) Math.PI;
        return angle;
    }

    @Override
    public void renderSafe(SourceEngineBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay) {

        super.renderSafe(blockEntity, partialTicks, poseStack, bufferSource, light, overlay);

        return;

        // Unfortunately the below is unsafe when used with modernfix mixins
        /*
        if (blockEntity.isFueled()) {

            poseStack.pushPose();
            float radiansAngle = getRadiansAngle(blockEntity, blockEntity.getBlockPos());

            poseStack.translate(0.5, 0.5, 0.5);
            poseStack.mulPose(getQuaternion(blockEntity, radiansAngle));
            poseStack.translate(-0.5, -0.5, -0.5);

            SuperByteBuffer shaft = getRotatedModel(blockEntity, blockEntity.getBlockState());
            shaft.renderInto(poseStack, bufferSource.getBuffer(RenderType.translucent()));

            poseStack.popPose();
        }
        */
    }

    private Quaternionf getQuaternion(SourceEngineBlockEntity be, float radiansAngle) {
        Direction facing = be.getBlockState().getValue(BlockStateProperties.FACING);
        Quaternionf rotation = new Quaternionf();

        switch (facing) {
            case EAST, WEST:
                rotation.rotateX(radiansAngle);
                break;
            case UP, DOWN:
                rotation.rotateY(radiansAngle);
                break;
            case NORTH, SOUTH:
                rotation.rotateZ(radiansAngle);
                break;
        }
        return rotation;
    }

}