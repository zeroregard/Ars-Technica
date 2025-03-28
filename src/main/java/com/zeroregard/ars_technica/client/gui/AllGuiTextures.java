package com.zeroregard.ars_technica.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.zeroregard.ars_technica.ArsTechnica;
import net.createmod.catnip.gui.UIRenderHelper;
import net.createmod.catnip.gui.element.ScreenElement;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import static com.zeroregard.ars_technica.ArsTechnica.prefix;

public enum AllGuiTextures implements ScreenElement {
    SOURCE_MOTOR_SCREEN("source_motor_screen",235, 99);

    public static final int FONT_COLOR = 0x575F7A;

    public final ResourceLocation location;
    public int width, height;
    public int startX, startY;

    private AllGuiTextures(String location, int width, int height) {
        this(location, 0, 0, width, height);
    }

    private AllGuiTextures(int startX, int startY) {
        this("icons", startX * 16, startY * 16, 16, 16);
    }

    private AllGuiTextures(String location, int startX, int startY, int width, int height) {
        this(ArsTechnica.MODID, location, startX, startY, width, height);
    }

    private AllGuiTextures(String namespace, String location, int startX, int startY, int width, int height) {
        this.location = prefix("textures/gui/" + location + ".png");
        this.width = width;
        this.height = height;
        this.startX = startX;
        this.startY = startY;
    }

    @OnlyIn(Dist.CLIENT)
    public void bind() {
        RenderSystem.setShaderTexture(0, location);
    }

    @OnlyIn(Dist.CLIENT)
    public void render(GuiGraphics graphics, int x, int y) {
        graphics.blit(location, x, y, startX, startY, width, height);
    }

    @OnlyIn(Dist.CLIENT)
    public void render(GuiGraphics graphics, int x, int y, Color c) {
        bind();
        UIRenderHelper.drawColoredTexture(graphics, c, x, y, startX, startY, width, height);
    }
}