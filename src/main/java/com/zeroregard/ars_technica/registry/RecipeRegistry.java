package com.zeroregard.ars_technica.registry;

import com.zeroregard.ars_technica.ArsTechnica;
import com.zeroregard.ars_technica.recipe.TechnomancerArmorRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.zeroregard.ars_technica.ArsTechnica.prefix;

public class RecipeRegistry {
  public static final DeferredRegister<RecipeType<?>> RECIPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, ArsTechnica.MODID);

  public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, ArsTechnica.MODID);

  public static void register(IEventBus eventBus) {
    RECIPES.register(eventBus);
    SERIALIZERS.register(eventBus);
  }

  public static final DeferredHolder<RecipeType<?>, RecipeType<TechnomancerArmorRecipe>> TECHNOMANCER_ARMOR_UP;
  public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<TechnomancerArmorRecipe>> TECHNOMANCER_ARMOR_UP_SERIALIZER;

  static {
    TECHNOMANCER_ARMOR_UP = RECIPES.register("armor_upgrade", () -> RecipeType.simple(prefix("armor_upgrade")));
    TECHNOMANCER_ARMOR_UP_SERIALIZER = SERIALIZERS.register("armor_upgrade", TechnomancerArmorRecipe.Serializer::new);
  }

}
