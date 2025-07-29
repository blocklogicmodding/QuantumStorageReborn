package com.blocklogic.quantumstoragereborn.config;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public class Config {
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final ModConfigSpec.Builder COMMON_BUILDER = new ModConfigSpec.Builder();

    public static ModConfigSpec COMMON_CONFIG;

    public static ModConfigSpec SPEC;

    // ========================================
    // CATEGORY CONSTANTS
    // ========================================

    public static final String CATEGORY_STORAGE_LIMITS = "storage_limits";
    public static final String CATEGORY_QUANTUM_CORE_RANGES = "quantum_core_ranges";

    // ========================================
    // STORAGE LIMITS CONFIGURATION
    // ========================================

    public static ModConfigSpec.IntValue MAX_QUANTUM_ITEM_CELL_STORAGE;
    public static ModConfigSpec.IntValue MAX_QUANTUM_FLUID_CELL_STORAGE;

    public static void register(ModContainer container) {
        registerCommonConfigs(container);
    }

    private static void registerCommonConfigs(ModContainer container) {
        storageLimitsConfig();
        COMMON_CONFIG = COMMON_BUILDER.build();
        SPEC = COMMON_CONFIG;
        container.registerConfig(ModConfig.Type.COMMON, COMMON_CONFIG);
    }

    // ========================================
    // CONFIGURATION CATEGORY METHODS
    // ========================================

    private static void storageLimitsConfig() {
        COMMON_BUILDER.comment("Storage Limits - Configure maximum storage capacities for quantum cells").push(CATEGORY_STORAGE_LIMITS);

        MAX_QUANTUM_ITEM_CELL_STORAGE = COMMON_BUILDER.comment("Maximum items storable in Quantum Item Cells",
                        "Default: " + Integer.MAX_VALUE + " (unlimited)",
                        "Minimum: 2048 (32 stacks)")
                .defineInRange("max_quantum_item_cell_storage", Integer.MAX_VALUE, 2048, Integer.MAX_VALUE);

        MAX_QUANTUM_FLUID_CELL_STORAGE = COMMON_BUILDER.comment("Maximum fluid storable in Quantum Fluid Cells (millibuckets)",
                        "Default: " + Integer.MAX_VALUE + " (unlimited)",
                        "Minimum: 20480 (20.48 buckets)")
                .defineInRange("max_quantum_fluid_cell_storage", Integer.MAX_VALUE, 20480, Integer.MAX_VALUE);

        COMMON_BUILDER.pop();
    }

    // ========================================
    // GETTER METHODS FOR STORAGE LIMITS
    // ========================================

    public static int getMaxQuantumItemCellStorage() {
        return MAX_QUANTUM_ITEM_CELL_STORAGE.get();
    }

    public static int getMaxQuantumFluidCellStorage() {
        return MAX_QUANTUM_FLUID_CELL_STORAGE.get();
    }


    // ========================================
    // VALIDATION METHODS
    // ========================================

    public static void validateConfig() {
        if (getMaxQuantumItemCellStorage() < 2048) {
            LOGGER.warn("Quantum Item Cell storage limit ({}) is below recommended minimum of 2048 items", getMaxQuantumItemCellStorage());
        }

        if (getMaxQuantumFluidCellStorage() < 20480) {
            LOGGER.warn("Quantum Fluid Cell storage limit ({}) is below recommended minimum of 20480mb", getMaxQuantumFluidCellStorage());
        }
    }

    public static void loadConfig() {
        LOGGER.info("Quantum Storage Reborn configs reloaded");
        validateConfig();
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        LOGGER.info("Quantum Storage Reborn configuration loaded");
        logConfigValues();
        validateConfig();
    }

    private static void logConfigValues() {
        LOGGER.info("Storage Limits Configuration:");
        LOGGER.info("  Max Quantum Item Cell Storage: {} items", getMaxQuantumItemCellStorage() == Integer.MAX_VALUE ? "unlimited" : String.format("%,d", getMaxQuantumItemCellStorage()));
        LOGGER.info("  Max Quantum Fluid Cell Storage: {}mb", getMaxQuantumFluidCellStorage() == Integer.MAX_VALUE ? "unlimited" : String.format("%,d", getMaxQuantumFluidCellStorage()));
    }
}