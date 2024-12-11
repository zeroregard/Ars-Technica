package net.mcreator.ars_technica.client.renderer.entity;

import com.hollingsworth.arsnouveau.client.registry.ShaderRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.mcreator.ars_technica.ArsTechnicaMod;
import net.mcreator.ars_technica.common.entity.ItemProjectileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Matrix4f;

public class ItemProjectileRenderer extends EntityRenderer<ItemProjectileEntity> {
    public static ResourceLocation TEXTURE = new ResourceLocation(ArsTechnicaMod.MODID,"textures/entity/magic_bubble.png");

    public ItemProjectileRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn);

    }

    @Override
    public void render(ItemProjectileEntity entityIn, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource bufferIn, int packedLightIn) {
        super.render(entityIn, entityYaw, partialTicks, matrixStack, bufferIn, packedLightIn);

        float time = (entityIn.tickCount + partialTicks) / 20.0f;
        float rotationAngle = (time) * 360.0f;

        matrixStack.pushPose();
        matrixStack.translate(0, 0.5, 0);
        matrixStack.mulPose(Axis.YP.rotationDegrees(rotationAngle));
        matrixStack.scale(0.25f, 0.25f, 0.25F);

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
        buffer.vertex(pose, -0.5f, -0.5f, 0.0f).color(255, 255, 255, 128).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(0, 1, 0).endVertex();
        buffer.vertex(pose, 0.5f, -0.5f, 0.0f).color(255, 255, 255, 128).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(0, 1, 0).endVertex();
        buffer.vertex(pose, 0.5f, 0.5f, 0.0f).color(255, 255, 255, 128).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(0, 1, 0).endVertex();
        buffer.vertex(pose, -0.5f, 0.5f, 0.0f).color(255, 255, 255, 128).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(0, 1, 0).endVertex();

        matrixStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(ItemProjectileEntity entity) {
        return new ResourceLocation("ars_nouveau","textures/entity/spell_proj.png");
    }
}