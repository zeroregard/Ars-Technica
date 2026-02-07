package com.zeroregard.ars_technica.events;

import com.zeroregard.ars_technica.ArsTechnica;
import com.zeroregard.ars_technica.client.gui.documentation.ProcessingIntroWithGlyphsPage;
import com.zeroregard.ars_technica.glyphs.EffectFuse;
import com.zeroregard.ars_technica.glyphs.EffectObliterate;
import com.zeroregard.ars_technica.glyphs.EffectPolish;
import com.zeroregard.ars_technica.glyphs.EffectPress;
import com.zeroregard.ars_technica.glyphs.EffectWhirl;
import com.simibubi.create.AllItems;
import com.zeroregard.ars_technica.helpers.ArcaneWrenchHelper;
import com.zeroregard.ars_technica.registry.BlockRegistry;
import com.zeroregard.ars_technica.registry.ItemRegistry;
import com.hollingsworth.arsnouveau.api.documentation.ReloadDocumentationEvent;
import com.hollingsworth.arsnouveau.api.documentation.builder.DocEntryBuilder;
import com.hollingsworth.arsnouveau.api.documentation.entry.TextEntry;
import com.hollingsworth.arsnouveau.api.registry.DocumentationRegistry;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.setup.registry.Documentation;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLEnvironment;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = ArsTechnica.MODID)
public class DocumentationEvents {

    @SubscribeEvent
    public static void onAddDocs(ReloadDocumentationEvent.AddEntries event) {
        // Crafting & Automation
        DocEntryBuilder processingBuilder = new DocEntryBuilder(ArsTechnica.MODID, DocumentationRegistry.CRAFTING, "create_processing")
                .withName("ars_technica.page.create_processing")
                .withIcon(EffectPress.INSTANCE.getGlyph());
        if (FMLEnvironment.dist == Dist.CLIENT) {
            processingBuilder
                    .withPage(ProcessingIntroWithGlyphsPage.create("ars_technica.page.create_processing", "ars_technica.page.create_processing.intro", processingGlyphStacks()))
                    .withPage(TextEntry.create("ars_technica.page.create_processing.depot"));
        } else {
            processingBuilder.withPage(TextEntry.create("ars_technica.page1.create_processing", "ars_technica.page.create_processing"));
        }
        Documentation.addPage(processingBuilder);

        Documentation.addPage(new DocEntryBuilder(ArsTechnica.MODID, DocumentationRegistry.CRAFTING, "transmutation")
                .withIcon(ItemRegistry.TRANSMUTATION_FOCUS.get())
                .withPage(TextEntry.create("ars_technica.page1.transmutation", "ars_technica.page.transmutation"))
                .withCraftingPages(ItemRegistry.TRANSMUTATION_FOCUS.get())
                .withCraftingPages(BlockRegistry.TRANSMUTATION_TURRET.get()));

        // Items and Equipment - withCraftingPages finds Enchanting Apparatus recipe runic_spanner (output: create:wrench)
        Documentation.addPage(new DocEntryBuilder(ArsTechnica.MODID, DocumentationRegistry.ITEMS, "arcane_wrench")
                .withIcon(ArcaneWrenchHelper.createArcaneWrench())
                .withPage(TextEntry.create("ars_technica.page1.arcane_wrench", "ars_technica.page.arcane_wrench"))
                .withCraftingPages(AllItems.WRENCH.get())
                .withTextPage("ars_technica.page2.arcane_wrench"));

        Documentation.addPage(new DocEntryBuilder(ArsTechnica.MODID, DocumentationRegistry.ARMOR, "technomancer_set")
                .withName("ars_technica.armor_set.technomancer")
                .withIcon(ItemRegistry.TECHNOMANCER_HELMET.get())
                .withPage(TextEntry.create("ars_technica.page.armor_set.technomancer", "ars_technica.armor_set.technomancer"))
                .withCraftingPages(ItemRegistry.TECHNOMANCER_HELMET.get())
                .withCraftingPages(ItemRegistry.TECHNOMANCER_CHESTPLATE.get())
                .withCraftingPages(ItemRegistry.TECHNOMANCER_LEGGINGS.get())
                .withCraftingPages(ItemRegistry.TECHNOMANCER_BOOTS.get())
                .withCraftingPages(ItemRegistry.ARTIFICER_CAP.get())
                .withCraftingPages(ItemRegistry.ARTIFICER_TUNIC.get())
                .withCraftingPages(ItemRegistry.ARTIFICER_PANTS.get())
                .withCraftingPages(ItemRegistry.ARTIFICER_SHOES.get())
                .withCraftingPages(ItemRegistry.MACHINAGUARD_HELMET.get())
                .withCraftingPages(ItemRegistry.MACHINAGUARD_CHESTPLATE.get())
                .withCraftingPages(ItemRegistry.MACHINAGUARD_LEGGINGS.get())
                .withCraftingPages(ItemRegistry.MACHINAGUARD_BOOTS.get()));
    }

    /** Glyph stacks for Create-processing glyphs (Press, Polish, Obliterate, Whirl, Fuse). Carve and Pack excluded. */
    private static List<ItemStack> processingGlyphStacks() {
        List<ItemStack> stacks = new ArrayList<>();
        for (AbstractSpellPart spellPart : List.of(
                EffectPress.INSTANCE, EffectPolish.INSTANCE, EffectObliterate.INSTANCE, EffectWhirl.INSTANCE,
                EffectFuse.INSTANCE)) {
            if (spellPart.getGlyph() != null) {
                stacks.add(spellPart.getGlyph().getDefaultInstance());
            }
        }
        return stacks;
    }
}
