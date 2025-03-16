package com.zeroregard.ars_technica.glyphs;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.RuneCaster;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectConjureWater;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectFlare;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectHex;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectSmelt;
import com.simibubi.create.content.kinetics.fan.processing.AllFanProcessingTypes;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import com.zeroregard.ars_technica.entity.ArcaneWhirlEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Set;

import static com.zeroregard.ars_technica.ArsTechnica.prefix;


public class EffectWhirl extends AbstractEffect {
    public static EffectWhirl INSTANCE = new EffectWhirl(prefix("glyph_whirl"), "Whirl");
    public final float DEFAULT_RADIUS  = 1.5f;
    public final int DEFAULT_DURATION = 360;

    private EffectWhirl(ResourceLocation resourceLocation, String description) {
        super(resourceLocation, description);
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (!(world instanceof ServerLevel serverWorld)) return;
        Vec3 position = rayTraceResult.getLocation();
        if(spellContext.getCaster() instanceof RuneCaster runeCaster) {
            Vec3 casterPos = runeCaster.getPosition();
            position = casterPos.add(0.5f, 0, 0.5f);
        }
        resolve(position, serverWorld, shooter, spellStats, spellContext, resolver);
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (!(world instanceof ServerLevel serverWorld)) return;
        Vec3 adjustedPosition = getAdjustedPosition(rayTraceResult);
        resolve(adjustedPosition, serverWorld, shooter, spellStats, spellContext, resolver);
    }

    private Vec3 getAdjustedPosition(BlockHitResult rayTraceResult) {
        BlockPos blockPos = rayTraceResult.getBlockPos();
        Direction hitFace = rayTraceResult.getDirection();
        Vec3 center = Vec3.atCenterOf(blockPos);
        switch (hitFace) {
            case UP:
                return center.add(0, 0.5, 0);
            case DOWN:
                return center.add(0, -1, 0);
            case NORTH:
                return center.add(0, 0, -1);
            case SOUTH:
                return center.add(0, 0, 1);
            case WEST:
                return center.add(-1, 0, 0);
            case EAST:
                return center.add(1, 0, 0);
            default:
                return center;
        }
    }

    protected void resolve(Vec3 position, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {

        float aoeAmplifier = (float)spellStats.getAoeMultiplier();
        double durationAmplifier = spellStats.getDurationMultiplier();
        int extraDurationTicks = Math.toIntExact(Math.round(durationAmplifier * 40));

        FanProcessingType processingType = null;

        if (spellContext.hasNextPart()) {
            while (spellContext.hasNextPart()) {
                AbstractSpellPart next = spellContext.nextPart();
                if (next instanceof AbstractEffect) {
                    if (next == EffectConjureWater.INSTANCE) {
                        processingType = AllFanProcessingTypes.SPLASHING;
                    } else if (next == EffectFlare.INSTANCE) {
                        processingType = AllFanProcessingTypes.SMOKING;
                    } else if (next == EffectSmelt.INSTANCE) {
                        processingType = AllFanProcessingTypes.BLASTING;
                    } else if (next == EffectHex.INSTANCE) {
                        processingType = AllFanProcessingTypes.HAUNTING;
                    }
                    break;
                }
            }
        }

        ArcaneWhirlEntity whirl = new ArcaneWhirlEntity(world, position, DEFAULT_RADIUS + aoeAmplifier * 0.33f, DEFAULT_DURATION + extraDurationTicks, processingType, resolver);
        world.addFreshEntity(whirl);
    }

    @Override
    public int getDefaultManaCost() {
        return 40;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAOE.INSTANCE, AugmentExtendTime.INSTANCE);
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.MANIPULATION, SpellSchools.ELEMENTAL_AIR);
    }

    @Override
    public String getBookDescription() {
        return "Creates a whirlwind in an area, causing items to be centrifuged.";
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.TWO;
    }
}

