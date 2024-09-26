package net.mcreator.ars_technica.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.mcreator.ars_technica.common.entity.ArcanePolishEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.joml.Quaternionf;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class ArcanePolishEntityRenderer extends GenericRenderer<ArcanePolishEntity> {
    private final ArcanePolishModel model;
    public ArcanePolishEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new ArcanePolishModel());
        this.model = (ArcanePolishModel) this.getGeoModel();
        addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    @Override
    public void render(ArcanePolishEntity entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight) {
        matrixStack.pushPose();
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
        matrixStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(ArcanePolishEntity entity) {
        return model.getTextureResource(entity);
    }

    @Override
    public RenderType getRenderType(ArcanePolishEntity animatable, ResourceLocation texture, @org.jetbrains.annotations.Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucentCull(texture);
    }


}
