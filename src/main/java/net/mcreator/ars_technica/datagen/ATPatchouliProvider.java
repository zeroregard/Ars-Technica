package net.mcreator.ars_technica.datagen;


import com.hollingsworth.arsnouveau.api.perk.IPerk;
import com.hollingsworth.arsnouveau.api.registry.PerkRegistry;
import com.hollingsworth.arsnouveau.api.spell.AbstractCastMethod;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.datagen.PatchouliProvider;
import com.hollingsworth.arsnouveau.common.datagen.patchouli.*;
import com.hollingsworth.arsnouveau.common.items.PerkItem;
import net.mcreator.ars_technica.common.items.threads.PressurePerk;
import net.mcreator.ars_technica.setup.ItemsRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;


import static net.mcreator.ars_technica.setup.GlyphsRegistry.registeredSpells;

public class ATPatchouliProvider extends PatchouliProvider {

    public ATPatchouliProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    public void collectJsons(CachedOutput cache) {

        for (AbstractSpellPart spell : registeredSpells) {
            addGlyphPage(spell);
        }

        // TODO
        /*
        addPage(new PatchouliBuilder(EQUIPMENT, ItemsRegistry.TRANSMUTATION_FOCUS.get())
                        .withIcon(ItemsRegistry.TRANSMUTATION_FOCUS.get())
                        .withTextPage("ars_technica.patchouli.pages.transmutation_focus_1")
                        .withPage(ImbuementPage(ModItems.MANIPULATION_ESSENCE.get()))
                        .withTextPage("ars_technica.patchouli.pages.transmutation_focus_2")
                        .withPage(new ApparatusPage(ModItems.TRANSMUTATION_FOCUS.get()))
                        .withTextPage("ars_technica.patchouli.pages.transmutation_focus_3")
                , getPath(EQUIPMENT, "transmutation_focus")
        );
         */

        addPerkPage(PressurePerk.INSTANCE);

        for (PatchouliPage patchouliPage : pages) {
            saveStable(cache, patchouliPage.build(), patchouliPage.path());
        }

    }

    @Override
    public void addPerkPage(IPerk perk) {
        PerkItem perkItem = PerkRegistry.getPerkItemMap().get(perk.getRegistryName());
        PatchouliBuilder builder = new PatchouliBuilder(ARMOR, perkItem)
                .withIcon(perkItem)
                .withTextPage(perk.getDescriptionKey())
                .withPage(new ApparatusPage(perkItem)).withSortNum(99);
        this.pages.add(new PatchouliPage(builder, getPath(ARMOR, perk.getRegistryName().getPath())));
    }

    @Override
    public PatchouliPage addBasicItem(ItemLike item, ResourceLocation category, IPatchouliPage recipePage) {
        PatchouliBuilder builder = new PatchouliBuilder(category, item.asItem().getDescriptionId())
                .withIcon(item.asItem())
                .withPage(new TextPage("ars_technica.page." + getRegistryName(item.asItem()).getPath()))
                .withPage(recipePage);
        var page = new PatchouliPage(builder, getPath(category, getRegistryName(item.asItem()).getPath()));
        this.pages.add(page);
        return page;
    }

    public void addGlyphPage(AbstractSpellPart spellPart) {
        ResourceLocation category = switch (spellPart.defaultTier().value) {
            case 1 -> GLYPHS_1;
            case 2 -> GLYPHS_2;
            default -> GLYPHS_3;
        };
        PatchouliBuilder builder = new PatchouliBuilder(category, spellPart.getName())
                .withName("ars_technica.glyph_name." + spellPart.getRegistryName().getPath())
                .withIcon(spellPart.getRegistryName().toString())
                .withSortNum(spellPart instanceof AbstractCastMethod ? 1 : spellPart instanceof AbstractEffect ? 2 : 3)
                .withPage(new TextPage("ars_technica.glyph_desc." + spellPart.getRegistryName().getPath()))
                .withPage(new GlyphScribePage(spellPart));
        this.pages.add(new PatchouliPage(builder, getPath(category, spellPart.getRegistryName().getPath())));
    }


    /**
     * Gets a name for this provider, to use in logging.
     */
    @Override
    public String getName() {
        return "Ars Technica Patchouli Datagen";
    }

    ImbuementPage ImbuementPage(ItemLike item) {
        return new ImbuementPage("ars_technica:imbuement_" + getRegistryName(item.asItem()).getPath());
    }

    private ResourceLocation getRegistryName(Item asItem) {
        return BuiltInRegistries.ITEM.getKey(asItem);
    }


}