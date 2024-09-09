package net.mcreator.ars_technica.client.events;

import com.hollingsworth.arsnouveau.api.perk.ArmorPerkHolder;
import com.hollingsworth.arsnouveau.api.perk.IPerkHolder;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;

import net.mcreator.ars_technica.ArsTechnicaMod;
import net.mcreator.ars_technica.setup.EntityRegistry;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.mcreator.ars_technica.setup.ItemsRegistry;
import net.mcreator.ars_technica.client.renderer.entity.WhirlEntityRenderer;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ArsTechnicaMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
@OnlyIn(Dist.CLIENT)
public class ClientHandler {

  @SubscribeEvent
  public static void initItemColors(final RegisterColorHandlersEvent.Item event) {

    event.register((stack, color) -> color > 0 ? -1 : colorFromArmor(stack),
        ItemsRegistry.TECHNOMANCER_BOOTS.get());

    event.register((stack, color) -> color > 0 ? -1 : colorFromArmor(stack),
        ItemsRegistry.TECHNOMANCER_CHESTPLATE.get());

    event.register((stack, color) -> color > 0 ? -1 : colorFromArmor(stack),
        ItemsRegistry.TECHNOMANCER_HELMET.get());

    event.register((stack, color) -> color > 0 ? -1 : colorFromArmor(stack),
        ItemsRegistry.TECHNOMANCER_LEGGINGS.get());
  }

  @SubscribeEvent
  public static void init(final FMLClientSetupEvent evt) {
  }

  public static int colorFromArmor(ItemStack stack) {
    IPerkHolder<ItemStack> holder = PerkUtil.getPerkHolder(stack);
    if (!(holder instanceof ArmorPerkHolder armorPerkHolder))
      return DyeColor.BROWN.getTextColor();
    return DyeColor.byName(armorPerkHolder.getColor(), DyeColor.BROWN).getTextColor();
  }

  @SubscribeEvent
  public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
    event.registerEntityRenderer(EntityRegistry.WHIRL_ENTITY.get(), WhirlEntityRenderer::new);
  }
}