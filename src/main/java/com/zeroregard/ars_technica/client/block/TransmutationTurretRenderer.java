package com.zeroregard.ars_technica.client.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zeroregard.ars_technica.block.TransmutationTurretTile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

public class TransmutationTurretRenderer implements BlockEntityRenderer<TransmutationTurretTile> {

    public TransmutationTurretRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(TransmutationTurretTile tile, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay) {
        // Basic rendering - the turret will use the default block model
        // Additional effects can be added here if needed
    }
}