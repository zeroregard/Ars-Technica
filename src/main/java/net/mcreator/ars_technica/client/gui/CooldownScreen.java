package net.mcreator.ars_technica.client.gui;

import com.simibubi.create.foundation.gui.AbstractSimiScreen;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.utility.Lang;
import net.mcreator.ars_technica.common.gui.AllGuiTextures;
import net.mcreator.ars_technica.common.gui.RenderableScrollInput;
import net.mcreator.ars_technica.common.helpers.CooldownHelper;
import net.minecraft.client.gui.GuiGraphics;

public abstract class CooldownScreen<T> extends AbstractSimiScreen {

    private ScrollInput slider;
    protected T blockEntity;
    protected int min;
    protected int max;

    private final AllGuiTextures background = AllGuiTextures.SOURCE_MOTOR_SCREEN;

    public CooldownScreen(String titleKey, T blockEntity, int min, int max) {
        super(Lang.translateDirect(titleKey));
        this.blockEntity = blockEntity;
        this.min = min;
        this.max = max;
    }

    @Override
    protected void init() {
        setWindowSize(background.width, background.height);
        setWindowOffset(15, 0);
        super.init();

        int x = guiLeft;
        int y = guiTop;

        slider = new RenderableScrollInput(x + 8, y + 39, 213, 9)
                .withRange(min, max + 1)
                .titled(Lang.translateDirect("gui.ars_technica.cooldown"))
                .calling(state -> {
                    updateEntity(state);
                    slider.titled(Lang.translateDirect("gui.ars_technica.cooldown", state));
                })
                .setState(getInitialEntityStateValue());

        addRenderableWidget(slider);

        var confirmButton = new IconButton(x + 202, y + 75, AllIcons.I_CONFIRM);
        confirmButton.withCallback(() -> {
            onClose();
        });
        addRenderableWidget(confirmButton);
    }

    protected abstract void updateEntity(Integer sliderValue);

    protected abstract int getInitialEntityStateValue();

    @Override
    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        int x = guiLeft;
        int y = guiTop;

        background.render(graphics, x, y);

        graphics.drawString(font, title, x + (background.width - 8) / 2 - font.width(title) / 2, y + 4, 0x592424, false);

        var stateValue = slider.getState();
        ((RenderableScrollInput)slider).renderSlider(CooldownHelper.getCooldownText(stateValue), graphics, font, "");
    }

    @Override
    public void removed() {
        send(slider.getState());
    }

    protected abstract void send(int value);

}
