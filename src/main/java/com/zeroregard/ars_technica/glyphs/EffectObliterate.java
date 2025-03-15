package com.zeroregard.ars_technica.glyphs;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import com.zeroregard.ars_technica.entity.ArcaneHammerEntity;
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
import java.util.Map;
import java.util.Set;

import static com.zeroregard.ars_technica.ArsTechnica.prefix;

public class EffectObliterate extends AbstractEffect {
    public static EffectObliterate INSTANCE = new EffectObliterate(prefix("glyph_obliterate"), "Obliterate");

    private EffectObliterate(ResourceLocation resourceLocation, String description) {
        super(resourceLocation, description);
    }

    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        var target = rayTraceResult.getEntity();
        var position = target.getPosition(1.0f);
        resolve(target, position, world, shooter, spellStats, spellContext, resolver);
    }

    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        var position = rayTraceResult.getBlockPos().getCenter().add(0, 0.5, 0);
        resolve(null, position, world, shooter, spellStats, spellContext, resolver);
    }

    private void resolve(@Nullable Entity target, Vec3 position, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        var color = new Color(spellContext.getColors().getColor());
        SpellContext newContext = spellContext.makeChildContext();
        spellContext.setCanceled(true);
        ArcaneHammerEntity arcaneHammerEntity = new ArcaneHammerEntity(target, position, world, shooter, color, resolver.getNewResolver(newContext), spellStats);
        setYaw(position, shooter, arcaneHammerEntity);
        world.addFreshEntity(arcaneHammerEntity);
    }

    private void setYaw(Vec3 position, @NotNull LivingEntity shooter, ArcaneHammerEntity arcaneHammerEntity) {
        Vec3 direction = position.subtract(shooter.position()).normalize();
        float yaw = (float)(-Math.atan2(direction.z(), direction.x()) + Math.PI/2);
        arcaneHammerEntity.setYaw(yaw);
    }

    @Override
    public void addAugmentDescriptions(Map<AbstractAugment, String> map) {
        super.addAugmentDescriptions(map);
        map.put(AugmentSensitive.INSTANCE, "Processes items instead of destroying them");
        map.put(AugmentAmplify.INSTANCE, "Increases the size and damage of the hammer");
    }

    @Override
    public int getDefaultManaCost() {
        return 100;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentSensitive.INSTANCE, AugmentAmplify.INSTANCE);
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.MANIPULATION);
    }

    @Override
    public String getBookDescription() {
        return "Obliterates foes by sheer force with an arcane hammer. Use Sensitive to crush items instead of destroying them";
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.THREE;
    }
}