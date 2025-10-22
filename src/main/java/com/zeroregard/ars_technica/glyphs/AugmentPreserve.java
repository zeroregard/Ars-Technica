package com.zeroregard.ars_technica.glyphs;

import com.hollingsworth.arsnouveau.api.spell.*;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Set;

import static com.zeroregard.ars_technica.ArsTechnica.prefix;

public class AugmentPreserve extends AbstractAugment {
    public static AugmentPreserve INSTANCE = new AugmentPreserve(prefix("glyph_preserve"), "Preserve");

    private AugmentPreserve(ResourceLocation resourceLocation, String description) {
        super(resourceLocation, description);
    }

    @Override
    public String getBookDescription() {
        return "When used with pierce, preserves the container item (like potion flasks or buckets) and only consumes the contents";
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.TWO;
    }

    @Override
    public int getDefaultManaCost() {
        return 50;
    }

    @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.MANIPULATION);
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf();
    }
}
