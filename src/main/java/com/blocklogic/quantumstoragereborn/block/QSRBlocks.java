package com.blocklogic.quantumstoragereborn.block;

import com.blocklogic.quantumstoragereborn.QuantumStorageReborn;
import com.blocklogic.quantumstoragereborn.block.custom.*;
import com.blocklogic.quantumstoragereborn.item.QSRItems;
import com.blocklogic.quantumstoragereborn.item.custom.CrateItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class QSRBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(QuantumStorageReborn.MODID);

    public static final DeferredBlock<Block> COPPER_CRATE = registerCrateBlock("copper_crate",
            () -> new CopperCrateBlock(BlockBehaviour.Properties.of()
                    .strength(3.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.WOOD)
                    .noOcclusion()
            ));

    public static final DeferredBlock<Block> IRON_CRATE = registerCrateBlock("iron_crate",
            () -> new IronCrateBlock(BlockBehaviour.Properties.of()
                    .strength(3.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.WOOD)
                    .noOcclusion()
            ));

    public static final DeferredBlock<Block> GOLD_CRATE = registerCrateBlock("gold_crate",
            () -> new GoldCrateBlock(BlockBehaviour.Properties.of()
                    .strength(3.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.WOOD)
                    .noOcclusion()
            ));

    public static final DeferredBlock<Block> DIAMOND_CRATE = registerCrateBlock("diamond_crate",
            () -> new DiamondCrateBlock(BlockBehaviour.Properties.of()
                    .strength(3.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.WOOD)
                    .noOcclusion()
            ));

    public static final DeferredBlock<Block> NETHERITE_CRATE = registerCrateBlock("netherite_crate",
            () -> new NetheriteCrateBlock(BlockBehaviour.Properties.of()
                    .strength(3.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.WOOD)
                    .noOcclusion()
            ));

    public static final DeferredBlock<Block> QUANTUM_ITEM_CELL = registerBlock("quantum_item_cell",
            () -> new QuantumItemCellBlock(BlockBehaviour.Properties.of()
                    .strength(4.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)
                    .noOcclusion()
            ));

    public static final DeferredBlock<Block> QUANTUM_FLUID_CELL = registerBlock("quantum_fluid_cell",
            () -> new QuantumFluidCellBlock(BlockBehaviour.Properties.of()
                    .strength(4.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)
                    .noOcclusion()
            ));

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> DeferredBlock<T> registerCrateBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerCrateBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        QSRItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    private static <T extends Block> void registerCrateBlockItem(String name, DeferredBlock<T> block) {
        QSRItems.ITEMS.register(name, () -> new CrateItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}