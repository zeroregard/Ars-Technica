package com.zeroregard.ars_technica.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import com.zeroregard.ars_technica.entity.ArcaneWhirlEntity;
import com.zeroregard.ars_technica.glyphs.EffectWhirl;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class ArcaneWhirlEntityRenderer extends GenericRenderer<ArcaneWhirlEntity> {
    private final ArcaneWhirlModel model;
    public ArcaneWhirlEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new ArcaneWhirlModel());
        this.model = (ArcaneWhirlModel) this.getGeoModel();
    }

    @Override
    public void render(ArcaneWhirlEntity entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight) {
        FanProcessingType type = entity.getProcessor();
        model.setType(type);
        float scale = getScaleFromRadius(entity);
        matrixStack.pushPose();
        matrixStack.scale(scale, scale, scale);
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);

        matrixStack.popPose();
    }

    private float getScaleFromRadius(ArcaneWhirlEntity entity) {
        return entity.getRadius() / EffectWhirl.INSTANCE.DEFAULT_RADIUS;
    }

    @Override
    public ResourceLocation getTextureLocation(ArcaneWhirlEntity entity) {
        return model.getTextureResource(entity);
    }

}
