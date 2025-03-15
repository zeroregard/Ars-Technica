package com.zeroregard.ars_technica.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zeroregard.ars_technica.entity.fusion.ArcaneFusionEntity;
import com.zeroregard.ars_technica.entity.fusion.ArcaneFusionType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class ArcaneFusionEntityRenderer extends ArcaneEntityRendererBase<ArcaneFusionEntity> {
    private final ArcaneFusionModel model;
    public ArcaneFusionEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new ArcaneFusionModel());
        this.model = (ArcaneFusionModel) this.getGeoModel();
    }

    @Override
    public void render(ArcaneFusionEntity entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight) {
        ArcaneFusionType type = entity.getFusionType();
        model.setType(type);

        matrixStack.pushPose();
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
        matrixStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(ArcaneFusionEntity entity) {
        return model.getTextureResource(entity);
    }

}
