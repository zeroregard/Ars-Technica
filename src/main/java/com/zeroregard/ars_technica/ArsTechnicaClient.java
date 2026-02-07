package com.zeroregard.ars_technica;

import com.simibubi.create.AllBlockEntityTypes;
import com.zeroregard.ars_technica.client.block.AllPartialModels;
import com.zeroregard.ars_technica.client.block.ArcaneSchematiccannonRenderer;
import com.zeroregard.ars_technica.client.block.TransmutationTurretRenderer;
import com.zeroregard.ars_technica.client.item.SpyMonocleCurioRenderer;
import com.zeroregard.ars_technica.ponder.ATPonderPlugin;
import com.zeroregard.ars_technica.registry.EntityRegistry;
import com.zeroregard.ars_technica.registry.ItemRegistry;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(value = ArsTechnica.MODID, dist = Dist.CLIENT)
public class ArsTechnicaClient {

    public ArsTechnicaClient(net.neoforged.bus.api.IEventBus modEventBus) {
        onCtorClient(modEventBus);
    }


    public static void onCtorClient(net.neoforged.bus.api.IEventBus modEventBus) {
        modEventBus.addListener(ArsTechnicaClient::clientInit);
        modEventBus.addListener(ArsTechnicaClient::registerAdditionalModels);
    }

    /** Register Arcane Wrench item partial models so they are loaded and PartialModel.get() works. Side-loaded models must use the 'standalone' variant. */
    public static void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
        event.register(new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(ArsTechnica.MODID, "item/runic_spanner"), "standalone"));
        event.register(new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(ArsTechnica.MODID, "item/arcane_gear"), "standalone"));
    }

    public static void clientInit(final FMLClientSetupEvent event) {
        AllPartialModels.init();
        PonderIndex.addPlugin(new ATPonderPlugin());
        CuriosRendererRegistry.register(ItemRegistry.SPY_MONOCLE.get(), () -> new SpyMonocleCurioRenderer(Minecraft.getInstance().getEntityModels().bakeLayer(SpyMonocleCurioRenderer.SPY_MONOCLE_LAYER)));
        event.enqueueWork(() -> {
            EntityRenderersEvent.RegisterRenderers renderRegisterEvent = new EntityRenderersEvent.RegisterRenderers();
            renderRegisterEvent.registerBlockEntityRenderer(AllBlockEntityTypes.SCHEMATICANNON.get(), ArcaneSchematiccannonRenderer::new);
            renderRegisterEvent.registerBlockEntityRenderer(EntityRegistry.TRANSMUTATION_TURRET_BLOCK_ENTITY.get(), TransmutationTurretRenderer::new);
        });
    }

}
