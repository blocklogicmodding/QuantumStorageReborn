package com.blocklogic.quantumstoragereborn.datagen.custom;

import com.blocklogic.quantumstoragereborn.block.QSRBlocks;
import com.blocklogic.quantumstoragereborn.item.QSRItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.concurrent.CompletableFuture;

public class QSRRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public QSRRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {

        // Quantum Blocks
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, QSRBlocks.QUANTUM_CORE.get())
                .pattern("DPE")
                .pattern("PQP")
                .pattern("EPD")
                .define('D', Items.DIAMOND_BLOCK)
                .define('E', Items.EMERALD_BLOCK)
                .define('P', Items.ENDER_PEARL)
                .define('Q', QSRBlocks.QUANTUM_ITEM_CELL)
                .unlockedBy("has_quantum_item_cell", has(QSRBlocks.QUANTUM_ITEM_CELL))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, QSRBlocks.QUANTUM_ITEM_CELL.get())
                .pattern("DCD")
                .pattern("CGC")
                .pattern("DCD")
                .define('C', Tags.Items.CHESTS)
                .define('D', Items.DIAMOND_BLOCK)
                .define('G', Tags.Items.GLASS_BLOCKS)
                .unlockedBy("has_diamond", has(Items.DIAMOND))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, QSRBlocks.QUANTUM_FLUID_CELL.get())
                .pattern("DBD")
                .pattern("BGB")
                .pattern("DBD")
                .define('B', Items.BUCKET)
                .define('D', Items.DIAMOND_BLOCK)
                .define('G', Tags.Items.GLASS_BLOCKS)
                .unlockedBy("has_diamond", has(Items.DIAMOND))
                .save(recipeOutput);

        //Trashcan
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, QSRBlocks.TRASHCAN.get())
                .pattern("SSS")
                .pattern("SES")
                .pattern("SSS")
                .define('E', Items.ENDER_PEARL)
                .define('S', Tags.Items.STONES)
                .unlockedBy("has_ender_pearl", has(Items.ENDER_PEARL))
                .save(recipeOutput);

        //Acacia Crates
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, QSRBlocks.ACACIA_COPPER_CRATE.get())
                .pattern("OOO")
                .pattern("#X#")
                .pattern("OOO")
                .define('O', Items.ACACIA_PLANKS)
                .define('#', Items.COPPER_INGOT)
                .define('X', Tags.Items.CHESTS)
                .unlockedBy("has_copper_ingot", has(Items.COPPER_INGOT))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, QSRBlocks.ACACIA_IRON_CRATE.get())
                .pattern("OOO")
                .pattern("#X#")
                .pattern("OOO")
                .define('O', Items.ACACIA_PLANKS)
                .define('#', Items.IRON_INGOT)
                .define('X', QSRBlocks.OAK_COPPER_CRATE.get())
                .unlockedBy("has_copper_crate", has(QSRBlocks.OAK_COPPER_CRATE.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, QSRBlocks.ACACIA_GOLD_CRATE.get())
                .pattern("OOO")
                .pattern("#X#")
                .pattern("OOO")
                .define('O', Items.ACACIA_PLANKS)
                .define('#', Items.GOLD_INGOT)
                .define('X', QSRBlocks.OAK_IRON_CRATE.get())
                .unlockedBy("has_iron_crate", has(QSRBlocks.OAK_IRON_CRATE.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, QSRBlocks.ACACIA_DIAMOND_CRATE.get())
                .pattern("OOO")
                .pattern("#X#")
                .pattern("OOO")
                .define('O', Items.ACACIA_PLANKS)
                .define('#', Items.DIAMOND)
                .define('X', QSRBlocks.OAK_GOLD_CRATE.get())
                .unlockedBy("has_gold_crate", has(QSRBlocks.OAK_GOLD_CRATE.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, QSRBlocks.ACACIA_NETHERITE_CRATE.get())
                .pattern("OOO")
                .pattern("#X#")
                .pattern("OOO")
                .define('O', Items.ACACIA_PLANKS)
                .define('#', Items.NETHERITE_INGOT)
                .define('X', QSRBlocks.OAK_DIAMOND_CRATE.get())
                .unlockedBy("has_diamond_crate", has(QSRBlocks.OAK_DIAMOND_CRATE.get()))
                .save(recipeOutput);

        //Oak Crates
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, QSRBlocks.OAK_COPPER_CRATE.get())
                .pattern("OOO")
                .pattern("#X#")
                .pattern("OOO")
                .define('O', Items.OAK_PLANKS)
                .define('#', Items.COPPER_INGOT)
                .define('X', Tags.Items.CHESTS)
                .unlockedBy("has_copper_ingot", has(Items.COPPER_INGOT))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, QSRBlocks.OAK_IRON_CRATE.get())
                .pattern("OOO")
                .pattern("#X#")
                .pattern("OOO")
                .define('O', Items.OAK_PLANKS)
                .define('#', Items.IRON_INGOT)
                .define('X', QSRBlocks.OAK_COPPER_CRATE.get())
                .unlockedBy("has_copper_crate", has(QSRBlocks.OAK_COPPER_CRATE.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, QSRBlocks.OAK_GOLD_CRATE.get())
                .pattern("OOO")
                .pattern("#X#")
                .pattern("OOO")
                .define('O', Items.OAK_PLANKS)
                .define('#', Items.GOLD_INGOT)
                .define('X', QSRBlocks.OAK_IRON_CRATE.get())
                .unlockedBy("has_iron_crate", has(QSRBlocks.OAK_IRON_CRATE.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, QSRBlocks.OAK_DIAMOND_CRATE.get())
                .pattern("OOO")
                .pattern("#X#")
                .pattern("OOO")
                .define('O', Items.OAK_PLANKS)
                .define('#', Items.DIAMOND)
                .define('X', QSRBlocks.OAK_GOLD_CRATE.get())
                .unlockedBy("has_gold_crate", has(QSRBlocks.OAK_GOLD_CRATE.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, QSRBlocks.OAK_NETHERITE_CRATE.get())
                .pattern("OOO")
                .pattern("#X#")
                .pattern("OOO")
                .define('O', Items.OAK_PLANKS)
                .define('#', Items.NETHERITE_INGOT)
                .define('X', QSRBlocks.OAK_DIAMOND_CRATE.get())
                .unlockedBy("has_diamond_crate", has(QSRBlocks.OAK_DIAMOND_CRATE.get()))
                .save(recipeOutput);

        //Items
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, QSRItems.QUANTUM_WRENCH.get())
                .pattern(" PD")
                .pattern(" SP")
                .pattern("S  ")
                .define('D', Items.DIAMOND)
                .define('P', Items.ENDER_PEARL)
                .define('S', Items.STICK)
                .unlockedBy("has_diamond", has(Items.DIAMOND))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, QSRItems.GOLD_RANGE_EXTENDER.get())
                .pattern("GPG")
                .pattern("P#P")
                .pattern("GPG")
                .define('G', Items.GOLD_BLOCK)
                .define('P', Items.ENDER_PEARL)
                .define('#', Items.REDSTONE_BLOCK)
                .unlockedBy("has_redstone", has(Items.REDSTONE))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, QSRItems.DIAMOND_RANGE_EXTENDER.get())
                .pattern("GPG")
                .pattern("P#P")
                .pattern("GPG")
                .define('G', Items.DIAMOND_BLOCK)
                .define('P', Items.ENDER_PEARL)
                .define('#', QSRItems.GOLD_RANGE_EXTENDER.get())
                .unlockedBy("has_gold_range_extender", has(QSRItems.GOLD_RANGE_EXTENDER.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, QSRItems.NETHERITE_RANGE_EXTENDER.get())
                .pattern("GPG")
                .pattern("P#P")
                .pattern("GPG")
                .define('G', Items.NETHERITE_INGOT)
                .define('P', Items.ENDER_PEARL)
                .define('#', QSRItems.GOLD_RANGE_EXTENDER.get())
                .unlockedBy("has_diamond_range_extender", has(QSRItems.GOLD_RANGE_EXTENDER.get()))
                .save(recipeOutput);
    }
}
