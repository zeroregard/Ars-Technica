package net.mcreator.ars_technica.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import net.mcreator.ars_technica.common.entity.WhirlEntity;

import net.mcreator.ars_technica.common.glyphs.EffectWhirl;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class WhirlEntityRenderer extends GenericRenderer<WhirlEntity> {
    private final WhirlModel model;
    public WhirlEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new WhirlModel());
        this.model = (WhirlModel) this.getGeoModel();
    }

    @Override
    public void render(WhirlEntity entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight) {
        FanProcessingType type = entity.getProcessor();
        model.setType(type);
        float scale = getScaleFromRadius(entity);
        matrixStack.pushPose();
        matrixStack.scale(scale, scale, scale);
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);

        matrixStack.popPose();
    }

    private float getScaleFromRadius(WhirlEntity entity) {
        return entity.getRadius() / EffectWhirl.INSTANCE.DEFAULT_RADIUS;
    }

    @Override
    public ResourceLocation getTextureLocation(WhirlEntity entity) {
        return model.getTextureResource(entity);
    }

}
