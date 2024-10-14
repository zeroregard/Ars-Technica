package net.mcreator.ars_technica.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.mcreator.ars_technica.ArsTechnicaMod;
import net.mcreator.ars_technica.common.entity.ArcaneHammerEntity;
import net.mcreator.ars_technica.common.entity.ArcanePressEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.joml.Quaternionf;
import software.bernie.geckolib.core.object.Color;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

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
