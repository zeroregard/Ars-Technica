package com.zeroregard.ars_technica.mixin;

import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.common.crafting.recipes.CrushRecipe;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectCrush;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.zeroregard.ars_technica.helpers.SpellResolverHelpers;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.IdentityHashMap;
import java.util.List;

@Mixin(EffectCrush.class)
public class EffectCrushMixin {
    @Unique
    private static final IdentityHashMap<List<?>, SpellResolver> ars_technica$crushedItemTracker = new IdentityHashMap<>();

    @WrapOperation(method = "onResolve", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"))
    private List<?> trackCrushedItems(Level instance, Class<?> aClass, AABB aabb, Operation<List<?>> original, @Local(argsOnly = true) SpellResolver resolver, @Local(argsOnly = true) SpellStats spellStats) {
        var ret = original.call(instance, aClass, aabb);
        if (!ret.isEmpty()) {
            ars_technica$crushedItemTracker.put(ret, resolver);
        }
        return ret;
    }

    @WrapOperation(method = "crushItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeHolder;value()Lnet/minecraft/world/item/crafting/Recipe;"))
    private static <T extends Recipe<?>> T rollForItems(RecipeHolder<CrushRecipe> instance, Operation<CrushRecipe> original, @Local(argsOnly = true) List<ItemEntity> itemEntities) {
        var recipe = ars_Technica$adaptRecipe(instance.value(), ars_technica$crushedItemTracker.remove(itemEntities));
        //noinspection unchecked
        return (T) original.call(new RecipeHolder<>(instance.id(), recipe));
    }

    @WrapOperation(method = "onResolveBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeHolder;value()Lnet/minecraft/world/item/crafting/Recipe;"))
    private <T extends Recipe<?>> T rollForBlocks(RecipeHolder<CrushRecipe> instance, Operation<CrushRecipe> original, @Local(argsOnly = true) SpellResolver resolver) {
        var recipe = ars_Technica$adaptRecipe(instance.value(), resolver);
        //noinspection unchecked
        return (T) original.call(new RecipeHolder<>(instance.id(), recipe));
    }

    @Unique
    private static CrushRecipe ars_Technica$adaptRecipe(CrushRecipe recipe, @Nullable SpellResolver resolver) {
        return SpellResolverHelpers.shouldDoubleOutputs(resolver) ? new CrushRecipe(recipe.input(), recipe.outputs().stream().map(o -> o.chance() < 1.0 ? new CrushRecipe.CrushOutput(o.stack().copyWithCount(Math.min(o.stack().getMaxStackSize(), o.stack().getCount() * 2)), o.chance(), o.maxRange()) : o).toList()) : recipe;
    }
}
