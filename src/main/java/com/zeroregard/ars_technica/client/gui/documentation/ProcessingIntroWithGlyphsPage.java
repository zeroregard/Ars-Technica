package com.zeroregard.ars_technica.client.gui.documentation;

import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.arsnouveau.api.documentation.SinglePageCtor;
import com.hollingsworth.arsnouveau.api.documentation.SinglePageWidget;
import com.hollingsworth.arsnouveau.client.gui.documentation.BaseDocScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class ProcessingIntroWithGlyphsPage extends SinglePageWidget {

    private static final float GLYPH_SCALE = 1.0f;
    private static final int SLOT_SIZE = 16;
    private static final int PADDING = 2;
    private static final int INTRO_TO_GLYPHS_GAP = 8;
    private static final int TITLE_HEIGHT = 14;

    private final Component title;
    private final Component introText;
    private final List<ItemStack> stacks;

    public ProcessingIntroWithGlyphsPage(Component title, Component introText, List<ItemStack> stacks,
                                        BaseDocScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
        this.title = title;
        this.introText = introText;
        this.stacks = stacks != null ? stacks : List.of();
    }

    public static SinglePageCtor create(String titleKey, String introKey, List<ItemStack> stacks) {
        return (parent, x, y, width, height) -> new ProcessingIntroWithGlyphsPage(
                Component.translatable(titleKey),
                Component.translatable(introKey),
                stacks,
                parent, x, y, width, height);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        int baseX = getX();
        int baseY = getY();

        int contentY = baseY;
        if (title != null) {
            DocClientUtils.drawHeaderNoUnderline(title, guiGraphics, baseX, baseY, getWidth(), mouseX, mouseY, partialTick);
            contentY = baseY + TITLE_HEIGHT;
        }

        DocClientUtils.drawParagraph(introText, guiGraphics, baseX, contentY, getWidth(), mouseX, mouseY, partialTick);

        var font = parent.getMinecraft().font;
        int lineCount = font.split(introText, getWidth()).size();
        int paragraphHeight = lineCount * (font.lineHeight + 2);
        int glyphY = contentY + paragraphHeight + INTRO_TO_GLYPHS_GAP;

        if (!stacks.isEmpty()) {
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(baseX, glyphY, 0);
            guiGraphics.pose().scale(GLYPH_SCALE, GLYPH_SCALE, 1f);
            int col = 0;
            for (ItemStack stack : stacks) {
                if (!stack.isEmpty()) {
                    int renderX = col * (SLOT_SIZE + PADDING);
                    int renderY = 0;
                    int scaledMouseX = (int) ((mouseX - baseX) / GLYPH_SCALE);
                    int scaledMouseY = (int) ((mouseY - glyphY) / GLYPH_SCALE);
                    setTooltipIfHovered(DocClientUtils.renderItemStack(guiGraphics, renderX, renderY, scaledMouseX, scaledMouseY, stack));
                }
                col++;
            }
            guiGraphics.pose().popPose();
        }
    }

    @Override
    public void gatherTooltips(List<Component> list) {
        if (!tooltipStack.isEmpty()) {
            Minecraft mc = Minecraft.getInstance();
            Item.TooltipContext ctx = mc.level != null ? Item.TooltipContext.of(mc.level) : Item.TooltipContext.EMPTY;
            list.addAll(GlyphDocTooltipHelper.getTooltipLines(tooltipStack, ctx, mc.player, TooltipFlag.Default.ADVANCED));
        }
    }

    @Override
    public void gatherTooltips(GuiGraphics stack, int mouseX, int mouseY, List<Component> tooltip) {
        if (!tooltipStack.isEmpty()) {
            Minecraft mc = Minecraft.getInstance();
            Item.TooltipContext ctx = mc.level != null ? Item.TooltipContext.of(mc.level) : Item.TooltipContext.EMPTY;
            tooltip.addAll(GlyphDocTooltipHelper.getTooltipLines(tooltipStack, ctx, mc.player, TooltipFlag.Default.ADVANCED));
        }
    }
}
