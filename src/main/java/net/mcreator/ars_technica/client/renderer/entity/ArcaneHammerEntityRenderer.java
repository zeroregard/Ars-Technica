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
    public ArcaneHammerEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new ArcaneHammerModel());
        this.model = (ArcaneHammerModel) this.getGeoModel();

        // TODO: make this render fully bright in darkness
    }

    @Override
    public void render(ArcaneHammerEntity entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(ArcaneHammerEntity entity) {
        return model.getTextureResource(entity);
    }

    @Override
    public RenderType getRenderType(ArcaneHammerEntity animatable, ResourceLocation texture, @org.jetbrains.annotations.Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucentCull(texture);
    }

    @Override
    public Color getRenderColor(ArcaneHammerEntity animatable, float partialTick, int packedLight) {
        float alpha = (float)animatable.getAlpha();
        var color = animatable.getColor();
        var finalColor = Color.ofRGBA(color.getRedFloat(), color.getGreenFloat(), color.getBlueFloat(), alpha);
        return finalColor;
    }
}
