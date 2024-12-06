package net.mcreator.ars_technica.common.glyphs;

import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import net.mcreator.ars_technica.ArsTechnicaMod;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;

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
        SpellContext newContext = spellContext.makeChildContext();
        spellContext.setCanceled(true);
        ArcaneFusionEntity arcaneFusionEntity = new ArcaneFusionEntity(target, position, world, shooter, color, resolver.getNewResolver(newContext), spellStats);
        world.addFreshEntity(arcaneFusionEntity);
    }

    @Override
    public int getDefaultManaCost() {
        return 150;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentSensitive.INSTANCE, AugmentAmplify.INSTANCE);
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.MANIPULATION, SpellSchools.ELEMENTAL_FIRE);
    }

    @Override
    public String getBookDescription() {
        return "Fuses items by slamming them together at breakneck speed";
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.THREE;
    }
}