package com.blocklogic.quantumstoragereborn.container.screen;

import com.blocklogic.quantumstoragereborn.QuantumStorageReborn;
import com.blocklogic.quantumstoragereborn.container.menu.QuantumItemCellMenu;
import com.blocklogic.quantumstoragereborn.network.CellActionPacket;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

public class QuantumItemCellScreen extends AbstractContainerScreen<QuantumItemCellMenu> {
    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(QuantumStorageReborn.MODID, "textures/gui/quantum_item_cell_gui.png");
    private static final ResourceLocation TITLE_BG =
            ResourceLocation.fromNamespaceAndPath(QuantumStorageReborn.MODID, "textures/gui/menu_title_background.png");

    private static final int EXTRACT_BUTTON_X = 151;
    private static final int EXTRACT_BUTTON_Y = 7;
    private static final int EXTRACT_BUTTON_SIZE = 18;

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

    public QuantumItemCellScreen(QuantumItemCellMenu menu, Inventory playerInventory, Component title) {
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

        guiGraphics.blit(TITLE_BG, x, y - 21, 0, 0, 176, 19);

        boolean extractHover = isMouseOverButton(mouseX, mouseY,
                x + EXTRACT_BUTTON_X, y + EXTRACT_BUTTON_Y,
                EXTRACT_BUTTON_SIZE, EXTRACT_BUTTON_SIZE);
        renderExtractButton(guiGraphics, x + EXTRACT_BUTTON_X, y + EXTRACT_BUTTON_Y, extractHover);

        boolean lockHover = isMouseOverButton(mouseX, mouseY,
                x + LOCK_BUTTON_X, y + LOCK_BUTTON_Y,
                LOCK_BUTTON_SIZE, LOCK_BUTTON_SIZE);
        renderLockButton(guiGraphics, x + LOCK_BUTTON_X, y + LOCK_BUTTON_Y, lockHover);

        renderCapacityBar(guiGraphics, x + CAPACITY_BAR_X, y + CAPACITY_BAR_Y);
    }

    private void renderExtractButton(GuiGraphics guiGraphics, int x, int y, boolean hover) {
        int u = hover ? 194 : 176;
        int v = 20;
        guiGraphics.blit(GUI_TEXTURE, x, y, u, v, EXTRACT_BUTTON_SIZE, EXTRACT_BUTTON_SIZE);
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
                x + EXTRACT_BUTTON_X, y + EXTRACT_BUTTON_Y,
                EXTRACT_BUTTON_SIZE, EXTRACT_BUTTON_SIZE)) {
            PacketDistributor.sendToServer(new CellActionPacket(CellActionPacket.ActionType.EXTRACT));
            return true;
        }

        if (isMouseOverButton(mouseX, mouseY,
                x + LOCK_BUTTON_X, y + LOCK_BUTTON_Y,
                LOCK_BUTTON_SIZE, LOCK_BUTTON_SIZE)) {
            if (!menu.isEmpty()) {
                PacketDistributor.sendToServer(new CellActionPacket(CellActionPacket.ActionType.TOGGLE_LOCK));
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

        Component storedItem = menu.getStoredItemText();
        int storedItemWidth = (int)(this.font.width(storedItem) * scale);
        int storedItemX = INFO_PANEL_X + (panelWidth - storedItemWidth) / 2;
        int storedItemY = INFO_PANEL_Y + 2;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(scale, scale, 1.0f);
        guiGraphics.drawString(this.font, storedItem, (int)(storedItemX / scale), (int)(storedItemY / scale), 0x00FF00, false);
        guiGraphics.pose().popPose();

        Component count = menu.getCountText();
        int countWidth = (int)(this.font.width(count) * scale);
        int countX = INFO_PANEL_X + (panelWidth - countWidth) / 2;
        int countY = INFO_PANEL_Y + 12;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(scale, scale, 1.0f);
        guiGraphics.drawString(this.font, count, (int)(countX / scale), (int)(countY / scale), 0x00FF00, false);
        guiGraphics.pose().popPose();

        Component capacity = menu.getCapacityText();
        int capacityWidth = (int)(this.font.width(capacity) * scale);
        int capacityX = INFO_PANEL_X + (panelWidth - capacityWidth) / 2;
        int capacityY = INFO_PANEL_Y + 22;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(scale, scale, 1.0f);
        guiGraphics.drawString(this.font, capacity, (int)(capacityX / scale), (int)(capacityY / scale), 0x00FF00, false);
        guiGraphics.pose().popPose();
    }


    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);

        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        if (isMouseOverButton(mouseX, mouseY,
                x + EXTRACT_BUTTON_X, y + EXTRACT_BUTTON_Y,
                EXTRACT_BUTTON_SIZE, EXTRACT_BUTTON_SIZE)) {
            Component tooltip = Component.translatable("gui.quantumstoragereborn.cell.extract_tooltip");
            guiGraphics.renderTooltip(this.font, tooltip, mouseX, mouseY);
        }

        if (isMouseOverButton(mouseX, mouseY,
                x + LOCK_BUTTON_X, y + LOCK_BUTTON_Y,
                LOCK_BUTTON_SIZE, LOCK_BUTTON_SIZE)) {
            Component tooltip = menu.isLocked() ?
                    Component.translatable("gui.quantumstoragereborn.cell.unlock_tooltip") :
                    Component.translatable("gui.quantumstoragereborn.cell.lock_tooltip");

            if (menu.isEmpty()) {
                tooltip = Component.translatable("gui.quantumstoragereborn.cell.lock_disabled_tooltip");
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