package net.mcreator.ars_technica.common.glyphs;

import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectConjureWater;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectFlare;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectHex;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectSmelt;
import com.simibubi.create.content.kinetics.fan.processing.AllFanProcessingTypes;
import net.mcreator.ars_technica.ArsTechnicaMod;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;

import net.mcreator.ars_technica.ConfigHandler;
import net.mcreator.ars_technica.common.entity.fusion.ArcaneFusionEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.object.Color;

import javax.annotation.Nonnull;

import java.util.*;

public class EffectFuse extends AbstractEffect {
    public static final EffectFuse INSTANCE = new EffectFuse();

    private EffectFuse() {
        super(new ResourceLocation(ArsTechnicaMod.MODID, "glyph_fuse"), "Fuse");
    }

    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        var target = rayTraceResult.getEntity();
        var position = target.getPosition(1.0f);
        resolve(target, position, world, shooter, spellStats, spellContext, resolver);
    }

    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        var position = rayTraceResult.getLocation();
        resolve(null, position, world, shooter, spellStats, spellContext, resolver);
    }

    private void resolve(@Nullable Entity target, Vec3 position, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        var color = new Color(spellContext.getColors().getColor());
        String fusionTypeId = getFusionType(spellContext);
        SpellContext newContext = spellContext.makeChildContext();
        spellContext.setCanceled(true);
        ArcaneFusionEntity arcaneFusionEntity = new ArcaneFusionEntity(target, position, world, shooter, color, resolver.getNewResolver(newContext), spellStats, fusionTypeId);
        world.addFreshEntity(arcaneFusionEntity);
    }

    private String getFusionType(SpellContext spellContext) {
        String fusionType = "regular";
        if (spellContext.hasNextPart()) {
            while (spellContext.hasNextPart()) {
                AbstractSpellPart next = spellContext.nextPart();
                if (next instanceof AbstractEffect) {
                    if (next == EffectSmelt.INSTANCE) {
                        fusionType = "heated";
                    } else if (next == EffectSuperheat.INSTANCE) {
                        fusionType = "super";
                    }
                    break;
                }
            }
        }
        return fusionType;
    }

    @Override
    public int getDefaultManaCost() {
        return 75;
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
        return "Fuses items by slamming them together at breakneck speed. Use Smelt for heated mixing, use Superheat for super-heated mixing (if enabled in config)";
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.ONE;
    }
}