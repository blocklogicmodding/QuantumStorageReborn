package com.blocklogic.quantumstoragereborn.block.custom;

import com.blocklogic.quantumstoragereborn.component.QSRDataComponents;
import com.blocklogic.quantumstoragereborn.entity.custom.QuantumFluidCellBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class QuantumFluidCellBlock extends BaseEntityBlock {
    public static final MapCodec<QuantumFluidCellBlock> CODEC = simpleCodec(QuantumFluidCellBlock::new);

    public QuantumFluidCellBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new QuantumFluidCellBlockEntity(pos, state);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof QuantumFluidCellBlockEntity cellEntity) {
            ItemStack heldItem = player.getItemInHand(hand);

            if (heldItem.getItem() == Items.BUCKET) {
                if (level.isClientSide()) {
                    return ItemInteractionResult.SUCCESS;
                }

                return extractFluidToBucket(cellEntity, player, level, pos, state);
            }

            else if (heldItem.getItem() instanceof BucketItem bucketItem && bucketItem.content != Fluids.EMPTY) {
                if (level.isClientSide()) {
                    return ItemInteractionResult.SUCCESS;
                }

                Fluid fluid = bucketItem.content;
                ResourceLocation fluidId = fluid.builtInRegistryHolder().key().location();
                FluidStack fluidStack = new FluidStack(fluid, 1000);

                if (cellEntity.canStoreFluid(fluidStack)) {
                    int stored = cellEntity.storeFluid(fluidStack);
                    if (stored >= 1000) {
                        heldItem.shrink(1);
                        ItemStack emptyBucket = new ItemStack(Items.BUCKET);
                        if (!player.getInventory().add(emptyBucket)) {
                            player.drop(emptyBucket, false);
                        }

                        level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1f, 1.5f);
                        level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
                        cellEntity.setChanged();
                        return ItemInteractionResult.SUCCESS;
                    } else {
                        player.displayClientMessage(Component.translatable("message.quantumstoragereborn.fluid_cell_full"), true);
                        return ItemInteractionResult.SUCCESS;
                    }
                } else {
                    // Get the stored fluid name for the error message
                    QuantumFluidCellBlockEntity.CellContents contents = cellEntity.getContents();
                    if (contents.storedFluidId().isPresent()) {
                        Fluid storedFluid = BuiltInRegistries.FLUID.get(contents.storedFluidId().get());
                        Component fluidName = Component.translatable(storedFluid.getFluidType().getDescriptionId());
                        player.displayClientMessage(Component.translatable("message.quantumstoragereborn.fluid_cell_wrong_fluid", fluidName), true);
                    } else {
                        player.displayClientMessage(Component.translatable("message.quantumstoragereborn.fluid_cell_wrong_fluid", "Unknown Fluid"), true);
                    }
                    return ItemInteractionResult.SUCCESS;
                }
            }

            if (!level.isClientSide()) {
                ((ServerPlayer) player).openMenu(new SimpleMenuProvider(cellEntity,
                        Component.translatable("gui.quantumstoragereborn.quantum_fluid_cell")), pos);
            }
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.FAIL;
    }

    private ItemInteractionResult extractFluidToBucket(QuantumFluidCellBlockEntity cellEntity, Player player,
                                                       Level level, BlockPos pos, BlockState state) {
        QuantumFluidCellBlockEntity.CellContents contents = cellEntity.getContents();

        if (contents.storedFluidId().isEmpty() || contents.amount() < 1000) {
            player.displayClientMessage(Component.translatable("message.quantumstoragereborn.fluid_cell_empty"), true);
            return ItemInteractionResult.SUCCESS;
        }

        ResourceLocation fluidId = contents.storedFluidId().get();
        ItemStack bucketToGive = getBucketForFluid(fluidId);

        if (bucketToGive.isEmpty()) {
            player.displayClientMessage(Component.translatable("message.quantumstoragereborn.no_bucket_for_fluid"), true);
            return ItemInteractionResult.SUCCESS;
        }

        FluidStack extracted = cellEntity.extractFluid(1000);
        if (!extracted.isEmpty()) {
            player.getItemInHand(InteractionHand.MAIN_HAND).shrink(1);

            if (!player.getInventory().add(bucketToGive)) {
                player.drop(bucketToGive, false);
            }

            level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1f, 1.5f);
            level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
            cellEntity.setChanged();

            player.displayClientMessage(Component.translatable("message.quantumstoragereborn.fluid_extracted"), true);
            return ItemInteractionResult.SUCCESS;
        }

        return ItemInteractionResult.SUCCESS;
    }

    private ItemStack getBucketForFluid(ResourceLocation fluidId) {
        if (fluidId.equals(Fluids.WATER.builtInRegistryHolder().key().location())) {
            return new ItemStack(Items.WATER_BUCKET);
        } else if (fluidId.equals(Fluids.LAVA.builtInRegistryHolder().key().location())) {
            return new ItemStack(Items.LAVA_BUCKET);
        }

        try {
            Fluid fluid = BuiltInRegistries.FLUID.get(fluidId);
            if (fluid != null && fluid != Fluids.EMPTY) {
                for (Item item : BuiltInRegistries.ITEM) {
                    if (item instanceof BucketItem bucketItem && bucketItem.content == fluid) {
                        return new ItemStack(item);
                    }
                }
            }
        } catch (Exception e) {
        }

        return ItemStack.EMPTY;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        List<ItemStack> drops = super.getDrops(state, params);

        if (params.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof QuantumFluidCellBlockEntity cellEntity) {
            for (ItemStack drop : drops) {
                if (drop.getItem() instanceof BlockItem blockItem && blockItem.getBlock() == this) {
                    QuantumFluidCellBlockEntity.CellContents contents = cellEntity.getContents();

                    if (contents.storedFluidId().isPresent() && contents.amount() > 0) {
                        drop.set(QSRDataComponents.FLUID_CELL_CONTENTS.get(), contents);
                    }
                }
            }
        }

        return drops;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof QuantumFluidCellBlockEntity cellEntity) {
            QuantumFluidCellBlockEntity.CellContents contents = stack.get(QSRDataComponents.FLUID_CELL_CONTENTS.get());
            if (contents != null) {
                cellEntity.restoreContents(contents);
            }
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.getBlock() != newState.getBlock()) {
            if (level.getBlockEntity(pos) instanceof QuantumFluidCellBlockEntity cellEntity) {
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}