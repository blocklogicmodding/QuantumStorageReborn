package com.blocklogic.quantumstoragereborn.datagen.custom;

import com.blocklogic.quantumstoragereborn.block.QSRBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;

import java.util.Set;

public class QSRLootTableProvider extends BlockLootSubProvider {
    public QSRLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropSelf(QSRBlocks.QUANTUM_ITEM_CELL.get());
        dropSelf(QSRBlocks.QUANTUM_FLUID_CELL.get());

        dropSelf(QSRBlocks.COPPER_CRATE.get());
        dropSelf(QSRBlocks.IRON_CRATE.get());
        dropSelf(QSRBlocks.GOLD_CRATE.get());
        dropSelf(QSRBlocks.DIAMOND_CRATE.get());
        dropSelf(QSRBlocks.NETHERITE_CRATE.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return QSRBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}
