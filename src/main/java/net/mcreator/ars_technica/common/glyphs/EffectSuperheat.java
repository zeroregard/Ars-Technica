package net.mcreator.ars_technica.common.glyphs;


import com.hollingsworth.arsnouveau.api.spell.*;
import net.mcreator.ars_technica.ArsTechnicaMod;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Set;

public class EffectSuperheat extends AbstractEffect {
    public static final EffectSuperheat INSTANCE = new EffectSuperheat();

    private EffectSuperheat() {
        super(ResourceLocation.fromNamespaceAndPath(ArsTechnicaMod.MODID, "glyph_superheat"), "Superheat");
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
