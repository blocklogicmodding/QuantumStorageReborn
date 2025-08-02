package com.blocklogic.quantumstoragereborn.util;

import com.blocklogic.quantumstoragereborn.block.QSRBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

/**
 * Utility class to validate if items can be stored in crates and quantum cells.
 * Prevents recursive storage (crates in crates, crates in cells, etc.)
 */
public class CrateStorageValidator {

    /**
     * Checks if the given ItemStack represents a crate block that should not be stored in other containers.
     *
     * @param stack The ItemStack to check
     * @return true if the item is a crate and should be blocked from storage, false otherwise
     */
    public static boolean isCrateItem(ItemStack stack) {
        if (stack.isEmpty()) return false;

        if (!(stack.getItem() instanceof BlockItem blockItem)) return false;

        Block block = blockItem.getBlock();

        // Check if the block is any of our crate blocks
        return block == QSRBlocks.COPPER_CRATE.get() ||
                block == QSRBlocks.IRON_CRATE.get() ||
                block == QSRBlocks.GOLD_CRATE.get() ||
                block == QSRBlocks.DIAMOND_CRATE.get() ||
                block == QSRBlocks.NETHERITE_CRATE.get();
    }

    /**
     * Checks if the given ItemStack represents a quantum cell that should not be stored in other containers.
     *
     * @param stack The ItemStack to check
     * @return true if the item is a quantum cell and should be blocked from storage, false otherwise
     */
    public static boolean isQuantumCellItem(ItemStack stack) {
        if (stack.isEmpty()) return false;

        if (!(stack.getItem() instanceof BlockItem blockItem)) return false;

        Block block = blockItem.getBlock();

        // Check if the block is any of our quantum cell blocks
        return block == QSRBlocks.QUANTUM_ITEM_CELL.get() ||
                block == QSRBlocks.QUANTUM_FLUID_CELL.get();
    }

    /**
     * Checks if an ItemStack can be stored in a crate.
     * Crates cannot store other crates or quantum cells.
     *
     * @param stack The ItemStack to check
     * @return true if the item can be stored in a crate, false otherwise
     */
    public static boolean canStoreInCrate(ItemStack stack) {
        if (stack.isEmpty()) return true;

        // Block crates and quantum cells from being stored in crates
        return !isCrateItem(stack) && !isQuantumCellItem(stack);
    }

    /**
     * Checks if an ItemStack can be stored in a quantum item cell.
     * Quantum cells cannot store crates or other quantum cells.
     *
     * @param stack The ItemStack to check
     * @return true if the item can be stored in a quantum cell, false otherwise
     */
    public static boolean canStoreInQuantumCell(ItemStack stack) {
        if (stack.isEmpty()) return true;

        // Block crates and quantum cells from being stored in quantum cells
        return !isCrateItem(stack) && !isQuantumCellItem(stack);
    }
}