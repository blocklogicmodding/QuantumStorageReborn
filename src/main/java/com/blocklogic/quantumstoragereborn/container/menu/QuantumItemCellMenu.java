package com.blocklogic.quantumstoragereborn.container.menu;

import com.blocklogic.quantumstoragereborn.config.Config;
import com.blocklogic.quantumstoragereborn.container.QSRMenuTypes;
import com.blocklogic.quantumstoragereborn.entity.custom.QuantumItemCellBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class QuantumItemCellMenu extends AbstractContainerMenu {
    private final QuantumItemCellBlockEntity blockEntity;
    private final Level level;
    private final ItemStackHandler inputInventory;

    public QuantumItemCellMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public QuantumItemCellMenu(int containerId, Inventory inv, BlockEntity blockEntity) {
        super(QSRMenuTypes.QUANTUM_ITEM_CELL_MENU.get(), containerId);
        this.blockEntity = (QuantumItemCellBlockEntity) blockEntity;
        this.level = inv.player.level();

        this.inputInventory = new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                processInputSlot(inv.player);
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return QuantumItemCellMenu.this.blockEntity.canStoreItem(stack);
            }
        };

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        // Input slot at x8, y8 (relative to GUI)
        this.addSlot(new SlotItemHandler(this.inputInventory, 0, 8, 8) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return QuantumItemCellMenu.this.blockEntity.canStoreItem(stack);
            }

            @Override
            public void setChanged() {
                super.setChanged();
                processInputSlot(inv.player);
            }
        });
    }

    private void processInputSlot(Player player) {
        if (level.isClientSide()) return;

        ItemStack inputStack = inputInventory.getStackInSlot(0);
        if (inputStack.isEmpty()) return;

        int stored = blockEntity.storeItems(inputStack, player);
        if (stored > 0) {
            inputStack.shrink(stored);
            inputInventory.setStackInSlot(0, inputStack);
        }
    }

    public void extractItems() {
        if (level.isClientSide()) return;

        Player player = level.getNearestPlayer(blockEntity.getBlockPos().getX(),
                blockEntity.getBlockPos().getY(), blockEntity.getBlockPos().getZ(), 10, false);

        if (player == null) return;

        ItemStack extracted = blockEntity.extractItems(64, player);
        if (!extracted.isEmpty()) {
            if (!player.getInventory().add(extracted)) {
                player.drop(extracted, false);
            }
        }

        this.broadcastChanges();
    }

    public void toggleLock() {
        if (level.isClientSide()) return;

        Player player = level.getNearestPlayer(blockEntity.getBlockPos().getX(),
                blockEntity.getBlockPos().getY(), blockEntity.getBlockPos().getZ(), 10, false);

        if (player == null) return;

        blockEntity.toggleLock(player);
        this.broadcastChanges();
    }

    public Component getStoredItemText() {
        QuantumItemCellBlockEntity.CellContents contents = blockEntity.getContents();
        if (contents.storedItemId().isEmpty()) {
            return Component.translatable("gui.quantumstoragereborn.cell.no_item");
        }

        ResourceLocation itemId = contents.storedItemId().get();
        Item item = BuiltInRegistries.ITEM.get(itemId);
        return Component.translatable("gui.quantumstoragereborn.cell.stored_item", item.getDescription());
    }

    public Component getCountText() {
        QuantumItemCellBlockEntity.CellContents contents = blockEntity.getContents();
        if (contents.count() <= 0) {
            return Component.translatable("gui.quantumstoragereborn.cell.empty");
        }

        return Component.translatable("gui.quantumstoragereborn.cell.count",
                String.format("%,d", contents.count()));
    }

    public Component getCapacityText() {
        QuantumItemCellBlockEntity.CellContents contents = blockEntity.getContents();
        float percentage = blockEntity.getCapacityPercentage() * 100f;

        return Component.translatable("gui.quantumstoragereborn.cell.capacity_percent",
                String.format("%.1f", percentage));
    }

    public float getCapacityPercentage() {
        return blockEntity.getCapacityPercentage();
    }

    public boolean isLocked() {
        return blockEntity.isLocked();
    }

    public boolean isEmpty() {
        return blockEntity.isEmpty();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        if (index == 36) {
            if (!moveItemStackTo(sourceStack, 0, 36, true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (blockEntity.canStoreItem(sourceStack)) {
                if (!moveItemStackTo(sourceStack, 36, 37, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                return ItemStack.EMPTY;
            }
        }

        if (sourceStack.isEmpty()) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }

        sourceSlot.onTake(player, sourceStack);
        return copyOfSourceStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.blockEntity != null &&
                !this.blockEntity.isRemoved() &&
                player.distanceToSqr(this.blockEntity.getBlockPos().getX() + 0.5D,
                        this.blockEntity.getBlockPos().getY() + 0.5D,
                        this.blockEntity.getBlockPos().getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (!inputInventory.getStackInSlot(0).isEmpty()) {
            player.drop(inputInventory.getStackInSlot(0), false);
        }
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 80 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 139));
        }
    }
}