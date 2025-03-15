package com.zeroregard.ars_technica.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zeroregard.ars_technica.entity.ArcaneHammerEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.joml.Quaternionf;

public class ArcaneHammerEntityRenderer extends ArcaneEntityRendererBase<ArcaneHammerEntity> {

    public ArcaneHammerEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new ArcaneHammerModel());
    }

    @Override
    public void render(ArcaneHammerEntity entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight) {
        matrixStack.pushPose();
        var yaw = entity.getYaw();
        var size = entity.getSize();
        matrixStack.rotateAround(new Quaternionf().rotateLocalY(yaw), 0, 0, 0);
        matrixStack.scale(size, size, size);
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
        matrixStack.popPose();
    }

}
