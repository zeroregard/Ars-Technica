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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
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

        ItemStack stack = entityIn.getStack();
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, 15728880, OverlayTexture.NO_OVERLAY, matrixStack, bufferIn, entityIn.level(), (int) entityIn.blockPosition().asLong());
        matrixStack.popPose();

        // Render small fluid block for preserved containers
        if (isPreservedContainer(stack)) {
            renderFluidBlock(matrixStack, bufferIn, packedLightIn, getFluidFromContainer(stack));
        }

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

    private boolean isPreservedContainer(ItemStack stack) {
        if (stack.isEmpty()) return false;
        
        // Check if this is an empty container that was likely preserved
        return stack.getItem() == Items.GLASS_BOTTLE || 
               stack.getItem() == Items.BUCKET || 
               stack.getItem() == Items.BOWL;
    }
    
    private FluidStack getFluidFromContainer(ItemStack container) {
        // This is a simplified approach - in a real implementation you'd need to track
        // what fluid was originally in the container. For now, we'll use a default.
        if (container.getItem() == Items.GLASS_BOTTLE) {
            return new FluidStack(Fluids.WATER, 1000);
        } else if (container.getItem() == Items.BUCKET) {
            return new FluidStack(Fluids.WATER, 1000);
        }
        return FluidStack.EMPTY;
    }
    
    private void renderFluidBlock(PoseStack matrixStack, MultiBufferSource bufferIn, int packedLightIn, FluidStack fluid) {
        if (fluid.isEmpty()) return;
        
        matrixStack.pushPose();
        matrixStack.translate(0, 0.1, 0); // Slightly above the container
        matrixStack.scale(0.5f, 0.5f, 0.5f); // Half size block
        
        // Simple fluid block rendering - this would need to be enhanced for proper fluid textures
        VertexConsumer buffer = bufferIn.getBuffer(RenderType.entityTranslucent(ResourceLocation.fromNamespaceAndPath("minecraft", "textures/block/water_still.png")));
        Matrix4f pose = matrixStack.last().pose();
        
        // Render a simple cube for the fluid
        float size = 0.3f;
        int color = 0xFF4A90E2; // Water blue color
        
        // Top face
        buffer.addVertex(pose, -size, size, -size).setColor(color).setUv(0, 0).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(packedLightIn);
        buffer.addVertex(pose, size, size, -size).setColor(color).setUv(1, 0).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(packedLightIn);
        buffer.addVertex(pose, size, size, size).setColor(color).setUv(1, 1).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(packedLightIn);
        buffer.addVertex(pose, -size, size, size).setColor(color).setUv(0, 1).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(packedLightIn);
        
        matrixStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull ItemProjectileEntity entity) {
        return ResourceLocation.fromNamespaceAndPath("ars_nouveau", "textures/entity/spell_proj.png");
    }
}