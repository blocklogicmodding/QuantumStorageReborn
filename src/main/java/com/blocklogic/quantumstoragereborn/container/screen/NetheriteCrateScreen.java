package com.blocklogic.quantumstoragereborn.container.screen;

import com.blocklogic.quantumstoragereborn.QuantumStorageReborn;
import com.blocklogic.quantumstoragereborn.container.menu.NetheriteCrateMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class NetheriteCrateScreen extends AbstractContainerScreen<NetheriteCrateMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            QuantumStorageReborn.MODID, "textures/gui/netherite_crate_gui.png");
    private static final ResourceLocation TITLE_BG = ResourceLocation.fromNamespaceAndPath(
            QuantumStorageReborn.MODID, "textures/gui/menu_title_background.png");

    public NetheriteCrateScreen(NetheriteCrateMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 338;
        this.imageHeight = 252;
        this.inventoryLabelY = this.imageHeight - 96;
        this.inventoryLabelX = 89;
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

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 338, 256);

        guiGraphics.blit(TITLE_BG, x + 81, y - 21, 0, 0, 176, 19);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}