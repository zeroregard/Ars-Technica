package com.zeroregard.ars_technica.block;

import com.hollingsworth.arsnouveau.client.renderer.item.GenericItemBlockRenderer;
import com.hollingsworth.arsnouveau.client.renderer.tile.GenericModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

import java.util.function.Supplier;

public class PreciseRelayRenderer extends GeoBlockRenderer<PreciseRelayTile> {

    public PreciseRelayRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
        super(new GenericModel<>("source_relay"));
    }

    public static Supplier<BlockEntityWithoutLevelRenderer> getISTER(String loc) {
        return () -> new GenericItemBlockRenderer(new GenericModel<>(loc));
    }
}