package com.zeroregard.ars_technica.glyphs;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.simibubi.create.content.equipment.sandPaper.SandPaperPolishingRecipe;
import com.zeroregard.ars_technica.ArsTechnica;
import com.zeroregard.ars_technica.entity.ArcanePolishEntity;
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

public class EffectPolish extends AbstractItemResolveEffect {
    public static EffectPolish INSTANCE = new EffectPolish(prefix("glyph_polish"), "Polish");

    private static float DEFAULT_SPEED = 2.0f;

    private EffectPolish(ResourceLocation resourceLocation, String description) {
        super(resourceLocation, description);
    }

    @Override
    public void onResolveEntities(List<ItemEntity> entityList, BlockPos pos, Vec3 posVec, Level world,
                                  @Nullable LivingEntity shooter,
                                  SpellStats spellStats,
                                  SpellContext spellContext, SpellResolver resolver) {
        List<ItemEntity> validPolishableEntities = new ArrayList<>();

        for (ItemEntity itemEntity : entityList) {
            ItemStack itemStack = itemEntity.getItem();

            var recipes = SandPaperPolishingRecipe.getMatchingRecipes(world, itemStack);
            if (!recipes.isEmpty()) {
                validPolishableEntities.add(itemEntity);
            }
        }

        boolean hasFocus = SpellResolverHelpers.hasTransmutationFocus(resolver);
        int aoeBuff = (int)Math.round(spellStats.getAoeMultiplier());
        int maxAmountToPolish = Math.round(4 * (1 + aoeBuff)) * (hasFocus ? 2 : 1);
        float speed = hasFocus ? DEFAULT_SPEED * 2.5f : DEFAULT_SPEED;
        var color = new Color(spellContext.getColors().getColor());

        if (!validPolishableEntities.isEmpty()) {
            ItemEntity closest = validPolishableEntities.stream()
                    .min(Comparator.comparingDouble(e -> e.position().distanceTo(posVec)))
                    .orElse(null);

            if (closest != null) {
                ArcanePolishEntity arcanePolishEntity = new ArcanePolishEntity(closest.position().add(0, 0.5f, 0), world, maxAmountToPolish, speed, color, validPolishableEntities);
                world.addFreshEntity(arcanePolishEntity);
            }
        }
    }

    @Override
    public int getDefaultManaCost() {
        return 120;
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
        return "Refines items into their polished variants";
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.TWO;
    }
}