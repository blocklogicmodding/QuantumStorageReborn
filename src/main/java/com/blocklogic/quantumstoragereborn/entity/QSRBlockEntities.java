package com.blocklogic.quantumstoragereborn.entity;

import com.blocklogic.quantumstoragereborn.QuantumStorageReborn;
import com.blocklogic.quantumstoragereborn.block.QSRBlocks;
import com.blocklogic.quantumstoragereborn.entity.custom.CopperCrateBlockEntity;
import com.blocklogic.quantumstoragereborn.entity.custom.DiamondCrateBlockEntity;
import com.blocklogic.quantumstoragereborn.entity.custom.GoldCrateBlockEntity;
import com.blocklogic.quantumstoragereborn.entity.custom.IronCrateBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class QSRBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, QuantumStorageReborn.MODID);

    public static final Supplier<BlockEntityType<CopperCrateBlockEntity>> COPPER_CRATE_BE =
            BLOCK_ENTITIES.register("copper_crate_be", () -> BlockEntityType.Builder.of(
                    CopperCrateBlockEntity::new, QSRBlocks.COPPER_CRATE.get()).build(null));

    public static final Supplier<BlockEntityType<IronCrateBlockEntity>> IRON_CRATE_BE =
            BLOCK_ENTITIES.register("iron_crate_be", () -> BlockEntityType.Builder.of(
                    IronCrateBlockEntity::new, QSRBlocks.IRON_CRATE.get()).build(null));

    public static final Supplier<BlockEntityType<GoldCrateBlockEntity>> GOLD_CRATE_BE =
            BLOCK_ENTITIES.register("gold_crate_be", () -> BlockEntityType.Builder.of(
                    GoldCrateBlockEntity::new, QSRBlocks.GOLD_CRATE.get()).build(null));

    public static final Supplier<BlockEntityType<DiamondCrateBlockEntity>> DIAMOND_CRATE_BE =
            BLOCK_ENTITIES.register("diamond_crate_be", () -> BlockEntityType.Builder.of(
                    DiamondCrateBlockEntity::new, QSRBlocks.DIAMOND_CRATE.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
