package com.blocklogic.quantumstoragereborn.item;

import com.blocklogic.quantumstoragereborn.QuantumStorageReborn;
import com.blocklogic.quantumstoragereborn.block.QSRBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class QSRCreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, QuantumStorageReborn.MODID);

    public static final Supplier<CreativeModeTab> QSR = CREATIVE_MODE_TAB.register("qsr",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(QSRBlocks.QUANTUM_ITEM_CELL.get()))
                    .title(Component.translatable("creativetab.quantumstoragereborn"))
                    .displayItems((ItemDisplayParameters, output) -> {
                        output.accept(QSRBlocks.QUANTUM_CORE);
                        output.accept(QSRBlocks.QUANTUM_ITEM_CELL);
                        output.accept(QSRBlocks.QUANTUM_FLUID_CELL);

                        output.accept(QSRBlocks.COPPER_CRATE);
                        output.accept(QSRBlocks.IRON_CRATE);
                        output.accept(QSRBlocks.GOLD_CRATE);
                        output.accept(QSRBlocks.DIAMOND_CRATE);
                        output.accept(QSRBlocks.NETHERITE_CRATE);

                        output.accept(QSRBlocks.TRASHCAN);

                        output.accept(QSRItems.QUANTUM_WRENCH);
                        output.accept(QSRItems.GOLD_RANGE_EXTENDER);
                        output.accept(QSRItems.DIAMOND_RANGE_EXTENDER);
                        output.accept(QSRItems.NETHERITE_RANGE_EXTENDER);

                        output.accept(QSRItems.IRON_CRATE_UPGRADE);
                        output.accept(QSRItems.GOLD_CRATE_UPGRADE);
                        output.accept(QSRItems.DIAMOND_CRATE_UPGRADE);
                        output.accept(QSRItems.NETHERITE_CRATE_UPGRADE);
                    }).build());

    public static void register (IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
