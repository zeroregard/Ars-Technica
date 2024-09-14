package net.mcreator.ars_technica.armor;

import com.hollingsworth.arsnouveau.api.item.ISpellModifierItem;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellSchools;
import net.minecraft.Util;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.perk.ArmorPerkHolder;
import com.hollingsworth.arsnouveau.api.perk.IPerkHolder;
import com.hollingsworth.arsnouveau.api.perk.IPerkProvider;
import com.hollingsworth.arsnouveau.api.registry.PerkRegistry;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import com.hollingsworth.arsnouveau.common.armor.AnimatedMagicArmor;

import net.mcreator.ars_technica.ArsTechnicaMod;
import net.mcreator.ars_technica.client.TooltipUtils;
import net.mcreator.ars_technica.client.renderer.item.TechnomancerArmorRenderer;
import net.mcreator.ars_technica.setup.ItemsRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;
import net.minecraft.world.level.Level;

import java.util.EnumMap;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;


import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.hollingsworth.arsnouveau.api.perk.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import static alexthw.ars_elemental.ConfigHandler.Common.ARMOR_MANA_REGEN;
import static alexthw.ars_elemental.ConfigHandler.Common.ARMOR_MAX_MANA;

public class TechnomancerArmor extends AnimatedMagicArmor implements ISpellModifierItem {

  private final String specialInformation;

  private static final EnumMap<ArmorItem.Type, UUID> ARMOR_MODIFIER_UUID_PER_TYPE = Util.make(new EnumMap<>(ArmorItem.Type.class), (p_266744_) -> {
    p_266744_.put(ArmorItem.Type.BOOTS, UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"));
    p_266744_.put(ArmorItem.Type.LEGGINGS, UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"));
    p_266744_.put(ArmorItem.Type.CHESTPLATE, UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"));
    p_266744_.put(ArmorItem.Type.HELMET, UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150"));
  });

  public TechnomancerArmor(ArmorItem.Type slot, @Nullable String tooltipSpecialInformation) {
    super(TechnomancerMaterial.INSTANCE, slot, new TechnomancerArmorModel("technomancer_medium_armor").withEmptyAnim());
    specialInformation = tooltipSpecialInformation;
  }

  @Override
  public int getMinTier() {
    return 2;
  }

  @Override
  public @Nullable String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
    String color = getColor(stack);
    String location = "textures/armor/technomancer_medium_armor_" + color + ".png";
    return new ResourceLocation(ArsTechnicaMod.MODID, location).toString();
  }

  @Override
  public int getManaDiscount(ItemStack i, Spell spell) {
    double sum = 0;
    for (AbstractSpellPart part : spell.recipe) {
      if (SpellSchools.MANIPULATION.isPartOfSchool(part))
        sum += 0.2 * part.getCastingCost();
    }
    return Mth.ceil(sum);
  }

  @Override
  public String getColor(ItemStack object) {
    IPerkHolder<ItemStack> perkHolder = PerkUtil.getPerkHolder(object);
    if (!(perkHolder instanceof ArmorPerkHolder data)) {
      return "brown";
    }
    return data.getColor() == null || data.getColor().isEmpty() ? "brown" : data.getColor();
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flags) {
    IPerkProvider<ItemStack> perkProvider = PerkRegistry.getPerkProvider(stack.getItem());
    if (perkProvider != null) {
      tooltip.add(Component.translatable("ars_nouveau.tier", 4).withStyle(ChatFormatting.GOLD));
      perkProvider.getPerkHolder(stack).appendPerkTooltip(tooltip, stack);
    }
    TooltipUtils.addOnShift(tooltip, () -> addInformationAfterShift(stack, world, tooltip, flags), "armor_set");
  }

  EquipmentSlot[] OrderedSlots = { EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET };
  private final Supplier<Item[]> orderedItemsSupplier = () -> new Item[] {
      ItemsRegistry.TECHNOMANCER_HELMET.get(),
      ItemsRegistry.TECHNOMANCER_CHESTPLATE.get(),
      ItemsRegistry.TECHNOMANCER_LEGGINGS.get(),
      ItemsRegistry.TECHNOMANCER_BOOTS.get()
  };

  @OnlyIn(Dist.CLIENT)
  public void addInformationAfterShift(ItemStack stack, Level world, List<Component> list, TooltipFlag flags) {
    Player player = ArsNouveau.proxy.getPlayer();
    if (player != null) {
      List<Component> equippedList = new ArrayList<>();
      Item[] orderedItems = orderedItemsSupplier.get();
      int equippedCounter = 0;
      for (int i = 0; i < OrderedSlots.length; i++) {
        EquipmentSlot slot = OrderedSlots[i];
        Item armor = orderedItems[i];
        MutableComponent cmp = Component.literal(" - ").append(armor.getDefaultInstance().getHoverName());
        if (player.getItemBySlot(slot).getItem() == armor) {
          cmp.withStyle(ChatFormatting.GREEN);
          equippedCounter++;
        } else
          cmp.withStyle(ChatFormatting.GRAY);
        equippedList.add(cmp);
      }
      list.add(getArmorSetTitle(equippedCounter)); // "X out of 4 equipped"
      list.addAll(equippedList); // list of equipped armor pieces
      Component specialInfo = getArmorPieceSpecialInformation();
      if ( specialInfo != null ) {
        list.add(specialInfo);
      }
      list.add(
          Component.translatable(ArsTechnicaMod.MODID + ".armor_set.technomancer.desc").withStyle(ChatFormatting.GRAY));
    }

  }

  @Override
  public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot pEquipmentSlot, ItemStack stack) {
    ImmutableMultimap.Builder<Attribute, AttributeModifier> attributes = new ImmutableMultimap.Builder<>();
    attributes.putAll(super.getDefaultAttributeModifiers(pEquipmentSlot));
    if (this.getType().getSlot() == pEquipmentSlot) {
      UUID uuid = ARMOR_MODIFIER_UUID_PER_TYPE.get(this.getType());
      IPerkHolder<ItemStack> perkHolder = PerkUtil.getPerkHolder(stack);
      if (perkHolder != null) {
        attributes.put(PerkAttributes.MAX_MANA.get(), new AttributeModifier(uuid, "max_mana_armor", ARMOR_MAX_MANA.get(), AttributeModifier.Operation.ADDITION));
        attributes.put(PerkAttributes.MANA_REGEN_BONUS.get(), new AttributeModifier(uuid, "mana_regen_armor", ARMOR_MANA_REGEN.get(), AttributeModifier.Operation.ADDITION));
        for (PerkInstance perkInstance : perkHolder.getPerkInstances()) {
          IPerk perk = perkInstance.getPerk();
          attributes.putAll(perk.getModifiers(this.getType().getSlot(), stack, perkInstance.getSlot().value));
        }

      }
    }
    return attributes.build();
  }

  private Component getArmorSetTitle(int equipped) {
    return Component.translatable(ArsTechnicaMod.MODID + ".armor_set.technomancer")
        .append(" (" + equipped + " / 4)")
        .withStyle(ChatFormatting.DARK_AQUA);
  }

  @Nullable
  private Component getArmorPieceSpecialInformation() {
    if (specialInformation == null) {
      return null;
    }
    return Component.translatable(ArsTechnicaMod.MODID + specialInformation)
            .withStyle(ChatFormatting.GOLD);
  }

  @Override
  public void initializeClient(Consumer<IClientItemExtensions> consumer) {
    super.initializeClient(consumer);
    consumer.accept(new IClientItemExtensions() {
      private GeoArmorRenderer<?> renderer;

      @Override
      public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack,
          EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
        if (renderer == null) {
          renderer = new TechnomancerArmorRenderer(getArmorModel());
        }
        renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);
        return this.renderer;
      }
    });
  }
}