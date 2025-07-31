package com.blocklogic.quantumstoragereborn.datagen.custom;

import com.blocklogic.quantumstoragereborn.QuantumStorageReborn;
import com.blocklogic.quantumstoragereborn.item.QSRItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class QSRItemModeProvider extends ItemModelProvider {
    public QSRItemModeProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, QuantumStorageReborn.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(QSRItems.IRON_CRATE_UPGRADE.get());
        basicItem(QSRItems.GOLD_CRATE_UPGRADE.get());
        basicItem(QSRItems.DIAMOND_CRATE_UPGRADE.get());
        basicItem(QSRItems.NETHERITE_CRATE_UPGRADE.get());

        basicItem(QSRItems.COPPER_BACKPACK.get());
        basicItem(QSRItems.IRON_BACKPACK.get());
        basicItem(QSRItems.GOLD_BACKPACK.get());
        basicItem(QSRItems.DIAMOND_BACKPACK.get());
        basicItem(QSRItems.NETHERITE_BACKPACK.get());
    }
}
