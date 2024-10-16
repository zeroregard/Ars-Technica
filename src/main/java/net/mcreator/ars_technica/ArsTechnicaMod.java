package net.mcreator.ars_technica;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.BasicSpellTurret;
import com.simibubi.create.content.decoration.encasing.EncasableBlock;
import com.simibubi.create.content.decoration.encasing.EncasingRegistry;
import com.simibubi.create.content.kinetics.BlockStressDefaults;
import com.simibubi.create.content.kinetics.BlockStressValues;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.RegisteredObjects;
import net.mcreator.ars_technica.client.AllPartialModels;
import net.mcreator.ars_technica.common.items.equipment.SpyMonocleCurioRenderer;
import net.mcreator.ars_technica.common.kinetics.CustomStressValueProvider;
import net.mcreator.ars_technica.mixin.BasicSpellTurretMixin;
import net.mcreator.ars_technica.recipe.ConfigRecipeCondition;
import net.mcreator.ars_technica.setup.*;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.DistExecutor;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.common.MinecraftForge;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.FriendlyByteBuf;

import net.mcreator.ars_technica.init.ArsTechnicaModSounds;
import net.mcreator.ars_technica.client.events.ClientHandler;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

import java.util.function.Supplier;
import java.util.function.Function;
import java.util.function.BiConsumer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.AbstractMap;

import static net.mcreator.ars_technica.setup.BlockRegistry.ANDESITE_ENCASED_TURRET_BLOCK;

@Mod("ars_technica")
public class ArsTechnicaMod {
	public static final Logger LOGGER = LogManager.getLogger(ArsTechnicaMod.class);
	public static final String MODID = "ars_technica";

	public ArsTechnicaMod() {
		MinecraftForge.EVENT_BUS.register(this);
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
		ArsTechnicaModSounds.REGISTRY.register(bus);
		ModSetup.registers(bus);
		ArsNouveauRegistry.init();
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigHandler.COMMON_SPEC);
		bus.addListener(this::setup);

		DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> {
			bus.addListener(this::doClientStuff);
			return new Object();
		});
	}

	@OnlyIn(Dist.CLIENT)
	private void doClientStuff(final FMLClientSetupEvent event) {
		CuriosRendererRegistry.register(ItemsRegistry.SPY_MONOCLE.get(), () -> new SpyMonocleCurioRenderer(Minecraft.getInstance().getEntityModels().bakeLayer(SpyMonocleCurioRenderer.SPY_MONOCLE_LAYER)));
		AllPartialModels.init();
	}

	public void setup(final FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			CraftingHelper.register(ConfigRecipeCondition.Serializer.INSTANCE);
			ArsNouveauRegistry.postInit();
			NetworkHandler.registerMessages();
		});
		registerStressValues();
		registerEncasing();
	}

	private static void registerStressValues() {
		var sourceEngineId = BlockRegistry.SOURCE_ENGINE.getId();
		BlockStressDefaults.setDefaultCapacity(sourceEngineId, 256.0);
		BlockStressDefaults.setGeneratorSpeed(sourceEngineId, () -> Couple.create(0, 256));
	}

	private static void registerEncasing() {
		EncasingRegistry.addVariant((Block & EncasableBlock) com.hollingsworth.arsnouveau.setup.registry.BlockRegistry.BASIC_SPELL_TURRET.get(), ANDESITE_ENCASED_TURRET_BLOCK.get());
	}

	public void clientSetup(final FMLClientSetupEvent event) {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientHandler::init);
	}

	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel PACKET_HANDLER = NetworkRegistry.newSimpleChannel(new ResourceLocation(MODID, MODID), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
	private static int messageID = 0;

	public static <T> void addNetworkMessage(Class<T> messageType, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, Supplier<NetworkEvent.Context>> messageConsumer) {
		PACKET_HANDLER.registerMessage(messageID, messageType, encoder, decoder, messageConsumer);
		messageID++;
	}

	private static final Collection<AbstractMap.SimpleEntry<Runnable, Integer>> workQueue = new ConcurrentLinkedQueue<>();

	public static void queueServerWork(int tick, Runnable action) {
		if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER)
			workQueue.add(new AbstractMap.SimpleEntry<>(action, tick));
	}

	@SubscribeEvent
	public void tick(TickEvent.ServerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			List<AbstractMap.SimpleEntry<Runnable, Integer>> actions = new ArrayList<>();
			workQueue.forEach(work -> {
				work.setValue(work.getValue() - 1);
				if (work.getValue() == 0)
					actions.add(work);
			});
			actions.forEach(e -> e.getKey().run());
			workQueue.removeAll(actions);
		}
	}
}
