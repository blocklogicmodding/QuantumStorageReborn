package com.blocklogic.quantumstoragereborn.datagen.custom;

import com.blocklogic.quantumstoragereborn.block.QSRBlocks;
import com.blocklogic.quantumstoragereborn.item.QSRItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
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

        // Crates
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, QSRBlocks.COPPER_CRATE.get())
                .pattern("#O#")
                .pattern("OXO")
                .pattern("#O#")
                .define('O', ItemTags.PLANKS)
                .define('#', Items.COPPER_INGOT)
                .define('X', Tags.Items.CHESTS)
                .unlockedBy("has_copper_ingot", has(Items.COPPER_INGOT))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, QSRBlocks.IRON_CRATE.get())
                .pattern("#O#")
                .pattern("OXO")
                .pattern("#O#")
                .define('O', ItemTags.PLANKS)
                .define('#', Items.IRON_INGOT)
                .define('X', Tags.Items.CHESTS)
                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, QSRBlocks.GOLD_CRATE.get())
                .pattern("#O#")
                .pattern("OXO")
                .pattern("#O#")
                .define('O', ItemTags.PLANKS)
                .define('#', Items.GOLD_INGOT)
                .define('X', Tags.Items.CHESTS)
                .unlockedBy("has_gold_ingot", has(Items.GOLD_INGOT))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, QSRBlocks.DIAMOND_CRATE.get())
                .pattern("#O#")
                .pattern("OXO")
                .pattern("#O#")
                .define('O', ItemTags.PLANKS)
                .define('#', Items.DIAMOND)
                .define('X', Tags.Items.CHESTS)
                .unlockedBy("has_diamond", has(Items.DIAMOND))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, QSRBlocks.NETHERITE_CRATE.get())
                .pattern("#O#")
                .pattern("OXO")
                .pattern("#O#")
                .define('O', ItemTags.PLANKS)
                .define('#', Items.NETHERITE_INGOT)
                .define('X', Tags.Items.CHESTS)
                .unlockedBy("has_netherite_ingot", has(Items.NETHERITE_INGOT))
                .save(recipeOutput);

        // Upgrade Bases - Used for upgrading crates in-place
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, QSRItems.IRON_CRATE_UPGRADE.get())
                .pattern("#G#")
                .pattern("GRG")
                .pattern("#G#")
                .define('G', Tags.Items.GLASS_PANES)
                .define('#', Items.IRON_INGOT)
                .define('R', Items.REDSTONE)
                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, QSRItems.GOLD_CRATE_UPGRADE.get())
                .pattern("#G#")
                .pattern("GRG")
                .pattern("#G#")
                .define('G', Tags.Items.GLASS_PANES)
                .define('#', Items.GOLD_INGOT)
                .define('R', Items.REDSTONE)
                .unlockedBy("has_gold_ingot", has(Items.GOLD_INGOT))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, QSRItems.DIAMOND_CRATE_UPGRADE.get())
                .pattern("#G#")
                .pattern("GRG")
                .pattern("#G#")
                .define('G', Tags.Items.GLASS_PANES)
                .define('#', Items.DIAMOND)
                .define('R', Items.REDSTONE)
                .unlockedBy("has_diamond", has(Items.DIAMOND))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, QSRItems.NETHERITE_CRATE_UPGRADE.get())
                .pattern("#G#")
                .pattern("GRG")
                .pattern("#G#")
                .define('G', Tags.Items.GLASS_PANES)
                .define('#', Items.NETHERITE_INGOT)
                .define('R', Items.REDSTONE)
                .unlockedBy("has_netherite_ingot", has(Items.NETHERITE_INGOT))
                .save(recipeOutput);

        // Backpacks
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, QSRItems.COPPER_BACKPACK.get())
                .pattern("#O#")
                .pattern("OXO")
                .pattern("#O#")
                .define('O', Tags.Items.LEATHERS)
                .define('#', Items.COPPER_INGOT)
                .define('X', Tags.Items.CHESTS)
                .unlockedBy("has_copper_ingot", has(Items.COPPER_INGOT))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, QSRItems.IRON_BACKPACK.get())
                .pattern("#O#")
                .pattern("OXO")
                .pattern("#O#")
                .define('O', Tags.Items.LEATHERS)
                .define('#', Items.IRON_INGOT)
                .define('X', Tags.Items.CHESTS)
                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, QSRItems.GOLD_BACKPACK.get())
                .pattern("#O#")
                .pattern("OXO")
                .pattern("#O#")
                .define('O', Tags.Items.LEATHERS)
                .define('#', Items.GOLD_INGOT)
                .define('X', Tags.Items.CHESTS)
                .unlockedBy("has_gold_ingot", has(Items.GOLD_INGOT))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, QSRItems.DIAMOND_BACKPACK.get())
                .pattern("#O#")
                .pattern("OXO")
                .pattern("#O#")
                .define('O', Tags.Items.LEATHERS)
                .define('#', Items.DIAMOND)
                .define('X', Tags.Items.CHESTS)
                .unlockedBy("has_diamond", has(Items.DIAMOND))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, QSRItems.NETHERITE_BACKPACK.get())
                .pattern("#O#")
                .pattern("OXO")
                .pattern("#O#")
                .define('O', Tags.Items.LEATHERS)
                .define('#', Items.NETHERITE_INGOT)
                .define('X', Tags.Items.CHESTS)
                .unlockedBy("has_netherite_ingot", has(Items.NETHERITE_INGOT))
                .save(recipeOutput);
    }
}
