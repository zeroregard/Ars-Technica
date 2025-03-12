package com.zeroregard.ars_technica.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zeroregard.ars_technica.entity.ArcanePolishEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class ArcanePolishEntityRenderer extends ArcaneEntityRendererBase<ArcanePolishEntity> {

    public ArcanePolishEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new ArcanePolishModel());
    }

    @Override
    public void render(ArcanePolishEntity entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight) {
        matrixStack.pushPose();
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
        matrixStack.popPose();
    }

}
