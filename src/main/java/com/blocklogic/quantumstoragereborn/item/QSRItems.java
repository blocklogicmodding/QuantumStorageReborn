package com.blocklogic.quantumstoragereborn.item;

import com.blocklogic.quantumstoragereborn.QuantumStorageReborn;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class QSRItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(QuantumStorageReborn.MODID);

    public static final DeferredItem<Item> QUANTUM_WRENCH = ITEMS.register("quantum_wrench",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> GOLD_RANGE_EXTENDER = ITEMS.register("gold_range_extender",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> DIAMOND_RANGE_EXTENDER = ITEMS.register("diamond_range_extender",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> NETHERITE_RANGE_EXTENDER = ITEMS.register("netherite_range_extender",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> IRON_CRATE_UPGRADE = ITEMS.register("iron_crate_upgrade",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> GOLD_CRATE_UPGRADE = ITEMS.register("gold_crate_upgrade",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> DIAMOND_CRATE_UPGRADE = ITEMS.register("diamond_crate_upgrade",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> NETHERITE_CRATE_UPGRADE = ITEMS.register("netherite_crate_upgrade",
            () -> new Item(new Item.Properties()));

    public static void register (IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
