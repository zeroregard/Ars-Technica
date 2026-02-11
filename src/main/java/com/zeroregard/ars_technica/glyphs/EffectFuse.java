package com.zeroregard.ars_technica.glyphs;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectSmelt;
import com.zeroregard.ars_technica.entity.fusion.ArcaneFusionEntity;
import com.zeroregard.ars_technica.saucelib.api.compound.ISubsequentEffectProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.util.Color;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.zeroregard.ars_technica.ArsTechnica.prefix;

public class EffectFuse extends AbstractEffect implements ISubsequentEffectProvider {

    private static final ResourceLocation[] SUBSEQUENT = new ResourceLocation[]{ EffectSmelt.INSTANCE.getRegistryName() };
    public static EffectFuse INSTANCE = new EffectFuse(prefix("glyph_fuse"), "Fuse");

    private EffectFuse(ResourceLocation resourceLocation, String description) {
        super(resourceLocation, description);
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
        var color = new Color(spellContext.getSpell().color().getColor());
        String fusionTypeId = getFusionType(spellStats, spellContext);
        SpellContext newContext = spellContext.makeChildContext();
        spellContext.setCanceled(true);
        ArcaneFusionEntity arcaneFusionEntity = new ArcaneFusionEntity(target, position, world, shooter, color, resolver.getNewResolver(newContext), spellStats, fusionTypeId);
        world.addFreshEntity(arcaneFusionEntity);
    }

    private String getFusionType(SpellStats spellStats, SpellContext spellContext) {
        String fusionType = "regular";
        var augments = spellStats.getAugments();
        boolean hasSuperheat = augments.stream()
                .anyMatch(augment -> augment instanceof AugmentSuperheat);

        if(hasSuperheat) {
            fusionType = "super";
            return fusionType;
        }

        if (spellContext.hasNextPart()) {
            while (spellContext.hasNextPart()) {
                AbstractSpellPart next = spellContext.nextPart();
                if (next instanceof AbstractEffect) {
                    if (next == EffectSmelt.INSTANCE) {
                        fusionType = "heated";
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

    @Override
    public void addAugmentDescriptions(Map<AbstractAugment, String> map) {
        super.addAugmentDescriptions(map);
        map.put(AugmentAOE.INSTANCE, "Increases the amount of items being processed");
        map.put(AugmentSuperheat.INSTANCE, "Switches to super-heated mixing");
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAOE.INSTANCE, AugmentSuperheat.INSTANCE);
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

    @Override
    public ResourceLocation[] getSubsequentEffectGlyphs() {
        return SUBSEQUENT;
    }

    @Override
    public boolean isPartInCluster(AbstractSpellPart part) {
        return part == AugmentSuperheat.INSTANCE || part == EffectSmelt.INSTANCE;
    }

    @Override
    public List<Component> getDefaultAdditionalTooltip() {
        return List.of(
                Component.literal("Mixing"),
                Component.literal("Heated Mixing"),
                Component.literal("Superheated Mixing"));
    }

    @Override
    public Component getSpellContextAdditionalTooltip(List<AbstractSpellPart> spell, int thisGlyphIndex) {
        for (int i = thisGlyphIndex + 1; i < spell.size(); i++) {
            AbstractSpellPart part = spell.get(i);
            if (part == null) continue;
            if (part instanceof AbstractAugment && part == AugmentSuperheat.INSTANCE)
                return Component.literal("Superheated Mixing");
            if (part instanceof AbstractEffect) {
                return part == EffectSmelt.INSTANCE ? Component.literal("Heated Mixing") : Component.literal("Mixing");
            }
        }
        return Component.literal("Mixing");
    }
}