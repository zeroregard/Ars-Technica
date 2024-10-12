package net.mcreator.ars_technica.common.glyphs;

import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import net.mcreator.ars_technica.ArsTechnicaMod;
import net.mcreator.ars_technica.common.crafting.DynamicCraftingContainer;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;

import net.mcreator.ars_technica.common.entity.ArcaneHammerEntity;
import net.mcreator.ars_technica.common.entity.ArcanePressEntity;
import net.mcreator.ars_technica.common.helpers.RecipeHelpers;
import net.mcreator.ars_technica.common.helpers.SpellResolverHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import net.mcreator.ars_technica.common.helpers.CraftingHelpers;
import net.mcreator.ars_technica.common.helpers.ItemHelpers;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.object.Color;

import javax.annotation.Nonnull;

import java.util.*;
import java.util.stream.Collectors;

public class EffectObliterate extends AbstractEffect {
    public static final EffectObliterate INSTANCE = new EffectObliterate();
    private static float DEFAULT_SPEED = 4.0f;

    private EffectObliterate() {
        super(new ResourceLocation(ArsTechnicaMod.MODID, "glyph_obliterate"), "Obliterate");
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
        ArcaneHammerEntity arcaneHammerEntity = new ArcaneHammerEntity(target, position, world, shooter, color, resolver, spellStats);
        setYaw(position, shooter, arcaneHammerEntity);
        world.addFreshEntity(arcaneHammerEntity);
    }

    private void setYaw(Vec3 position, @NotNull LivingEntity shooter, ArcaneHammerEntity arcaneHammerEntity) {
        Vec3 direction = position.subtract(shooter.position()).normalize();
        Vec3 rotation = new Vec3(0, 0, 0);
        float yaw = (float)(-Math.atan2(direction.z(), direction.x()) + Math.PI/2);
        arcaneHammerEntity.setYaw(yaw);
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