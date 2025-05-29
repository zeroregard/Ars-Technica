package com.zeroregard.ars_technica.client.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.content.schematics.cannon.SchematicannonBlockEntity;
import com.simibubi.create.content.schematics.cannon.SchematicannonRenderer;
import com.zeroregard.ars_technica.api.ITechnomancerAware;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class ArcaneSchematiccannonRenderer extends SchematicannonRenderer {

    private static float accumulatedTime = 0.0f;
    public ArcaneSchematiccannonRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(SchematicannonBlockEntity blockEntity, float partialTicks, PoseStack ms,
                              MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(blockEntity, partialTicks, ms, buffer, light, overlay);
        
        if (blockEntity instanceof ITechnomancerAware technomancerAware && technomancerAware.isTechnomancerNearby()) {
            accumulatedTime += Minecraft.getInstance().getFrameTimeNs() / 1000000000f;
            renderCannonModel(blockEntity, partialTicks, ms, buffer, light, overlay);
        }
    }

    private void renderCannonModel(SchematicannonBlockEntity blockEntity, float partialTicks, PoseStack ms,
                                   MultiBufferSource buffer, int light, int overlay) {
        BlockState state = blockEntity.getBlockState();
        BlockPos pos = blockEntity.getBlockPos();

        double[] cannonAngles = getCannonAngles(blockEntity, pos, partialTicks);
        double yaw = cannonAngles[0];
        double pitch = cannonAngles[1];
        double recoil = getRecoil(blockEntity, partialTicks);

        Minecraft mc = Minecraft.getInstance();
        Vec3 camPos = mc.gameRenderer.getMainCamera().getPosition();
        double distSq = camPos.distanceToSqr(Vec3.atCenterOf(pos));
        double distance = Math.sqrt(distSq);
        float alpha = 1.0f;

        if (distance > 4) {
            alpha = (float) Mth.clamp(1.0 - ((distance - 4) / 4.0), 0.0, 1.0);
        }
        int alphaInt = Math.round(255 * alpha);

        ms.pushPose();


        var translateX = .5f + 0.005 * Math.sin(accumulatedTime * 30);
        var translateZ = .5f + 0.005 * Math.cos(accumulatedTime * 30);
        SuperByteBuffer pipe = CachedBuffers.partial(AllPartialModels.ARCANE_PIPE, state);
        pipe.translate(.5f, 15 / 16f, .5f);
        pipe.scale(1.05f);
        pipe.rotate((float) ((yaw + 90) / 180 * Math.PI), Direction.UP);
        pipe.rotate((float) (pitch / 180 * Math.PI), Direction.SOUTH);
        pipe.translate(-translateX, -15 / 16f, -translateZ);
        pipe.translate(0, -recoil / 100, 0);
        pipe.light(LightTexture.FULL_BRIGHT)
                .color(255, 255, 255, alphaInt)
                .renderInto(ms,  buffer.getBuffer(RenderType.translucentMovingBlock()));
        ms.popPose();
    }

}
