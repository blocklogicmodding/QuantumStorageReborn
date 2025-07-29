package com.blocklogic.quantumstoragereborn.block.custom;

import com.blocklogic.quantumstoragereborn.component.QSRDataComponents;
import com.blocklogic.quantumstoragereborn.entity.custom.QuantumFluidCellBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
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
        if (level.isClientSide()) {
            return ItemInteractionResult.SUCCESS;
        }

        if (level.getBlockEntity(pos) instanceof QuantumFluidCellBlockEntity cellEntity) {
            ((ServerPlayer) player).openMenu(new SimpleMenuProvider(cellEntity,
                    Component.translatable("gui.quantumstoragereborn.quantum_fluid_cell")), pos);
        }

        return ItemInteractionResult.SUCCESS;
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