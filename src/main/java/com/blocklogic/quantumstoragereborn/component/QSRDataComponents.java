package com.blocklogic.quantumstoragereborn.component;

import com.blocklogic.quantumstoragereborn.QuantumStorageReborn;
import com.blocklogic.quantumstoragereborn.entity.custom.QuantumItemCellBlockEntity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.function.Supplier;

public class QSRDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, QuantumStorageReborn.MODID);

    public static final Supplier<DataComponentType<InventoryData>> INVENTORY_DATA =
            DATA_COMPONENTS.register("inventory_data", () -> DataComponentType.<InventoryData>builder()
                    .persistent(InventoryData.CODEC)
                    .build());

    public static final Supplier<DataComponentType<QuantumItemCellBlockEntity.CellContents>> CELL_CONTENTS =
            DATA_COMPONENTS.register("cell_contents",
                    () -> DataComponentType.<QuantumItemCellBlockEntity.CellContents>builder()
                            .persistent(QuantumItemCellBlockEntity.CELL_CONTENTS_CODEC)
                            .build());

    public static void register(IEventBus eventBus) {
        DATA_COMPONENTS.register(eventBus);
    }

    public record InventoryData(List<ItemStack> items) {
        public static final Codec<InventoryData> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        ItemStack.OPTIONAL_CODEC.listOf().fieldOf("items").forGetter(InventoryData::items)
                ).apply(instance, InventoryData::new));

        public static InventoryData create(List<ItemStack> items) {
            return new InventoryData(items);
        }

        public boolean isEmpty() {
            return items.isEmpty() || items.stream().allMatch(ItemStack::isEmpty);
        }
    }
}