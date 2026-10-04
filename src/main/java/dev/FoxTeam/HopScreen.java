package dev.FoxTeam;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class HopScreen extends Screen {
    public HopScreen() {
        super(Component.translatable("foxhop.screen.title"));
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int buttonWidth = 160;
        int buttonHeight = 20;

        this.addRenderableWidget(Button.builder(
                Component.translatable("foxhop.screen.mode_default"),
                button -> {
                    HopMode.setCurrentMode(HopMode.DEFAULT);
                    MovementHandler.sendStatusMessage();
                    this.onClose();
                })
                .pos(centerX - buttonWidth / 2, centerY - 25)
                .size(buttonWidth, buttonHeight)
                .build());

        this.addRenderableWidget(Button.builder(
                Component.translatable("foxhop.screen.mode_strafe"),
                button -> {
                    HopMode.setCurrentMode(HopMode.STRAFE);
                    MovementHandler.sendStatusMessage();
                    this.onClose();
                })
                .pos(centerX - buttonWidth / 2, centerY + 5)
                .size(buttonWidth, buttonHeight)
                .build());

        this.addRenderableWidget(Button.builder(
                Component.translatable("foxhop.screen.close"),
                button -> this.onClose())
                .pos(centerX - buttonWidth / 2, centerY + 35)
                .size(buttonWidth, buttonHeight)
                .build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 50, 0xFFFFFF);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
