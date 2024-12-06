package net.mcreator.ars_technica.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.mcreator.ars_technica.common.entity.fusion.ArcaneFusionEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class ArcaneFusionEntityRenderer extends ArcaneEntityRendererBase<ArcaneFusionEntity> {

    public ArcaneFusionEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new ArcaneFusionModel());
    }

    @Override
    public void render(ArcaneFusionEntity entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight) {
        matrixStack.pushPose();
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
        matrixStack.popPose();
    }

}
