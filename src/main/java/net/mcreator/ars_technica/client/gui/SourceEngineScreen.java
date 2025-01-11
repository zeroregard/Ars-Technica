package net.mcreator.ars_technica.client.gui;

import com.simibubi.create.foundation.gui.AbstractSimiScreen;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.utility.Lang;
import net.mcreator.ars_technica.common.packets.ConfigureSourceEnginePacket;
import net.mcreator.ars_technica.common.blocks.SourceEngineBlockEntity;
import net.mcreator.ars_technica.common.gui.AllGuiTextures;
import net.mcreator.ars_technica.common.gui.RenderableScrollInput;
import net.mcreator.ars_technica.setup.NetworkHandler;
import net.minecraft.client.gui.GuiGraphics;

public class SourceEngineScreen extends AbstractSimiScreen {

    private final SourceEngineBlockEntity blockEntity;
    private ScrollInput stressRatioSlider;

    private final AllGuiTextures background = AllGuiTextures.SOURCE_MOTOR_SCREEN;

    public SourceEngineScreen(SourceEngineBlockEntity be) {
        super(Lang.translateDirect("gui.source_engine.title"));
        this.blockEntity = be;
    }

    @Override
    protected void init() {
        setWindowSize(background.width, background.height);
        setWindowOffset(15, 0);
        super.init();

        int x = guiLeft;
        int y = guiTop;

        stressRatioSlider = new RenderableScrollInput(x + 8, y + 39, 213, 9)
                .withRange(0, 101)
                .titled(Lang.translateDirect("gui.source_engine.stress_units_ratio"))
                .calling(state -> {
                    blockEntity.setGeneratedStressUnitsRatio(state);
                    stressRatioSlider.titled(Lang.translateDirect("gui.source_engine.stress_units_ratio", state));
                })
                .setState(blockEntity.generatedStressUnitsRatio);

        addRenderableWidget(stressRatioSlider);

        var confirmButton = new IconButton(x + 202, y + 75, AllIcons.I_CONFIRM);
        confirmButton.withCallback(() -> {
            onClose();
        });
        addRenderableWidget(confirmButton);
    }

    @Override
    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        int x = guiLeft;
        int y = guiTop;

        background.render(graphics, x, y);

        graphics.drawString(font, title, x + (background.width - 8) / 2 - font.width(title) / 2, y + 4, 0x592424, false);

        ((RenderableScrollInput)stressRatioSlider).renderSlider(String.valueOf(stressRatioSlider.getState()),  graphics, font, "%");
    }

    @Override
    public void removed() {
        send();
    }

    protected void send() {
        NetworkHandler.CHANNEL
                .sendToServer(new ConfigureSourceEnginePacket(blockEntity.getBlockPos(), stressRatioSlider.getState()));
    }

}