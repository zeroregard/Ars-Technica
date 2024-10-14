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

public class ArcaneHammerEntityRenderer extends GenericRenderer<ArcaneHammerEntity> {
    private final ArcaneHammerModel model;
    //private final AutoGlowingGeoLayer glowLayer;

    public ArcaneHammerEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new ArcaneHammerModel());
        this.model = (ArcaneHammerModel) this.getGeoModel();
        //glowLayer = new AutoGlowingGeoLayer<>(this);
        //addRenderLayer(glowLayer);
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

    @Override
    public ResourceLocation getTextureLocation(ArcaneHammerEntity entity) {
        return model.getTextureResource(entity);
    }

    @Override
    public RenderType getRenderType(ArcaneHammerEntity animatable, ResourceLocation texture, @org.jetbrains.annotations.Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucentEmissive(texture);
    }

    @Override
    public Color getRenderColor(ArcaneHammerEntity animatable, float partialTick, int packedLight) {
        float alpha = (float)animatable.getAlpha();
        var color = animatable.getColor();
        if (color == null) {
            return Color.ofRGBA(1, 1, 1, alpha);
        }
        var finalColor = Color.ofRGBA(color.getRedFloat(), color.getGreenFloat(), color.getBlueFloat(), alpha);
        return finalColor;
    }
}
