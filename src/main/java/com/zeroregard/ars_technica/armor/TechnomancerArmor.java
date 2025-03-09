package com.zeroregard.ars_technica.armor;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.item.ISpellModifierItem;
import com.hollingsworth.arsnouveau.api.mana.IManaDiscountEquipment;
import com.hollingsworth.arsnouveau.api.perk.*;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellSchools;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import com.hollingsworth.arsnouveau.common.armor.AnimatedMagicArmor;
import com.hollingsworth.arsnouveau.common.items.data.ArmorPerkHolder;
import com.zeroregard.ars_technica.ArsTechnica;
import com.zeroregard.ars_technica.client.armor.TechnomancerArmorRenderer;
import com.zeroregard.ars_technica.client.utils.TooltipUtils;
import com.zeroregard.ars_technica.registry.ItemRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.zeroregard.ars_technica.Config.Common.ARMOR_MANA_REGEN;
import static com.zeroregard.ars_technica.Config.Common.ARMOR_MAX_MANA;

public class TechnomancerArmor extends AnimatedMagicArmor implements ISpellModifierItem, IManaDiscountEquipment {

  private final String specialInformation;

  private static final EnumMap<Type, UUID> ARMOR_MODIFIER_UUID_PER_TYPE = Util.make(new EnumMap<>(Type.class), (p_266744_) -> {
    p_266744_.put(Type.BOOTS, UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"));
    p_266744_.put(Type.LEGGINGS, UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"));
    p_266744_.put(Type.CHESTPLATE, UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"));
    p_266744_.put(Type.HELMET, UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150"));
  });

  public TechnomancerArmor(Type slot, @Nullable String tooltipSpecialInformation) {
    super(ATMaterials.techno, slot, new TechnomancerArmorModel("technomancer_medium_armor").withEmptyAnim());
    specialInformation = tooltipSpecialInformation;
  }

  @Override
  public int getMinTier() {
    return 2;
  }

  @Override
  public @Nullable ResourceLocation getArmorTexture(@NotNull ItemStack stack, @NotNull Entity entity, @NotNull EquipmentSlot slot, ArmorMaterial.Layer layer, boolean innerModel) {
    return ResourceLocation.fromNamespaceAndPath(ArsTechnica.MODID, "textures/armor/technomancer.png");
  }

  @Override
  public int getManaDiscount(ItemStack i, Spell spell) {
    return Mth.ceil(getDiscount(spell.unsafeList()));
  }

  double getDiscount(List<AbstractSpellPart> recipe) {
    double sum = 0;
    for (AbstractSpellPart part : recipe) {
      if (SpellSchools.MANIPULATION.isPartOfSchool(part))
        sum += 0.2 * part.getCastingCost();
    }
    return Mth.ceil(sum);
  }

  @Override
  public String getColor(ItemStack object) {
    var perkHolder = PerkUtil.getPerkHolder(object);
    if (!(perkHolder instanceof ArmorPerkHolder data)) {
      return "purple";
    }
    return data.getColor() == null || data.getColor().isEmpty() ? "purple" : data.getColor();
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flags) {
    var perkProvider = PerkUtil.getPerkHolder(stack);
    if (perkProvider != null) {
      tooltip.add(Component.translatable("ars_nouveau.tier", 4).withStyle(ChatFormatting.GOLD));
      perkProvider.appendPerkTooltip(tooltip, stack);
    }
    TooltipUtils.addOnShift(tooltip, () -> addInformationAfterShift(stack, context, tooltip, flags), "armor_set");
  }

  EquipmentSlot[] OrderedSlots = { EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET };
  private final Supplier<Item[]> orderedItemsSupplier = () -> new Item[] {
      ItemRegistry.TECHNOMANCER_HELMET.get(),
      ItemRegistry.TECHNOMANCER_CHESTPLATE.get(),
      ItemRegistry.TECHNOMANCER_LEGGINGS.get(),
      ItemRegistry.TECHNOMANCER_BOOTS.get()
  };

  @OnlyIn(Dist.CLIENT)
  public void addInformationAfterShift(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flags) {
    Player player = ArsNouveau.proxy.getPlayer();
    if (player != null) {
      List<Component> equippedList = new ArrayList<>();
      Item[] orderedItems = orderedItemsSupplier.get();
      //check if the player have all the armor pieces of the set. Color the text green if they do, gray if they don't
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
      //add the tooltip for the armor set and the list of equipped armor pieces, then add the description
      list.add(getArmorSetTitle(equippedCounter));
      list.addAll(equippedList);
      Component specialInfo = getArmorPieceSpecialInformation();
      if ( specialInfo != null ) {
        list.add(specialInfo);
      }
      list.add(Component.translatable(ArsTechnica.MODID + ".armor_set.technomancer.desc").withStyle(ChatFormatting.GRAY));
    }
  }

  @Override
  public @NotNull ItemAttributeModifiers getDefaultAttributeModifiers(@NotNull ItemStack stack) {
    var modifiers = super.getDefaultAttributeModifiers()
            .withModifierAdded(PerkAttributes.MAX_MANA, new AttributeModifier(ArsNouveau.prefix("max_mana_armor_" + this.type.getName()), ARMOR_MAX_MANA.get(), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.bySlot(this.type.getSlot()))
            .withModifierAdded(PerkAttributes.MANA_REGEN_BONUS, new AttributeModifier(ArsNouveau.prefix("mana_regen_armor_" + this.type.getName()), ARMOR_MANA_REGEN.get(), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.bySlot(this.type.getSlot()));

    for (PerkInstance perkInstance : PerkUtil.getPerksFromItem(stack)) {
      IPerk perk = perkInstance.getPerk();
      modifiers = perk.applyAttributeModifiers(modifiers, stack, perkInstance.getSlot().value(), EquipmentSlotGroup.bySlot(this.type.getSlot()));
    }

    return modifiers;
  }

  private Component getArmorSetTitle(int equipped) {
    return Component.translatable(ArsTechnica.MODID + ".armor_set.technomancer")
        .append(" (" + equipped + " / 4)")
        .withStyle(ChatFormatting.DARK_AQUA);
  }

  @Nullable
  private Component getArmorPieceSpecialInformation() {
    if (specialInformation == null) {
      return null;
    }
    return Component.translatable(ArsTechnica.MODID + specialInformation)
            .withStyle(ChatFormatting.GOLD);
  }

  @Override
  public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
    consumer.accept(new GeoRenderProvider() {
      private GeoArmorRenderer<?> renderer;

      public <T extends LivingEntity> @NotNull HumanoidModel<?> getGeoArmorRenderer(@Nullable T livingEntity, ItemStack itemStack, @Nullable EquipmentSlot equipmentSlot, @Nullable HumanoidModel<T> original) {
        if (this.renderer == null) this.renderer = new TechnomancerArmorRenderer(getArmorModel());
        return this.renderer;
      }
    });
  }

  public static boolean isWearingFullSet(LivingEntity entity) {
    ItemStack head = entity.getItemBySlot(EquipmentSlot.HEAD);
    ItemStack chest = entity.getItemBySlot(EquipmentSlot.CHEST);
    ItemStack legs = entity.getItemBySlot(EquipmentSlot.LEGS);
    ItemStack boots = entity.getItemBySlot(EquipmentSlot.FEET);

    return head.getItem() instanceof TechnomancerArmor &&
            chest.getItem() instanceof TechnomancerArmor &&
            legs.getItem() instanceof TechnomancerArmor &&
            boots.getItem() instanceof TechnomancerArmor;
  }
}