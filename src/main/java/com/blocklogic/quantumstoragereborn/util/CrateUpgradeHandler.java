package com.blocklogic.quantumstoragereborn.util;

import com.blocklogic.quantumstoragereborn.block.QSRBlocks;
import com.blocklogic.quantumstoragereborn.entity.custom.*;
import com.blocklogic.quantumstoragereborn.item.QSRItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.List;

public class CrateUpgradeHandler {

    public static boolean canUpgrade(ItemStack upgradeItem, Block currentBlock) {
        if (upgradeItem.getItem() == QSRItems.IRON_CRATE_UPGRADE.get()) {
            return currentBlock == QSRBlocks.COPPER_CRATE.get();
        } else if (upgradeItem.getItem() == QSRItems.GOLD_CRATE_UPGRADE.get()) {
            return currentBlock == QSRBlocks.IRON_CRATE.get();
        } else if (upgradeItem.getItem() == QSRItems.DIAMOND_CRATE_UPGRADE.get()) {
            return currentBlock == QSRBlocks.GOLD_CRATE.get();
        } else if (upgradeItem.getItem() == QSRItems.NETHERITE_CRATE_UPGRADE.get()) {
            return currentBlock == QSRBlocks.DIAMOND_CRATE.get();
        }
        return false;
    }

    public static Block getUpgradeTarget(ItemStack upgradeItem) {
        if (upgradeItem.getItem() == QSRItems.IRON_CRATE_UPGRADE.get()) {
            return QSRBlocks.IRON_CRATE.get();
        } else if (upgradeItem.getItem() == QSRItems.GOLD_CRATE_UPGRADE.get()) {
            return QSRBlocks.GOLD_CRATE.get();
        } else if (upgradeItem.getItem() == QSRItems.DIAMOND_CRATE_UPGRADE.get()) {
            return QSRBlocks.DIAMOND_CRATE.get();
        } else if (upgradeItem.getItem() == QSRItems.NETHERITE_CRATE_UPGRADE.get()) {
            return QSRBlocks.NETHERITE_CRATE.get();
        }
        return null;
    }

    public static boolean performUpgrade(Level level, BlockPos pos, BlockState currentState, Player player, InteractionHand hand) {
        ItemStack upgradeItem = player.getItemInHand(hand);
        Block currentBlock = currentState.getBlock();

        if (!canUpgrade(upgradeItem, currentBlock)) {
            return false;
        }

        Block targetBlock = getUpgradeTarget(upgradeItem);
        if (targetBlock == null) {
            return false;
        }

        BlockEntity currentEntity = level.getBlockEntity(pos);
        List<ItemStack> inventoryItems = new ArrayList<>();

        if (currentEntity instanceof CopperCrateBlockEntity copperCrate) {
            inventoryItems = extractInventory(copperCrate.inventory);
        } else if (currentEntity instanceof IronCrateBlockEntity ironCrate) {
            inventoryItems = extractInventory(ironCrate.inventory);
        } else if (currentEntity instanceof GoldCrateBlockEntity goldCrate) {
            inventoryItems = extractInventory(goldCrate.inventory);
        } else if (currentEntity instanceof DiamondCrateBlockEntity diamondCrate) {
            inventoryItems = extractInventory(diamondCrate.inventory);
        }

        BlockState newState = targetBlock.defaultBlockState();
        level.setBlock(pos, newState, 3);

        BlockEntity newEntity = level.getBlockEntity(pos);
        if (newEntity != null) {
            restoreInventoryToNewCrate(newEntity, inventoryItems);
        }

        if (!player.isCreative()) {
            upgradeItem.shrink(1);
        }

        level.playSound(null, pos, SoundEvents.VILLAGER_WORK_ARMORER, SoundSource.BLOCKS, 0.5F, 0.5F);

        return true;
    }

    private static List<ItemStack> extractInventory(ItemStackHandler inventory) {
        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            items.add(stack.copy());
        }
        return items;
    }

    private static void restoreInventoryToNewCrate(BlockEntity newEntity, List<ItemStack> items) {
        ItemStackHandler newInventory = null;

        if (newEntity instanceof CopperCrateBlockEntity copperCrate) {
            newInventory = copperCrate.inventory;
        } else if (newEntity instanceof IronCrateBlockEntity ironCrate) {
            newInventory = ironCrate.inventory;
        } else if (newEntity instanceof GoldCrateBlockEntity goldCrate) {
            newInventory = goldCrate.inventory;
        } else if (newEntity instanceof DiamondCrateBlockEntity diamondCrate) {
            newInventory = diamondCrate.inventory;
        } else if (newEntity instanceof NetheriteCrateBlockEntity netheriteCrate) {
            newInventory = netheriteCrate.inventory;
        }

        if (newInventory != null) {
            int slotsToFill = Math.min(items.size(), newInventory.getSlots());
            for (int i = 0; i < slotsToFill; i++) {
                newInventory.setStackInSlot(i, items.get(i));
            }
            newEntity.setChanged();
        }
    }
}