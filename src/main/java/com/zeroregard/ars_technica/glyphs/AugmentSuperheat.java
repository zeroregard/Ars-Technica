package com.zeroregard.ars_technica.glyphs;


import com.hollingsworth.arsnouveau.api.spell.*;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Set;

import static com.zeroregard.ars_technica.ArsTechnica.prefix;

public class AugmentSuperheat extends AbstractAugment {
    public static AugmentSuperheat INSTANCE = new AugmentSuperheat(prefix("glyph_superheat"), "Superheat");

    private AugmentSuperheat(ResourceLocation resourceLocation, String description) {
        super(resourceLocation, description);
    }
    @Override
    public String getBookDescription() {
        return "When used in combination with Fuse, super-heats ingredients";
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.THREE;
    }

    @Override
    public int getDefaultManaCost() {
        return 150;
    }

    @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ELEMENTAL_FIRE);
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf();
    }

}
