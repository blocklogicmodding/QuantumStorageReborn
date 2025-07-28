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

                        output.accept(QSRBlocks.ACACIA_COPPER_CRATE);
                        output.accept(QSRBlocks.ACACIA_IRON_CRATE);
                        output.accept(QSRBlocks.ACACIA_GOLD_CRATE);
                        output.accept(QSRBlocks.ACACIA_DIAMOND_CRATE);
                        output.accept(QSRBlocks.ACACIA_NETHERITE_CRATE);

                        output.accept(QSRBlocks.OAK_COPPER_CRATE);
                        output.accept(QSRBlocks.OAK_IRON_CRATE);
                        output.accept(QSRBlocks.OAK_GOLD_CRATE);
                        output.accept(QSRBlocks.OAK_DIAMOND_CRATE);
                        output.accept(QSRBlocks.OAK_NETHERITE_CRATE);

                        output.accept(QSRBlocks.TRASHCAN);

                        output.accept(QSRItems.QUANTUM_WRENCH);
                        output.accept(QSRItems.GOLD_RANGE_EXTENDER);
                        output.accept(QSRItems.DIAMOND_RANGE_EXTENDER);
                        output.accept(QSRItems.NETHERITE_RANGE_EXTENDER);
                    }).build());

    public static void register (IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
