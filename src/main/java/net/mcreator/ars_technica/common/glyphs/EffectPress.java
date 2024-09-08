package net.mcreator.ars_technica.common.glyphs;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import net.mcreator.ars_technica.ArsTechnicaMod;
import net.mcreator.ars_technica.common.crafting.DynamicCraftingContainer;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.mcreator.ars_technica.common.helpers.CraftingHelpers;
import net.mcreator.ars_technica.common.helpers.ItemHelpers;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import javax.annotation.Nullable;
import javax.annotation.Nonnull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class EffectPress extends AbstractItemResolveEffect {
    public static final EffectPress INSTANCE = new EffectPress();

    private EffectPress() {
        super(new ResourceLocation(ArsTechnicaMod.MODID, "glyph_press"), "Press");
    }

    @Override
    public void onResolveEntities(List<ItemEntity> entityList, BlockPos pos, Vec3 posVec, Level world,
                                  @Nullable LivingEntity shooter,
                                  SpellStats spellStats,
                                  SpellContext spellContext, SpellResolver resolver) {
        RegistryAccess registryAccess = world.registryAccess();
        for (ItemEntity itemEntity : entityList) {
            ItemStack itemStack = itemEntity.getItem();
            int stackSize = itemStack.getCount();

            Optional<PressingRecipe> pressingRecipe = world.getRecipeManager()
                    .getRecipeFor(AllRecipeTypes.PRESSING.getType(), new RecipeWrapper(new net.minecraftforge.items.ItemStackHandler(1) {
                        {
                            setStackInSlot(0, itemStack);
                        }
                    }), world);

            if (pressingRecipe.isPresent()) {
                PressingRecipe recipe = pressingRecipe.get();
                ItemStack resultStack = recipe.getResultItem(registryAccess);
                resultStack.setCount(stackSize);
                itemEntity.discard();
                ItemEntity resultEntity = new ItemEntity(world, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), resultStack);
                world.addFreshEntity(resultEntity);
            }
        }
    }

    @Override
    public int getDefaultManaCost() {
        return 20;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAOE.INSTANCE);
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.MANIPULATION);
    }

    @Override
    public String getBookDescription() {
        return "Flattens items into their pressed variants";
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.TWO;
    }
}