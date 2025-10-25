package com.zeroregard.ars_technica.client.block;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.renderer.tile.ArsGeoBlockRenderer;
import com.hollingsworth.arsnouveau.common.block.BasicSpellTurret;
import com.mojang.blaze3d.vertex.PoseStack;
import com.zeroregard.ars_technica.block.TransmutationTurretTile;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class TransmutationTurretRenderer extends ArsGeoBlockRenderer<TransmutationTurretTile> {

    public static GeoModel<TransmutationTurretTile> modelTransmutation = new TurretModel();

    public TransmutationTurretRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
        super(rendererDispatcherIn, modelTransmutation);
    }

    @Override
    protected void rotateBlock(Direction facing, PoseStack poseStack) {
        // no idea why we really need this but it works for now
        super.rotateBlock(facing, poseStack);
        if (facing == Direction.UP) {
            poseStack.translate(0, -0.5, -0.5);
        } else if (facing == Direction.DOWN) {
            poseStack.translate(0, -0.5, 0.5);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(TransmutationTurretTile instance) {
        return ResourceLocation.fromNamespaceAndPath(ArsNouveau.MODID, "textures/block/transmutation_turret.png");
    }

    public static class TurretModel extends GeoModel<TransmutationTurretTile> {

        @Override
        public ResourceLocation getModelResource(TransmutationTurretTile t) {
            return ResourceLocation.fromNamespaceAndPath(ArsNouveau.MODID, "geo/basic_spell_turret.geo.json");
        }

        @Override
        public ResourceLocation getTextureResource(TransmutationTurretTile t) {
            return ResourceLocation.fromNamespaceAndPath(ArsNouveau.MODID, "textures/block/transmutation_turret.png");
        }

        @Override
        public ResourceLocation getAnimationResource(TransmutationTurretTile t) {
            return ResourceLocation.fromNamespaceAndPath(ArsNouveau.MODID, "animations/basic_spell_turret_animations.json");
        }
    }
}

