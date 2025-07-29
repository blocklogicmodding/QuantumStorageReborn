package com.blocklogic.quantumstoragereborn.container.screen;

import com.blocklogic.quantumstoragereborn.QuantumStorageReborn;
import com.blocklogic.quantumstoragereborn.container.menu.QuantumFluidCellMenu;
import com.blocklogic.quantumstoragereborn.network.FluidCellActionPacket;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

public class QuantumFluidCellScreen extends AbstractContainerScreen<QuantumFluidCellMenu> {
    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(QuantumStorageReborn.MODID, "textures/gui/quantum_fluid_cell_gui.png");
    private static final ResourceLocation TITLE_BG =
            ResourceLocation.fromNamespaceAndPath(QuantumStorageReborn.MODID, "textures/gui/menu_title_background.png");

    private static final int LOCK_BUTTON_X = 155;
    private static final int LOCK_BUTTON_Y = 67;
    private static final int LOCK_BUTTON_SIZE = 10;

    private static final int INFO_PANEL_X = 10;
    private static final int INFO_PANEL_Y = 33;
    private static final int INFO_PANEL_WIDTH = 156;
    private static final int INFO_PANEL_HEIGHT = 28;

    private static final int CAPACITY_BAR_X = 31;
    private static final int CAPACITY_BAR_Y = 8;
    private static final int CAPACITY_BAR_WIDTH = 114;
    private static final int CAPACITY_BAR_HEIGHT = 16;

    public QuantumFluidCellScreen(QuantumFluidCellMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageHeight = 165;
        this.inventoryLabelY = this.imageHeight - 96;
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
        this.titleLabelY = -16;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(GUI_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        // Menu Title BG: x0, y-21 | Size: 176x19 | Blit from: x0, y0
        guiGraphics.blit(TITLE_BG, x, y - 21, 0, 0, 176, 19);

        boolean lockHover = isMouseOverButton(mouseX, mouseY,
                x + LOCK_BUTTON_X, y + LOCK_BUTTON_Y,
                LOCK_BUTTON_SIZE, LOCK_BUTTON_SIZE);
        renderLockButton(guiGraphics, x + LOCK_BUTTON_X, y + LOCK_BUTTON_Y, lockHover);

        renderCapacityBar(guiGraphics, x + CAPACITY_BAR_X, y + CAPACITY_BAR_Y);
    }

    private void renderLockButton(GuiGraphics guiGraphics, int x, int y, boolean hover) {
        boolean locked = menu.isLocked();
        int u = hover ? 186 : 176;
        int v = locked ? 10 : 0;
        guiGraphics.blit(GUI_TEXTURE, x, y, u, v, LOCK_BUTTON_SIZE, LOCK_BUTTON_SIZE);
    }

    private void renderCapacityBar(GuiGraphics guiGraphics, int x, int y) {
        float percentage = menu.getCapacityPercentage();
        int fillWidth = (int) (CAPACITY_BAR_WIDTH * percentage);

        if (fillWidth > 0) {
            // Capacity bar fill: x0, y165 | Size: 114x16
            guiGraphics.blit(GUI_TEXTURE, x, y, 0, 165, fillWidth, CAPACITY_BAR_HEIGHT);
        }
    }

    private boolean isMouseOverButton(double mouseX, double mouseY, int buttonX, int buttonY, int width, int height) {
        return mouseX >= buttonX && mouseX < buttonX + width &&
                mouseY >= buttonY && mouseY < buttonY + height;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        if (isMouseOverButton(mouseX, mouseY,
                x + LOCK_BUTTON_X, y + LOCK_BUTTON_Y,
                LOCK_BUTTON_SIZE, LOCK_BUTTON_SIZE)) {
            if (!menu.isEmpty()) {
                PacketDistributor.sendToServer(new FluidCellActionPacket(FluidCellActionPacket.ActionType.TOGGLE_LOCK));
            }
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);

        renderInfoPanel(guiGraphics);
    }

    private void renderInfoPanel(GuiGraphics guiGraphics) {
        float scale = 0.7f;
        int panelWidth = INFO_PANEL_WIDTH;

        Component storedFluid = menu.getStoredFluidText();
        int storedFluidWidth = (int)(this.font.width(storedFluid) * scale);
        int storedFluidX = INFO_PANEL_X + (panelWidth - storedFluidWidth) / 2;
        int storedFluidY = INFO_PANEL_Y + 2;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(scale, scale, 1.0f);
        guiGraphics.drawString(this.font, storedFluid, (int)(storedFluidX / scale), (int)(storedFluidY / scale), 0x00FFFF, false);
        guiGraphics.pose().popPose();

        Component amount = menu.getAmountText();
        int amountWidth = (int)(this.font.width(amount) * scale);
        int amountX = INFO_PANEL_X + (panelWidth - amountWidth) / 2;
        int amountY = INFO_PANEL_Y + 12;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(scale, scale, 1.0f);
        guiGraphics.drawString(this.font, amount, (int)(amountX / scale), (int)(amountY / scale), 0x00FFFF, false);
        guiGraphics.pose().popPose();

        Component capacity = menu.getCapacityText();
        int capacityWidth = (int)(this.font.width(capacity) * scale);
        int capacityX = INFO_PANEL_X + (panelWidth - capacityWidth) / 2;
        int capacityY = INFO_PANEL_Y + 22;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(scale, scale, 1.0f);
        guiGraphics.drawString(this.font, capacity, (int)(capacityX / scale), (int)(capacityY / scale), 0x00FFFF, false);
        guiGraphics.pose().popPose();
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);

        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        if (isMouseOverButton(mouseX, mouseY,
                x + LOCK_BUTTON_X, y + LOCK_BUTTON_Y,
                LOCK_BUTTON_SIZE, LOCK_BUTTON_SIZE)) {
            Component tooltip = menu.isLocked() ?
                    Component.translatable("gui.quantumstoragereborn.fluid_cell.unlock_tooltip") :
                    Component.translatable("gui.quantumstoragereborn.fluid_cell.lock_tooltip");

            if (menu.isEmpty()) {
                tooltip = Component.translatable("gui.quantumstoragereborn.fluid_cell.lock_disabled_tooltip");
            }

            guiGraphics.renderTooltip(this.font, tooltip, mouseX, mouseY);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}