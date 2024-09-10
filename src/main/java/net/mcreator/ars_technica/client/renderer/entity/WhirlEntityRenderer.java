package net.mcreator.ars_technica.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.mcreator.ars_technica.ArsTechnicaMod;
import net.mcreator.ars_technica.common.entity.WhirlEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

public class WhirlEntityRenderer extends EntityRenderer<WhirlEntity> {

    public WhirlEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(WhirlEntity entity) {
        // No texture for now
        return null;
    }

    @Override
    public void render(WhirlEntity entity, float entityYaw, float partialTicks, @NotNull PoseStack poseStack, net.minecraft.client.renderer.@NotNull MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);

        // Get the Minecraft BlockRenderDispatcher
        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();

        // Render a stone block at the entity's position
        poseStack.pushPose();
        poseStack.translate(entity.getX() - 0.5, entity.getY(), entity.getZ() - 0.5);  // Adjust position
        blockRenderer.renderSingleBlock(Blocks.STONE.defaultBlockState(), poseStack, bufferSource, packedLight, net.minecraft.client.renderer.LightTexture.FULL_BRIGHT);
        poseStack.popPose();
    }

}
