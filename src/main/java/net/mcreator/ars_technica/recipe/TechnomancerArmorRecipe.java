package net.mcreator.ars_technica.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.EnchantingApparatusRecipe;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.ITextOutput;
import com.hollingsworth.arsnouveau.api.perk.ArmorPerkHolder;
import com.hollingsworth.arsnouveau.api.perk.IPerkHolder;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import com.hollingsworth.arsnouveau.common.block.tile.EnchantingApparatusTile;

import net.mcreator.ars_technica.ArsTechnicaMod;
import net.mcreator.ars_technica.setup.RecipeRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static com.hollingsworth.arsnouveau.setup.registry.RegistryHelper.getRegistryName;

public class TechnomancerArmorRecipe extends EnchantingApparatusRecipe implements ITextOutput {

  public int tier; // index starts at 0 (0 == 'Tier 1')

  public TechnomancerArmorRecipe(ResourceLocation id, List<Ingredient> pedestalItems, Ingredient reagent,
      ItemStack result, int cost, int tier) {
    super(id, pedestalItems, reagent, result, cost, true);
    this.tier = tier;
  }

  public TechnomancerArmorRecipe(EnchantingApparatusRecipe recipe) {
    this(recipe.id, recipe.pedestalItems, recipe.reagent, recipe.result, recipe.sourceCost, 3);
  }

  public JsonElement asRecipe() {
    JsonObject jsonobject = new JsonObject();
    jsonobject.addProperty("type", ArsTechnicaMod.MODID + ":armor_upgrade");

    JsonArray pedestalArr = new JsonArray();
    for (Ingredient i : this.pedestalItems) {
      JsonObject object = new JsonObject();
      object.add("item", i.toJson());
      pedestalArr.add(object);
    }
    JsonArray reagent = new JsonArray();
    reagent.add(this.reagent.toJson());
    jsonobject.add("reagent", reagent);

    JsonObject resultObj = new JsonObject();
    resultObj.addProperty("item", getRegistryName(result.getItem()).toString());
    jsonobject.add("pedestalItems", pedestalArr);
    jsonobject.add("output", resultObj);
    jsonobject.addProperty("sourceCost", sourceCost);
    jsonobject.addProperty("tier", tier);
    return jsonobject;
  }

  @Override
  public boolean isMatch(List<ItemStack> pedestalItems, ItemStack reagent,
      EnchantingApparatusTile enchantingApparatusTile, @Nullable Player player) {
    IPerkHolder<ItemStack> perkHolder = PerkUtil.getPerkHolder(reagent);
    if (!(perkHolder instanceof ArmorPerkHolder armorPerkHolder)) {
      return false;
    }
    return armorPerkHolder.getTier() == (tier - 1)
        && super.isMatch(pedestalItems, reagent, enchantingApparatusTile, player);
  }

  @Override
  public ItemStack getResult(List<ItemStack> pedestalItems, ItemStack reagent,
      EnchantingApparatusTile enchantingApparatusTile) {
    ItemStack result = this.result.copy();
    if (reagent.hasTag()) {
      result.setTag(reagent.getTag());
      result.setDamageValue(0);
    }
    return result.copy();
  }

  @Override
  public RecipeType<?> getType() {
    return RecipeRegistry.TECHNOMANCER_ARMOR_UP.get();
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return RecipeRegistry.TECHNOMANCER_ARMOR_UP_SERIALIZER.get();
  }

  /**
   * Returns the component that should be displayed in the output slot.
   */
  @Override
  public Component getOutputComponent() {
    return Component.translatable("ars_nouveau.armor_upgrade.book_desc", tier);
  }

  @Override
  public boolean excludeJei() {
    return true;
  }

  public static class Serializer implements RecipeSerializer<TechnomancerArmorRecipe> {

    @Override
    public @NotNull TechnomancerArmorRecipe fromJson(@NotNull ResourceLocation recipeId, JsonObject json) {
      int tier = json.has("tier") ? GsonHelper.getAsInt(json, "tier") : 0;
      Ingredient reagent = Ingredient.fromJson(GsonHelper.getAsJsonArray(json, "reagent"));
      ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "output"));
      int cost = json.has("sourceCost") ? GsonHelper.getAsInt(json, "sourceCost") : 0;
      JsonArray pedestalItems = GsonHelper.getAsJsonArray(json, "pedestalItems");
      List<Ingredient> stacks = new ArrayList<>();

      for (JsonElement e : pedestalItems) {
        JsonObject obj = e.getAsJsonObject();
        Ingredient input;
        if (GsonHelper.isArrayNode(obj, "item")) {
          input = Ingredient.fromJson(GsonHelper.getAsJsonArray(obj, "item"));
        } else {
          input = Ingredient.fromJson(GsonHelper.getAsJsonObject(obj, "item"));
        }
        stacks.add(input);
      }
      return new TechnomancerArmorRecipe(recipeId, stacks, reagent, output, cost, tier);
    }

    @Nullable
    @Override
    public TechnomancerArmorRecipe fromNetwork(@NotNull ResourceLocation recipeId, FriendlyByteBuf buffer) {

      int length = buffer.readInt();
      Ingredient reagent = Ingredient.fromNetwork(buffer);
      ItemStack output = buffer.readItem();
      List<Ingredient> stacks = new ArrayList<>();

      for (int i = 0; i < length; i++) {
        try {
          stacks.add(Ingredient.fromNetwork(buffer));
        } catch (Exception e) {
          e.printStackTrace();
          break;
        }
      }
      int cost = buffer.readInt();
      int tier = buffer.readInt();
      return new TechnomancerArmorRecipe(recipeId, stacks, reagent, output, cost, tier);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf, TechnomancerArmorRecipe recipe) {
      buf.writeInt(recipe.pedestalItems.size());
      recipe.reagent.toNetwork(buf);
      buf.writeItem(recipe.result);
      for (Ingredient i : recipe.pedestalItems) {
        i.toNetwork(buf);
      }
      buf.writeInt(recipe.sourceCost);
      buf.writeInt(recipe.tier);
    }
  }

}