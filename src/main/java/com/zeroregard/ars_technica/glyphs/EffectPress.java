package com.zeroregard.ars_technica.glyphs;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtract;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.content.logistics.depot.DepotBlock;
import com.zeroregard.ars_technica.entity.ArcanePressEntity;
import com.zeroregard.ars_technica.helpers.RecipeHelpers;
import com.zeroregard.ars_technica.helpers.RecipeHelpers.CompactingMatch;
import com.zeroregard.ars_technica.helpers.SpellResolverHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
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
    public void onResolve(net.minecraft.world.phys.HitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, 
                          SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (rayTraceResult instanceof BlockHitResult blockHit) {
            BlockPos blockPos = blockHit.getBlockPos();
            BlockState state = world.getBlockState(blockPos);
            
            if (state.getBlock() instanceof DepotBlock) {
                boolean hasFocus = SpellResolverHelpers.hasTransmutationFocus(resolver);
                int aoeBuff = (int)Math.round(spellStats.getAoeMultiplier());
                int maxAmountToPress = Math.round(4 * (1 + aoeBuff)) * (hasFocus ? 2 : 1);
                float speed = hasFocus ? DEFAULT_SPEED * 2.5f : DEFAULT_SPEED;
                var color = new Color(spellContext.getSpell().color().getColor());
                
                Vec3 spawnPos = Vec3.atCenterOf(blockPos).add(0, 1.0, 0);
                ArcanePressEntity arcanePressEntity = new ArcanePressEntity(spawnPos, world, maxAmountToPress, speed, color, Collections.emptyList());
                arcanePressEntity.bindDepot(blockPos);
                world.addFreshEntity(arcanePressEntity);
                return;
            }
        }
        
        super.onResolve(rayTraceResult, world, shooter, spellStats, spellContext, resolver);
    }

    @Override
    public void onResolveEntities(List<ItemEntity> entityList, BlockPos pos, Vec3 posVec, Level world,
                                  @Nullable LivingEntity shooter,
                                  SpellStats spellStats,
                                  SpellContext spellContext, SpellResolver resolver) {
        boolean hasFocus = SpellResolverHelpers.hasTransmutationFocus(resolver);
        int aoeBuff = (int)Math.round(spellStats.getAoeMultiplier());
        int maxAmountToPress = Math.round(4 * (1 + aoeBuff)) * (hasFocus ? 2 : 1);
        float speed = hasFocus ? DEFAULT_SPEED * 2.5f : DEFAULT_SPEED;
        var color = new Color(spellContext.getSpell().color().getColor());

        if (spellStats.hasBuff(AugmentExtract.INSTANCE)) {
            Optional<CompactingMatch> compactMatch = RecipeHelpers.findCompactingMatch(entityList, posVec, world);
            if (compactMatch.isPresent()) {
                Vec3 spawnPos = posVec.add(0, 1.0, 0);
                ArcanePressEntity arcanePressEntity = new ArcanePressEntity(spawnPos, world, 1, speed, color, Collections.emptyList());
                arcanePressEntity.setCompactJob(compactMatch.get());
                world.addFreshEntity(arcanePressEntity);
                return;
            }
        }

        List<ItemEntity> validPressableEntities = new ArrayList<>();
        for (ItemEntity itemEntity : entityList) {
            ItemStack itemStack = itemEntity.getItem();
            if (RecipeHelpers.getPressingRecipeForItemStack(itemStack, world).isPresent()) {
                validPressableEntities.add(itemEntity);
            }
        }

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
        map.put(AugmentExtract.INSTANCE, "Uses Compact recipes instead of Press when items can form one");
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAOE.INSTANCE, AugmentExtract.INSTANCE);
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