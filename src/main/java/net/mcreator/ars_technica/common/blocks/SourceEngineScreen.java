package net.mcreator.ars_technica.common.blocks;

import com.simibubi.create.foundation.gui.AbstractSimiScreen;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.utility.Lang;
import net.mcreator.ars_technica.common.gui.AllGuiTextures;
import net.mcreator.ars_technica.common.gui.RenderableScrollInput;
import net.mcreator.ars_technica.setup.NetworkHandler;
import net.minecraft.client.gui.GuiGraphics;

public class SourceEngineScreen extends AbstractSimiScreen {

    private final SourceEngineBlockEntity blockEntity;
    private ScrollInput speedSlider;
    private ScrollInput stressRatioSlider;
    private int lastModification = -1;

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

        speedSlider = new RenderableScrollInput(x + 8, y + 25, 213, 8)
                .withRange(-SourceEngineBlockEntity.MAX_SPEED, SourceEngineBlockEntity.MAX_SPEED + 1)
                .titled(Lang.translateDirect("gui.source_engine.generated_speed"))
                .calling(state -> {
                    blockEntity.setGeneratedSpeed(state);
                    speedSlider.titled(Lang.translateDirect("gui.source_engine.generated_speed", state));
                })
                .setState(blockEntity.generatedSpeed);

        stressRatioSlider = new RenderableScrollInput(x + 8, y + 53, 213, 9)
                .withRange(0, 101)
                .titled(Lang.translateDirect("gui.source_engine.stress_units_ratio"))
                .calling(state -> {
                    blockEntity.setGeneratedStressUnitsRatio(state);
                    stressRatioSlider.titled(Lang.translateDirect("gui.source_engine.stress_units_ratio", state));
                })
                .setState(blockEntity.generatedStressUnitsRatio);
        speedSlider.visible = true;
        speedSlider.active = true;
        addRenderableWidget(speedSlider);
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

        ((RenderableScrollInput)speedSlider).renderSlider(graphics, font, "");
        ((RenderableScrollInput)stressRatioSlider).renderSlider(graphics, font, "%");
    }


    @Override
    public void tick() {
        super.tick();
        if (lastModification >= 0)
            lastModification++;

        if (lastModification >= 20) {
            lastModification = -1;
            send();
        }
    }

    @Override
    public void removed() {
        send();
    }

    protected void send() {
        NetworkHandler.CHANNEL
                .sendToServer(new ConfigureSourceEnginePacket(blockEntity.getBlockPos(), speedSlider.getState(),
                        stressRatioSlider.getState()));
    }

}