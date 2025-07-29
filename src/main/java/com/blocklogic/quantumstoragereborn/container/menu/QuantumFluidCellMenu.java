package com.blocklogic.quantumstoragereborn.container.menu;

import com.blocklogic.quantumstoragereborn.config.Config;
import com.blocklogic.quantumstoragereborn.container.QSRMenuTypes;
import com.blocklogic.quantumstoragereborn.entity.custom.QuantumFluidCellBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.items.SlotItemHandler;

public class QuantumFluidCellMenu extends AbstractContainerMenu {
    private final QuantumFluidCellBlockEntity blockEntity;
    private final Level level;

    public QuantumFluidCellMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public QuantumFluidCellMenu(int containerId, Inventory inv, BlockEntity blockEntity) {
        super(QSRMenuTypes.QUANTUM_FLUID_CELL_MENU.get(), containerId);
        this.blockEntity = (QuantumFluidCellBlockEntity) blockEntity;
        this.level = inv.player.level();

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        // Input slot at x8, y8
        this.addSlot(new SlotItemHandler(this.blockEntity.inventory, 0, 8, 8));

        // Output slot at x152, y8
        this.addSlot(new SlotItemHandler(this.blockEntity.inventory, 1, 152, 8) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false; // Output slot - no placing items
            }
        });
    }

    public void toggleLock() {
        if (level.isClientSide()) return;

        Player player = level.getNearestPlayer(blockEntity.getBlockPos().getX(),
                blockEntity.getBlockPos().getY(), blockEntity.getBlockPos().getZ(), 10, false);

        if (player == null) return;

        blockEntity.toggleLock(player);
        this.broadcastChanges();
    }

    public Component getStoredFluidText() {
        QuantumFluidCellBlockEntity.CellContents contents = blockEntity.getContents();
        if (contents.storedFluidId().isEmpty()) {
            return Component.translatable("gui.quantumstoragereborn.fluid_cell.no_fluid");
        }

        ResourceLocation fluidId = contents.storedFluidId().get();
        Fluid fluid = BuiltInRegistries.FLUID.get(fluidId);
        return Component.translatable("gui.quantumstoragereborn.fluid_cell.stored_fluid",
                Component.translatable(fluid.getFluidType().getDescriptionId()));
    }

    public Component getAmountText() {
        QuantumFluidCellBlockEntity.CellContents contents = blockEntity.getContents();
        if (contents.amount() <= 0) {
            return Component.translatable("gui.quantumstoragereborn.fluid_cell.empty");
        }

        return Component.translatable("gui.quantumstoragereborn.fluid_cell.amount",
                String.format("%,d", contents.amount()));
    }

    public Component getCapacityText() {
        QuantumFluidCellBlockEntity.CellContents contents = blockEntity.getContents();
        float percentage = blockEntity.getCapacityPercentage() * 100f;

        return Component.translatable("gui.quantumstoragereborn.fluid_cell.capacity_percent",
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

        // Output slot
        if (index == 37) {
            if (!moveItemStackTo(sourceStack, 0, 36, true)) {
                return ItemStack.EMPTY;
            }
        }
        // Input slot
        else if (index == 36) {
            if (!moveItemStackTo(sourceStack, 0, 36, true)) {
                return ItemStack.EMPTY;
            }
        }
        // Player inventory/hotbar to input slot
        else {
            if (!moveItemStackTo(sourceStack, 36, 37, false)) {
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