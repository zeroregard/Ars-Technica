package com.zeroregard.ars_technica.glyphs;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtract;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectSmelt;
import com.simibubi.create.content.logistics.depot.DepotBlock;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.zeroregard.ars_technica.entity.ArcaneCompactEntity;
import com.zeroregard.ars_technica.entity.ArcanePackEntity;
import com.zeroregard.ars_technica.entity.ArcanePressEntity;
import com.zeroregard.ars_technica.entity.fusion.fluids.ArcaneFusionFluids;
import com.zeroregard.ars_technica.entity.fusion.fluids.FluidSourceProvider;
import com.zeroregard.ars_technica.helpers.RecipeHelpers;
import com.zeroregard.ars_technica.helpers.SpellResolverHelpers;
import com.zeroregard.ars_technica.saucelib.api.compound.ISubsequentEffectProvider;
import com.zeroregard.ars_technica.saucelib.api.compound.SubsequentContextHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.util.Color;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

import static com.zeroregard.ars_technica.ArsTechnica.prefix;

public class EffectPress extends AbstractItemResolveEffect implements ISubsequentEffectProvider {
    public static EffectPress INSTANCE = new EffectPress(prefix("glyph_press"), "Press");
    private static float DEFAULT_SPEED = 4.0f;

    /** With Extract: Smelt as next effect enables heated Compact recipes. */
    private static final ResourceLocation[] SUBSEQUENT_GLYPHS = new ResourceLocation[]{ EffectSmelt.INSTANCE.getRegistryName() };

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

        if (spellStats.hasBuff(AugmentSensitive.INSTANCE)) {
            int gridSize = getPackGridSize(spellStats.getAmpMultiplier());
            if (!RecipeHelpers.canDoAnyPack(entityList, gridSize, world)) {
                return;
            }
            Vec3 spawnPos = posVec.add(0, 1.0, 0);
            ArcanePackEntity packEntity = new ArcanePackEntity(spawnPos, world, maxAmountToPress, speed, color, entityList, pos, gridSize);
            world.addFreshEntity(packEntity);
            return;
        }

        if (spellStats.hasBuff(AugmentExtract.INSTANCE)) {
            // Gather fluids at spell impact and at shooter so "cast near tank" works even if ray hit is elsewhere
            List<FluidSourceProvider> nearbyFluids = new ArrayList<>(
                ArcaneFusionFluids.pickupFluidsAround(world, BlockPos.containing(posVec), 8));
            if (shooter != null) {
                BlockPos shooterBlock = shooter.blockPosition();
                if (!shooterBlock.equals(BlockPos.containing(posVec))) {
                    nearbyFluids.addAll(ArcaneFusionFluids.pickupFluidsAround(world, shooterBlock, 8));
                }
            }
            HeatCondition suppliedHeat = getSuppliedHeatForCompact(spellStats, spellContext);
            if (RecipeHelpers.findCompactingMatch(entityList, nearbyFluids, posVec, world, suppliedHeat).isEmpty()) {
                return;
            }
            Vec3 spawnPos = posVec.add(0, 1.0, 0);
            ArcaneCompactEntity compactEntity = new ArcaneCompactEntity(spawnPos, world, maxAmountToPress, speed, color, entityList, nearbyFluids, posVec, suppliedHeat);
            world.addFreshEntity(compactEntity);
            return;
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

    /** When using Extract (Compact): Superheat augment → SUPERHEATED; Smelt as next effect in spell (like Fuse) → HEATED; else NONE. */
    private static HeatCondition getSuppliedHeatForCompact(SpellStats spellStats, SpellContext spellContext) {
        if (spellStats.hasBuff(AugmentSuperheat.INSTANCE)) return HeatCondition.SUPERHEATED;
        SpellContext child = spellContext.makeChildContext();
        while (child.hasNextPart()) {
            AbstractSpellPart next = child.nextPart();
            if (next instanceof AbstractEffect) {
                if (next == EffectSmelt.INSTANCE) return HeatCondition.HEATED;
                break;
            }
        }
        return HeatCondition.NONE;
    }

    private static int getPackGridSize(double amplifier) {
        int value = (int) amplifier + 2;
        value = Math.max(value, 1);
        value = Math.min(value, 3);
        return value;
    }

    /** Derives the active Press mode from the cluster (effect at 0 plus following augments/effects). */
    public static PressMode getPressModeFromCluster(List<AbstractSpellPart> cluster) {
        if (cluster == null || cluster.isEmpty()) return PressMode.PRESSING;
        boolean hasSensitive = cluster.stream().anyMatch(AugmentSensitive.INSTANCE::equals);
        if (hasSensitive) return PressMode.PACKING;
        boolean hasExtract = cluster.stream().anyMatch(AugmentExtract.INSTANCE::equals);
        if (!hasExtract) return PressMode.PRESSING;
        boolean hasSuperheat = cluster.stream().anyMatch(AugmentSuperheat.INSTANCE::equals);
        if (hasSuperheat) return PressMode.SUPERHEATED_COMPACTING;
        for (int i = 1; i < cluster.size(); i++) {
            AbstractSpellPart part = cluster.get(i);
            if (part instanceof AbstractEffect) {
                return part == EffectSmelt.INSTANCE ? PressMode.HEATED_COMPACTING : PressMode.COMPACTING;
            }
        }
        return PressMode.COMPACTING;
    }

    /** Only base recipe types for the "(Create Processing: ...)" shift tooltip: Press, Compact, Pack. */
    @Override
    public List<Component> getDefaultAdditionalTooltip() {
        return List.of(
            Component.literal("Press"),
            Component.literal("Compact"),
            Component.literal("Pack")
        );
    }

    @Override
    public Component getSpellContextAdditionalTooltip(List<AbstractSpellPart> spell, int thisGlyphIndex) {
        List<AbstractSpellPart> cluster = SubsequentContextHelper.getCluster(spell, thisGlyphIndex);
        return Component.literal(getPressModeFromCluster(cluster).getRecipeDisplayName());
    }

    @Override
    public ResourceLocation[] getSubsequentEffectGlyphs() {
        return SUBSEQUENT_GLYPHS;
    }

    @Override
    public boolean isPartInCluster(AbstractSpellPart part) {
        if (part == null) return false;
        if (part instanceof AbstractAugment augment) {
            return getCompatibleAugments() != null && getCompatibleAugments().contains(augment);
        }
        if (part instanceof AbstractEffect nextEffect) {
            ResourceLocation id = nextEffect.getRegistryName();
            if (SUBSEQUENT_GLYPHS == null || id == null) return false;
            for (ResourceLocation sid : SUBSEQUENT_GLYPHS) {
                if (sid != null && sid.equals(id)) return true;
            }
        }
        return false;
    }

    @Override
    public int getDefaultManaCost() {
        return 100;
    }

    @Override
    public void addAugmentDescriptions(Map<AbstractAugment, String> map) {
        super.addAugmentDescriptions(map);
        map.put(AugmentAOE.INSTANCE, "Increases the amount of items processed");
        map.put(AugmentExtract.INSTANCE, "Uses Compact recipes (with nearby fluids) instead of Press when items can form one");
        map.put(AugmentSensitive.INSTANCE, "Uses Packing (2x2/3x3 same-item crafting) instead of Press or Compact");
        map.put(AugmentSuperheat.INSTANCE, "With Extract: allows super-heated Compact recipes");
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAOE.INSTANCE, AugmentExtract.INSTANCE, AugmentSensitive.INSTANCE, AugmentSuperheat.INSTANCE);
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