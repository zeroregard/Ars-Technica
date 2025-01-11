package net.mcreator.ars_technica.common.gui;

import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.UIRenderHelper;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class RenderableScrollInput extends ScrollInput {

    public RenderableScrollInput(int xIn, int yIn, int widthIn, int heightIn) {
        super(xIn, yIn, widthIn, heightIn);
    }

    public void renderSlider(String value, GuiGraphics graphics, Font font, String textAmend) {

        int range = max - min;
        if (range <= 0) return;

        Component cursorText = Component.literal(value + textAmend);
        int cursorWidth = font.width(cursorText) + 3;
        int coordinateX = getX() + (int) ((width - 4) * (state - min) / (float) range);
        int coordinateY = getY() + height / 2;
        int cursorX = ( (coordinateX)) - cursorWidth / 2;
        int cursorY = ( (coordinateY)) - 7;

        AllGuiTextures.VALUE_SETTINGS_CURSOR_LEFT.render(graphics, cursorX - 3, cursorY);
        UIRenderHelper.drawCropped(graphics, cursorX, cursorY, cursorWidth, 14,
                0, AllGuiTextures.VALUE_SETTINGS_CURSOR);
        AllGuiTextures.VALUE_SETTINGS_CURSOR_RIGHT.render(graphics, cursorX + cursorWidth, cursorY);

        graphics.drawString(font, cursorText, cursorX + 2, cursorY + 3, 0x442000, false);
    }

}