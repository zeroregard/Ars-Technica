package com.zeroregard.ars_technica.glyphs;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.zeroregard.ars_technica.entity.ArcanePressEntity;
import com.zeroregard.ars_technica.helpers.RecipeHelpers;
import com.zeroregard.ars_technica.helpers.SpellResolverHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.util.Color;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

import static com.zeroregard.ars_technica.ArsTechnica.prefix;

public class EffectPress extends AbstractItemResolveEffect {
    public static EffectPress INSTANCE = new EffectPress(prefix("glyph_press"), "Press");
    private static float DEFAULT_SPEED = 4.0f;

    private EffectPress(ResourceLocation resourceLocation, String description) {
        super(resourceLocation, description);
    }

    @Override
    public void onResolveEntities(List<ItemEntity> entityList, BlockPos pos, Vec3 posVec, Level world,
                                  @Nullable LivingEntity shooter,
                                  SpellStats spellStats,
                                  SpellContext spellContext, SpellResolver resolver) {
        List<ItemEntity> validPressableEntities = new ArrayList<>();

        for (ItemEntity itemEntity : entityList) {
            ItemStack itemStack = itemEntity.getItem();

            var pressingRecipe = RecipeHelpers.getPressingRecipeForItemStack(itemStack, world);

            if (pressingRecipe.isPresent()) {
                validPressableEntities.add(itemEntity);
            }
        }

        boolean hasFocus = SpellResolverHelpers.hasTransmutationFocus(resolver);
        int aoeBuff = (int)Math.round(spellStats.getAoeMultiplier());
        int maxAmountToPress = Math.round(4 * (1 + aoeBuff)) * (hasFocus ? 2 : 1);
        float speed = hasFocus ? DEFAULT_SPEED * 2.5f : DEFAULT_SPEED;
        var color = new Color(spellContext.getColors().getColor());

        if (!validPressableEntities.isEmpty()) {
            ItemEntity closest = validPressableEntities.stream()
                    .min(Comparator.comparingDouble(e -> e.position().distanceTo(posVec)))
                    .orElse(null);

            if (closest != null) {
                ArcanePressEntity arcanePressEntity = new ArcanePressEntity(closest.position().add(0, 1.0f, 0), world, maxAmountToPress, speed, color, validPressableEntities);
                world.addFreshEntity(arcanePressEntity);
            }
        }
    }

    @Override
    public int getDefaultManaCost() {
        return 100;
    }

    @Override
    public void addAugmentDescriptions(Map<AbstractAugment, String> map) {
        super.addAugmentDescriptions(map);
        map.put(AugmentAOE.INSTANCE, "Increases the amount of items processed");
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