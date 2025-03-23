package com.zeroregard.ars_technica.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.zeroregard.ars_technica.entity.ItemProjectileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class ItemProjectileRenderer extends EntityRenderer<ItemProjectileEntity> {
    public static ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("ars_nouveau", "textures/entity/bubble.png");

    public ItemProjectileRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn);

    }

    @Override
    public void render(@NotNull ItemProjectileEntity entityIn, float entityYaw, float partialTicks, @NotNull PoseStack matrixStack, @NotNull MultiBufferSource bufferIn, int packedLightIn) {
        super.render(entityIn, entityYaw, partialTicks, matrixStack, bufferIn, packedLightIn);

        float time = (entityIn.tickCount + partialTicks) / 20.0f;
        float rotationAngle = (time) * 360.0f;

        matrixStack.pushPose();
        matrixStack.translate(0, 0.5, 0);
        matrixStack.mulPose(Axis.YP.rotationDegrees(rotationAngle));
        matrixStack.scale(0.25f, 0.25f, 0.25F);

        if(entityIn.getStack() == null) {
            matrixStack.popPose();
            renderBubble(matrixStack, bufferIn, packedLightIn);
            return;
        }

        Minecraft.getInstance().getItemRenderer().renderStatic(entityIn.getStack(), ItemDisplayContext.FIXED, 15728880, OverlayTexture.NO_OVERLAY, matrixStack, bufferIn, entityIn.level(), (int) entityIn.blockPosition().asLong());
        matrixStack.popPose();

        renderBubble(matrixStack, bufferIn, packedLightIn);
    }

    private void renderBubble(PoseStack matrixStack, MultiBufferSource bufferIn, int packedLightIn) {
        matrixStack.pushPose();
        matrixStack.translate(0, 0.5, 0);
        matrixStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        float scale = 0.75f;
        matrixStack.scale(scale, scale, scale);
        VertexConsumer buffer = bufferIn.getBuffer(RenderType.entityTranslucentEmissive(TEXTURE, true));
        Matrix4f pose = matrixStack.last().pose();
        buffer.addVertex(pose, -0.5f, -0.5f, 0.0f).setColor(255, 255, 255, 128).setUv(0, 0).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(packedLightIn, packedLightIn >> 16).setNormal(0, 1, 0);
        buffer.addVertex(pose, 0.5f, -0.5f, 0.0f).setColor(255, 255, 255, 128).setUv(1, 0).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(packedLightIn, packedLightIn >> 16).setNormal(0, 1, 0);
        buffer.addVertex(pose, 0.5f, 0.5f, 0.0f).setColor(255, 255, 255, 128).setUv(1, 1).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(packedLightIn, packedLightIn >> 16).setNormal(0, 1, 0);
        buffer.addVertex(pose, -0.5f, 0.5f, 0.0f).setColor(255, 255, 255, 128).setUv(0, 1).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(packedLightIn, packedLightIn >> 16).setNormal(0, 1, 0);

        matrixStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull ItemProjectileEntity entity) {
        return ResourceLocation.fromNamespaceAndPath("ars_nouveau", "textures/entity/spell_proj.png");
    }
}