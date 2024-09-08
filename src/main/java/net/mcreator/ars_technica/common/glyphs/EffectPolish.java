package net.mcreator.ars_technica.common.glyphs;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.equipment.sandPaper.SandPaperPolishingRecipe;
import net.mcreator.ars_technica.ArsTechnicaMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class EffectPolish extends AbstractItemResolveEffect {
    public static final EffectPolish INSTANCE = new EffectPolish();

    private EffectPolish() {
        super(new ResourceLocation(ArsTechnicaMod.MODID, "glyph_polish"), "Polish");
    }

    @Override
    public void onResolveEntities(List<ItemEntity> entityList, BlockPos pos, Vec3 posVec, Level world,
                                  @Nullable LivingEntity shooter,
                                  SpellStats spellStats,
                                  SpellContext spellContext, SpellResolver resolver) {
        RegistryAccess registryAccess = world.registryAccess();
        for (ItemEntity itemEntity : entityList) {
            ItemStack itemStack = itemEntity.getItem();
            int stackSize = itemStack.getCount();

            SandPaperPolishingRecipe.SandPaperInv sandpaperInventory = new SandPaperPolishingRecipe.SandPaperInv(itemStack);
            Optional<SandPaperPolishingRecipe> polishingRecipe = world.getRecipeManager()
                    .getRecipeFor(AllRecipeTypes.SANDPAPER_POLISHING.getType(), sandpaperInventory, world);

            if (polishingRecipe.isPresent()) {
                SandPaperPolishingRecipe recipe = polishingRecipe.get();
                ItemStack resultStack = recipe.getResultItem(registryAccess);
                resultStack.setCount(stackSize);
                itemEntity.discard();
                ItemEntity resultEntity = new ItemEntity(world, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), resultStack);
                world.addFreshEntity(resultEntity);
            }
        }
    }

    @Override
    public int getDefaultManaCost() {
        return 20;
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
        return "Refines items into their polished variants";
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.TWO;
    }
}