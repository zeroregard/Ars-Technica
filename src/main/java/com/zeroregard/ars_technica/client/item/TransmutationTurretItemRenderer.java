package com.zeroregard.ars_technica.client.item;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemBlockRenderer;
import com.hollingsworth.arsnouveau.common.items.AnimBlockItem;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

import java.util.function.Supplier;

public class TransmutationTurretItemRenderer extends GenericItemBlockRenderer {
    
    private static final TransmutationTurretItemModel MODEL = new TransmutationTurretItemModel();
    
    public TransmutationTurretItemRenderer() {
        super(MODEL);
    }
    
    public static Supplier<BlockEntityWithoutLevelRenderer> getISTER() {
        return () -> new TransmutationTurretItemRenderer();
    }
    
    public static class TransmutationTurretItemModel extends GeoModel<AnimBlockItem> {
        
        @Override
        public ResourceLocation getModelResource(AnimBlockItem animatable) {
            return ResourceLocation.fromNamespaceAndPath(ArsNouveau.MODID, "geo/basic_spell_turret.geo.json");
        }
        
        @Override
        public ResourceLocation getTextureResource(AnimBlockItem animatable) {
            return ResourceLocation.fromNamespaceAndPath(ArsNouveau.MODID, "textures/block/transmutation_turret.png");
        }
        
        @Override
        public ResourceLocation getAnimationResource(AnimBlockItem animatable) {
            return ResourceLocation.fromNamespaceAndPath(ArsNouveau.MODID, "animations/basic_spell_turret_animations.json");
        }
    }
}

