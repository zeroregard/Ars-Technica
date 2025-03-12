package com.zeroregard.ars_technica.client.entity;

import com.zeroregard.ars_technica.entity.Colorable;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.util.Color;

public class ArcaneEntityRendererBase<T extends Entity & GeoEntity & Colorable> extends GenericRenderer<T> {
    protected final GeoModel<T> model;

    public ArcaneEntityRendererBase(EntityRendererProvider.Context context, GeoModel<T> model) {
        super(context, model);
        this.model = this.getGeoModel();
    }

    @Override
    public RenderType getRenderType(T animatable, ResourceLocation texture, @org.jetbrains.annotations.Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucentEmissive(texture, false);
    }

    @Override
    public Color getRenderColor(T animatable, float partialTick, int packedLight) {
        float alpha = (float)animatable.getAlpha();
        var color = animatable.getColor();
        if (color == null) {
            return Color.ofRGBA(1, 1, 1, alpha);
        }
        var finalColor = Color.ofRGBA(color.getRedFloat(), color.getGreenFloat(), color.getBlueFloat(), alpha);
        return finalColor;
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return model.getTextureResource(entity);
    }
}
