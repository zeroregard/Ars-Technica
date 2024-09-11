package net.mcreator.ars_technica.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import net.mcreator.ars_technica.ArsTechnicaMod;
import net.mcreator.ars_technica.common.entity.WhirlEntity;

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
        ArsTechnicaMod.LOGGER.info(type);
        model.setType(type);

        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(WhirlEntity entity) {
        return model.getTextureResource(entity);
    }

}
