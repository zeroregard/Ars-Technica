package net.mcreator.ars_technica.recipe.jei;

import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.mcreator.ars_technica.ArsTechnicaMod;
import net.mcreator.ars_technica.recipe.TechnomancerArmorRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class JeiArsExtraPlugin implements IModPlugin {

    public static final RecipeType<TechnomancerArmorRecipe> TECHNOMANCER_ARMOR_TYPE = RecipeType.create(ArsTechnicaMod.MODID, "armor_upgrade", TechnomancerArmorRecipe.class);

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return new ResourceLocation(ArsTechnicaMod.MODID, "main");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        registry.addRecipeCategories(
                new TechnomancerArmorUpgradeRecipeCategory(registry.getJeiHelpers().getGuiHelper())
        );
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registry) {
        assert Minecraft.getInstance().level != null;
        RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();
        List<TechnomancerArmorRecipe> armorRecipes = new ArrayList<>();
        for (Recipe<?> i : manager.getRecipes()) {
            if (i instanceof TechnomancerArmorRecipe atr) {
                armorRecipes.add(atr);
            }
        }
        registry.addRecipes(TECHNOMANCER_ARMOR_TYPE, armorRecipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
        registry.addRecipeCatalyst(new ItemStack(BlockRegistry.ENCHANTING_APP_BLOCK), TECHNOMANCER_ARMOR_TYPE);
    }

}