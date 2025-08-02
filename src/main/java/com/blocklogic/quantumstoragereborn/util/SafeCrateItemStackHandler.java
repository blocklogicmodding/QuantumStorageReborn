package com.blocklogic.quantumstoragereborn.util;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.ItemStackHandler;

/**
 * Thread-safe ItemStackHandler for crates that prevents race conditions
 * during rapid inventory operations.
 */
public class SafeCrateItemStackHandler extends ItemStackHandler {

    private final BlockEntity blockEntity;
    private volatile boolean isUpdating = false;

    public SafeCrateItemStackHandler(int size, BlockEntity blockEntity) {
        super(size);
        this.blockEntity = blockEntity;
    }

    @Override
    protected void onContentsChanged(int slot) {
        // Prevent recursive calls and race conditions
        if (isUpdating || blockEntity == null || blockEntity.getLevel() == null) {
            return;
        }

        try {
            isUpdating = true;
            blockEntity.setChanged();

            // Only sync to clients if we're on the server side
            if (!blockEntity.getLevel().isClientSide()) {
                // Use flag 2 (UPDATE_CLIENTS) instead of 3 to reduce packet spam
                blockEntity.getLevel().sendBlockUpdated(
                        blockEntity.getBlockPos(),
                        blockEntity.getBlockState(),
                        blockEntity.getBlockState(),
                        2
                );
            }
        } finally {
            isUpdating = false;
        }
    }

    /**
     * Gets whether this handler is currently updating to prevent race conditions
     */
    public boolean isUpdating() {
        return isUpdating;
    }

    /**
     * Temporarily disable updates during bulk operations
     */
    public void setUpdating(boolean updating) {
        this.isUpdating = updating;
    }
}