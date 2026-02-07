package com.zeroregard.ars_technica.client.gui.documentation;

import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.arsnouveau.api.documentation.SinglePageCtor;
import com.hollingsworth.arsnouveau.api.documentation.SinglePageWidget;
import com.hollingsworth.arsnouveau.client.gui.documentation.BaseDocScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;


public class ProcessingGlyphsPage extends SinglePageWidget {

    private static final float SCALE = 1.0f;
    private static final int SLOT_SIZE = 16;
    private static final int PADDING = 2;

    private final List<ItemStack> stacks;

    public ProcessingGlyphsPage(List<ItemStack> stacks, BaseDocScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
        this.stacks = stacks != null ? stacks : List.of();
    }

    public static SinglePageCtor create(List<ItemStack> stacks) {
        return (parent, x, y, width, height) -> new ProcessingGlyphsPage(stacks, parent, x, y, width, height);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        if (stacks.isEmpty()) {
            return;
        }
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(getX(), getY(), 0);
        guiGraphics.pose().scale(SCALE, SCALE, 1f);
        int col = 0;
        for (ItemStack stack : stacks) {
            if (!stack.isEmpty()) {
                int renderX = col * (SLOT_SIZE + PADDING);
                int renderY = 0;
                int scaledMouseX = (int) ((mouseX - getX()) / SCALE);
                int scaledMouseY = (int) ((mouseY - getY()) / SCALE);
                setTooltipIfHovered(DocClientUtils.renderItemStack(guiGraphics, renderX, renderY, scaledMouseX, scaledMouseY, stack));
            }
            col++;
        }
        guiGraphics.pose().popPose();
    }

    @Override
    public void gatherTooltips(List<net.minecraft.network.chat.Component> list) {
        if (!tooltipStack.isEmpty()) {
            Minecraft mc = Minecraft.getInstance();
            Item.TooltipContext ctx = mc.level != null ? Item.TooltipContext.of(mc.level) : Item.TooltipContext.EMPTY;
            list.addAll(GlyphDocTooltipHelper.getTooltipLines(tooltipStack, ctx, mc.player, TooltipFlag.Default.ADVANCED));
        }
    }

    @Override
    public void gatherTooltips(GuiGraphics stack, int mouseX, int mouseY, List<net.minecraft.network.chat.Component> tooltip) {
        if (!tooltipStack.isEmpty()) {
            Minecraft mc = Minecraft.getInstance();
            Item.TooltipContext ctx = mc.level != null ? Item.TooltipContext.of(mc.level) : Item.TooltipContext.EMPTY;
            tooltip.addAll(GlyphDocTooltipHelper.getTooltipLines(tooltipStack, ctx, mc.player, TooltipFlag.Default.ADVANCED));
        }
    }
}
