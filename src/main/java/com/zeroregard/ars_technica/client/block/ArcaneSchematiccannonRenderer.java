package com.zeroregard.ars_technica.client.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.schematics.cannon.LaunchedItem;
import com.simibubi.create.content.schematics.cannon.SchematicannonBlockEntity;
import com.simibubi.create.content.schematics.cannon.SchematicannonRenderer;
import com.zeroregard.ars_technica.api.ITechnomancerAware;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.createmod.ponder.render.VirtualRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
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
        accumulatedTime += Minecraft.getInstance().getFrameTimeNs() / 1000000000f;
        boolean blocksLaunching = !blockEntity.flyingBlocks.isEmpty();
        if (blocksLaunching)
            renderLaunchedBlocks(blockEntity, partialTicks, ms, buffer, light, overlay);
        if (blockEntity instanceof ITechnomancerAware technomancerAware && technomancerAware.isTechnomancerNearby()) {
            renderCannonModel(blockEntity, partialTicks, ms, buffer, light, overlay);
        }
        super.renderSafe(blockEntity, partialTicks, ms, buffer, light, overlay);

    }

    private static void renderLaunchedBlocks(SchematicannonBlockEntity blockEntity, float partialTicks, PoseStack ms,
                                             MultiBufferSource buffer, int light, int overlay) {
        for (LaunchedItem launched : blockEntity.flyingBlocks) {

            if (launched.ticksRemaining == 0)
                continue;

            // Calculate position of flying block
            Vec3 start = Vec3.atCenterOf(blockEntity.getBlockPos()
                    .above());
            Vec3 target = Vec3.atCenterOf(launched.target);
            Vec3 distance = target.subtract(start);

            double yDifference = target.y - start.y;
            double throwHeight = Math.sqrt(distance.lengthSqr()) * .6f + yDifference;
            Vec3 cannonOffset = distance.add(0, throwHeight, 0)
                    .normalize()
                    .scale(2);
            start = start.add(cannonOffset);
            yDifference = target.y - start.y;

            float progress =
                    ((float) launched.totalTicks - (launched.ticksRemaining + 1 - partialTicks)) / launched.totalTicks;
            Vec3 blockLocationXZ = target.subtract(start)
                    .scale(progress)
                    .multiply(1, 0, 1);

            // Height is determined through a bezier curve
            float t = progress;
            double yOffset = 2 * (1 - t) * t * throwHeight + t * t * yDifference;
            Vec3 blockLocation = blockLocationXZ.add(0.5, yOffset + 1.5, 0.5)
                    .add(cannonOffset);

            // Offset to position
            ms.pushPose();
            ms.translate(blockLocation.x, blockLocation.y, blockLocation.z);

            ms.translate(.125f, .125f, .125f);
            ms.mulPose(Axis.YP.rotationDegrees(360 * t));
            ms.mulPose(Axis.XP.rotationDegrees(360 * t));
            ms.translate(-.125f, -.125f, -.125f);

            if (launched instanceof LaunchedItem.ForBlockState) {
                // Render the Block
                BlockState state;
                if (launched instanceof LaunchedItem.ForBelt) {
                    // Render a shaft instead of the belt
                    state = AllBlocks.SHAFT.getDefaultState();
                } else {
                    state = ((LaunchedItem.ForBlockState) launched).state;
                }
                float scale = .3f;
                ms.scale(scale, scale, scale);
                Minecraft.getInstance()
                        .getBlockRenderer()
                        .renderSingleBlock(state, ms, buffer, light, overlay,
                                VirtualRenderHelper.VIRTUAL_DATA, null);
            } else if (launched instanceof LaunchedItem.ForEntity) {
                // Render the item
                float scale = 1.2f;
                ms.scale(scale, scale, scale);
                Minecraft.getInstance()
                        .getItemRenderer()
                        .renderStatic(launched.stack, ItemDisplayContext.GROUND, light, overlay, ms, buffer, blockEntity.getLevel(), 0);
            }

            ms.popPose();

            // Render particles for launch
            if (launched.ticksRemaining == launched.totalTicks && blockEntity.firstRenderTick) {
                start = start.subtract(.5, .5, .5);
                blockEntity.firstRenderTick = false;
                for (int i = 0; i < 10; i++) {
                    RandomSource r = blockEntity.getLevel()
                            .getRandom();
                    double sX = cannonOffset.x * .01f;
                    double sY = (cannonOffset.y + 1) * .01f;
                    double sZ = cannonOffset.z * .01f;
                    double rX = r.nextFloat() - sX * 40;
                    double rY = r.nextFloat() - sY * 40;
                    double rZ = r.nextFloat() - sZ * 40;
                    blockEntity.getLevel()
                            .addParticle(ParticleTypes.CLOUD, start.x + rX, start.y + rY, start.z + rZ, sX, sY, sZ);
                }
            }

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
