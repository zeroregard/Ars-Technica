package com.zeroregard.ars_technica.recipe;

import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.zeroregard.ars_technica.ArsTechnica;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class JeiArsExtraPlugin implements IModPlugin {

    public static final RecipeType<TechnomancerArmorRecipe> TECHNOMANCER_ARMOR_TYPE = RecipeType.create(ArsTechnica.MODID, "armor_upgrade", TechnomancerArmorRecipe.class);

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(ArsTechnica.MODID, "main");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        registry.addRecipeCategories(
                new TechnomancerUpgradeRecipeCategory(registry.getJeiHelpers().getGuiHelper())
        );
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registry) {
        assert Minecraft.getInstance().level != null;
        RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();
        List<TechnomancerArmorRecipe> armorRecipes = new ArrayList<>();
        for (RecipeHolder<?> i : manager.getRecipes()) {
            if (i.value() instanceof TechnomancerArmorRecipe aer) {
                armorRecipes.add(aer);
            }
        }
        registry.addRecipes(TECHNOMANCER_ARMOR_TYPE, armorRecipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
        registry.addRecipeCatalyst(new ItemStack(BlockRegistry.ENCHANTING_APP_BLOCK), TECHNOMANCER_ARMOR_TYPE);
    }

}

