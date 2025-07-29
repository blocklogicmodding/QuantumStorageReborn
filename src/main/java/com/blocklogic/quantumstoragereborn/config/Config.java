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

    // ========================================
    // QUANTUM CORE RANGES CONFIGURATION
    // ========================================

    public static ModConfigSpec.IntValue QUANTUM_CORE_BASE_RANGE;
    public static ModConfigSpec.IntValue GOLD_RANGE_EXTENDER;
    public static ModConfigSpec.IntValue DIAMOND_RANGE_EXTENDER;
    public static ModConfigSpec.IntValue NETHERITE_RANGE_EXTENDER;

    public static void register(ModContainer container) {
        registerCommonConfigs(container);
    }

    private static void registerCommonConfigs(ModContainer container) {
        storageLimitsConfig();
        quantumCoreRangesConfig();
        COMMON_CONFIG = COMMON_BUILDER.build();
        SPEC = COMMON_CONFIG; // Legacy compatibility
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

    private static void quantumCoreRangesConfig() {
        COMMON_BUILDER.comment("Quantum Core Ranges - Configure range limits for quantum cores and range extenders").push(CATEGORY_QUANTUM_CORE_RANGES);

        QUANTUM_CORE_BASE_RANGE = COMMON_BUILDER.comment("Base quantum core range (blocks)",
                        "This is the default range when no range extenders are installed",
                        "Range covers radius around the quantum core",
                        "Total area covered is (range*2+1)³ blocks")
                .defineInRange("quantum_core_base_range", 8, 4, 32);

        GOLD_RANGE_EXTENDER = COMMON_BUILDER.comment("Gold range extender range (blocks)",
                        "Range when gold range extender is installed",
                        "Replaces base range when extender is present")
                .defineInRange("gold_range_extender", 16, 8, 64);

        DIAMOND_RANGE_EXTENDER = COMMON_BUILDER.comment("Diamond range extender range (blocks)",
                        "Range when diamond range extender is installed",
                        "Replaces base range when extender is present")
                .defineInRange("diamond_range_extender", 32, 16, 128);

        NETHERITE_RANGE_EXTENDER = COMMON_BUILDER.comment("Netherite range extender range (blocks)",
                        "Range when netherite range extender is installed",
                        "Replaces base range when extender is present",
                        "",
                        "WARNING: Large ranges can impact server performance significantly!",
                        "A range of 64 covers a 129x129x129 block area - use with caution on multiplayer servers",
                        "Maximum range of 256 covers a 513x513x513 area and may cause severe lag!")
                .defineInRange("netherite_range_extender", 64, 32, 256);

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
    // GETTER METHODS FOR QUANTUM CORE RANGES
    // ========================================

    public static int getQuantumCoreBaseRange() {
        return QUANTUM_CORE_BASE_RANGE.get();
    }

    public static int getGoldRangeExtender() {
        return GOLD_RANGE_EXTENDER.get();
    }

    public static int getDiamondRangeExtender() {
        return DIAMOND_RANGE_EXTENDER.get();
    }

    public static int getNetheriteRangeExtender() {
        return NETHERITE_RANGE_EXTENDER.get();
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

        if (getGoldRangeExtender() <= getQuantumCoreBaseRange()) {
            LOGGER.warn("Gold range extender ({}) should be greater than base range ({})", getGoldRangeExtender(), getQuantumCoreBaseRange());
        }

        if (getDiamondRangeExtender() <= getGoldRangeExtender()) {
            LOGGER.warn("Diamond range extender ({}) should be greater than gold range ({})", getDiamondRangeExtender(), getGoldRangeExtender());
        }

        if (getNetheriteRangeExtender() <= getDiamondRangeExtender()) {
            LOGGER.warn("Netherite range extender ({}) should be greater than diamond range ({})", getNetheriteRangeExtender(), getDiamondRangeExtender());
        }

        if (getNetheriteRangeExtender() > 128) {
            LOGGER.warn("Netherite range ({}) is very large and may impact server performance! Consider reducing for multiplayer servers.", getNetheriteRangeExtender());
        }

        if (getNetheriteRangeExtender() > 200) {
            LOGGER.error("Netherite range ({}) is extremely large! This WILL cause severe performance issues on multiplayer servers!", getNetheriteRangeExtender());
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

        LOGGER.info("Quantum Core Ranges Configuration:");
        LOGGER.info("  Base Range: {} blocks ({}x{}x{} area)", getQuantumCoreBaseRange(),
                getQuantumCoreBaseRange() * 2 + 1, getQuantumCoreBaseRange() * 2 + 1, getQuantumCoreBaseRange() * 2 + 1);
        LOGGER.info("  Gold Extender: {} blocks ({}x{}x{} area)", getGoldRangeExtender(),
                getGoldRangeExtender() * 2 + 1, getGoldRangeExtender() * 2 + 1, getGoldRangeExtender() * 2 + 1);
        LOGGER.info("  Diamond Extender: {} blocks ({}x{}x{} area)", getDiamondRangeExtender(),
                getDiamondRangeExtender() * 2 + 1, getDiamondRangeExtender() * 2 + 1, getDiamondRangeExtender() * 2 + 1);
        LOGGER.info("  Netherite Extender: {} blocks ({}x{}x{} area)", getNetheriteRangeExtender(),
                getNetheriteRangeExtender() * 2 + 1, getNetheriteRangeExtender() * 2 + 1, getNetheriteRangeExtender() * 2 + 1);

        long netheriteVolume = (long) (getNetheriteRangeExtender() * 2 + 1) * (getNetheriteRangeExtender() * 2 + 1) * (getNetheriteRangeExtender() * 2 + 1);
        if (netheriteVolume > 2097152) { // 128^3
            LOGGER.warn("Netherite extender covers {} blocks - this is a very large volume!", String.format("%,d", netheriteVolume));
        }
    }
}