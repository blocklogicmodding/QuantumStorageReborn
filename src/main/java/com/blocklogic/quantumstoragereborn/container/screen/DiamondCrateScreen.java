package com.blocklogic.quantumstoragereborn.container.screen;

import com.blocklogic.quantumstoragereborn.QuantumStorageReborn;
import com.blocklogic.quantumstoragereborn.container.menu.DiamondCrateMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class DiamondCrateScreen extends AbstractContainerScreen<DiamondCrateMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            QuantumStorageReborn.MODID, "textures/gui/diamond_crate_gui.png");
    private static final ResourceLocation TEXTURE_STITCH = ResourceLocation.fromNamespaceAndPath(
            QuantumStorageReborn.MODID, "textures/gui/diamond_crate_gui_1.png");
    private static final ResourceLocation TITLE_BG = ResourceLocation.fromNamespaceAndPath(
            QuantumStorageReborn.MODID, "textures/gui/menu_title_background.png");

    public DiamondCrateScreen(DiamondCrateMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 285;
        this.imageHeight = 251;
        this.inventoryLabelY = this.imageHeight - 96;
        this.inventoryLabelX = 62;
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
        this.titleLabelY = -16;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        guiGraphics.blit(TITLE_BG, x + 54, y - 21, 0, 0, 176, 19);

        guiGraphics.blit(TEXTURE_STITCH, x + 256, y, 0, 0, 29, 160);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}