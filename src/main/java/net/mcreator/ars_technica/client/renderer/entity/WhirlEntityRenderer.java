package net.mcreator.ars_technica.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.mcreator.ars_technica.common.entity.WhirlEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.MultiBufferSource;
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
    public void render(WhirlEntity entity, float entityYaw, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);

        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();

        // Debugging: Ensure the method is being called
        Minecraft.getInstance().getProfiler().push("renderBlock");

        poseStack.pushPose();
        poseStack.translate(entity.getX() - 0.5, entity.getY(), entity.getZ() - 0.5);  // Adjust position
        blockRenderer.renderSingleBlock(Blocks.STONE.defaultBlockState(), poseStack, bufferSource, packedLight, net.minecraft.client.renderer.LightTexture.FULL_BRIGHT);
        poseStack.popPose();

        Minecraft.getInstance().getProfiler().pop(); // End profiling
    }

}
