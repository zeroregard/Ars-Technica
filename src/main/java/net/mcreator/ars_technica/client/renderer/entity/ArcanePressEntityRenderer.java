package net.mcreator.ars_technica.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.mcreator.ars_technica.common.entity.ArcanePressEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.joml.Quaternionf;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class ArcanePressEntityRenderer extends ArcaneEntityRendererBase<ArcanePressEntity> {
    private static float accumulatedTime = 0.0f;

    public ArcanePressEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new ArcanePressModel());
    }

    @Override
    public void render(ArcanePressEntity entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight) {
        accumulatedTime += Minecraft.getInstance().getDeltaFrameTime();
        float elapsedTime = (accumulatedTime / 20.0f);
        float angle = elapsedTime * (2 * (float)Math.PI / 10);
        float radius = 0.15f;

        float x = radius * (float)Math.cos(angle);
        float z = radius * (float)Math.sin(angle);

        matrixStack.pushPose();
        matrixStack.translate(x, 0, z);
        matrixStack.rotateAround(new Quaternionf().rotateLocalY(angle), 0, 0, 0);
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);

        matrixStack.popPose();
    }

}
