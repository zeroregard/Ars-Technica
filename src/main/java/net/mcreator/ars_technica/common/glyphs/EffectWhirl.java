package net.mcreator.ars_technica.common.glyphs;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectConjureWater;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectFlare;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectHex;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectSmelt;
import com.simibubi.create.content.kinetics.fan.processing.AllFanProcessingTypes;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import net.mcreator.ars_technica.ArsTechnicaMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import java.util.Set;

import net.mcreator.ars_technica.common.entity.WhirlEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;


public class EffectWhirl extends AbstractEffect {
public static final EffectWhirl INSTANCE = new EffectWhirl();

    private final double defaultRadius = 1.5;
    private final int defaultDuration = 360;

    private EffectWhirl() {
        super(new ResourceLocation(ArsTechnicaMod.MODID, "glyph_whirl"), "Whirl");
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (!(world instanceof ServerLevel serverWorld)) return;
        Vec3 position = rayTraceResult.getLocation();
        resolve(position, serverWorld, shooter, spellStats, spellContext, resolver);
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (!(world instanceof ServerLevel serverWorld)) return;
        Vec3 position = rayTraceResult.getLocation();
        resolve(position, serverWorld, shooter, spellStats, spellContext, resolver);
    }

    protected void resolve(Vec3 position, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {

        double aoeAmplifier = spellStats.getAoeMultiplier();
        double durationAmplifier = spellStats.getDurationMultiplier();
        int extraDurationTicks = Math.toIntExact(Math.round(durationAmplifier * 40));

        FanProcessingType processingType = AllFanProcessingTypes.NONE;

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

        WhirlEntity whirl = new WhirlEntity(world, position, defaultRadius + aoeAmplifier, defaultDuration + extraDurationTicks, processingType, resolver);
        world.addFreshEntity(whirl);

    }


    @Override
    public int getDefaultManaCost() {
        return 20;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAOE.INSTANCE, AugmentAmplify.INSTANCE, AugmentExtendTime.INSTANCE);
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.MANIPULATION, SpellSchools.ELEMENTAL_AIR);
    }

    @Override
    public String getBookDescription() {
        return "Creates a whirlwind in an area, causing items to be centrifuged";
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.TWO;
    }
}

