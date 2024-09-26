package net.mcreator.ars_technica.common.glyphs;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import net.mcreator.ars_technica.ArsTechnicaMod;
import net.mcreator.ars_technica.common.crafting.DynamicCraftingContainer;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;

import net.mcreator.ars_technica.common.entity.ArcanePressEntity;
import net.mcreator.ars_technica.common.helpers.RecipeHelpers;
import net.mcreator.ars_technica.common.helpers.SpellResolverHelpers;
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

import java.util.*;
import java.util.stream.Collectors;

public class EffectPress extends AbstractItemResolveEffect {
    public static final EffectPress INSTANCE = new EffectPress();
    private static float DEFAULT_SPEED = 4.0f;

    private EffectPress() {
        super(new ResourceLocation(ArsTechnicaMod.MODID, "glyph_press"), "Press");
    }

    @Override
    public void onResolveEntities(List<ItemEntity> entityList, BlockPos pos, Vec3 posVec, Level world,
                                  @Nullable LivingEntity shooter,
                                  SpellStats spellStats,
                                  SpellContext spellContext, SpellResolver resolver) {
        List<ItemEntity> validPressableEntities = new ArrayList<>();

        for (ItemEntity itemEntity : entityList) {
            ItemStack itemStack = itemEntity.getItem();

            Optional<PressingRecipe> pressingRecipe = RecipeHelpers.getPressingRecipeForItemStack(itemStack, world);

            if (pressingRecipe.isPresent()) {
                validPressableEntities.add(itemEntity);
            }
        }

        boolean hasFocus = SpellResolverHelpers.hasTransmutationFocus(resolver);
        int aoeBuff = (int)Math.round(spellStats.getAoeMultiplier());
        int maxAmountToPress = Math.round(4 * (1 + aoeBuff)) * (hasFocus ? 2 : 1);
        float speed = hasFocus ? DEFAULT_SPEED * 2.5f : DEFAULT_SPEED;

        if (!validPressableEntities.isEmpty()) {
            ItemEntity closest = validPressableEntities.stream()
                    .min(Comparator.comparingDouble(e -> e.position().distanceTo(posVec)))
                    .orElse(null);

            if (closest != null) {
                ArcanePressEntity arcanePressEntity = new ArcanePressEntity(closest.position().add(0, 1.0f, 0), world, maxAmountToPress, speed, validPressableEntities);
                world.addFreshEntity(arcanePressEntity);
            }
        }
    }

    @Override
    public int getDefaultManaCost() {
        return 100;
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