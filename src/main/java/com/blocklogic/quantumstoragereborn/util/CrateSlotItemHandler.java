package com.blocklogic.quantumstoragereborn.util;

import com.blocklogic.quantumstoragereborn.util.CrateStorageValidator;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

/**
 * Custom slot for crates that prevents crates and quantum cells from being placed inside.
 */
public class CrateSlotItemHandler extends SlotItemHandler {

    public CrateSlotItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        if (!super.mayPlace(stack)) {
            return false;
        }

        return CrateStorageValidator.canStoreInCrate(stack);
    }
}
