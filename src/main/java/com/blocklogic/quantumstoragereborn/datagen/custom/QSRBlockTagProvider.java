package com.blocklogic.quantumstoragereborn.datagen.custom;

import com.blocklogic.quantumstoragereborn.QuantumStorageReborn;
import com.blocklogic.quantumstoragereborn.block.QSRBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class QSRBlockTagProvider extends BlockTagsProvider {
    public QSRBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, QuantumStorageReborn.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(QSRBlocks.QUANTUM_ITEM_CELL.get())
                .add(QSRBlocks.QUANTUM_FLUID_CELL.get())
                .add(QSRBlocks.QUANTUM_CORE.get())
                .add(QSRBlocks.TRASHCAN.get());

        tag(BlockTags.MINEABLE_WITH_AXE)
                .add(QSRBlocks.OAK_COPPER_CRATE.get())
                .add(QSRBlocks.OAK_IRON_CRATE.get())
                .add(QSRBlocks.OAK_GOLD_CRATE.get())
                .add(QSRBlocks.OAK_DIAMOND_CRATE.get())
                .add(QSRBlocks.OAK_NETHERITE_CRATE.get());
    }
}
