package com.blocklogic.quantumstoragereborn.block.custom;

import com.blocklogic.quantumstoragereborn.component.QSRDataComponents;
import com.blocklogic.quantumstoragereborn.entity.custom.IronCrateBlockEntity;
import com.blocklogic.quantumstoragereborn.item.QSRItems;
import com.blocklogic.quantumstoragereborn.util.CrateUpgradeHandler;
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

import java.util.ArrayList;
import java.util.List;

public class IronCrateBlock extends BaseEntityBlock {
    public static final MapCodec<IronCrateBlock> CODEC = simpleCodec(IronCrateBlock::new);

    public IronCrateBlock(Properties properties) {
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
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new IronCrateBlockEntity(blockPos, blockState);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.getItem() == QSRItems.GOLD_CRATE_UPGRADE.get()) {
            if (!level.isClientSide()) {
                boolean upgraded = CrateUpgradeHandler.performUpgrade(level, pos, state, player, hand);
                if (upgraded) {
                    return ItemInteractionResult.SUCCESS;
                }
            }
            return ItemInteractionResult.SUCCESS;
        }

        if (level.getBlockEntity(pos) instanceof IronCrateBlockEntity ironCrateBlockEntity) {
            if (!level.isClientSide()) {
                ((ServerPlayer) player).openMenu(new SimpleMenuProvider(ironCrateBlockEntity,
                        Component.translatable("gui.quantumstoragereborn.iron_crate")), pos);
                return ItemInteractionResult.SUCCESS;
            }
        }
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        List<ItemStack> drops = super.getDrops(state, params);

        if (params.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof IronCrateBlockEntity ironCrateBlockEntity) {
            for (ItemStack drop : drops) {
                if (drop.getItem() instanceof BlockItem blockItem && blockItem.getBlock() == this) {
                    List<ItemStack> inventoryItems = new ArrayList<>();

                    for (int i = 0; i < ironCrateBlockEntity.inventory.getSlots(); i++) {
                        ItemStack stack = ironCrateBlockEntity.inventory.getStackInSlot(i);
                        inventoryItems.add(stack.copy());
                    }

                    if (inventoryItems.stream().anyMatch(stack -> !stack.isEmpty())) {
                        QSRDataComponents.InventoryData inventoryData = QSRDataComponents.InventoryData.create(inventoryItems);
                        drop.set(QSRDataComponents.INVENTORY_DATA.get(), inventoryData);
                    }
                }
            }
        }

        return drops;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof IronCrateBlockEntity ironCrateBlockEntity) {
            QSRDataComponents.InventoryData inventoryData = stack.get(QSRDataComponents.INVENTORY_DATA.get());
            if (inventoryData != null && !inventoryData.isEmpty()) {
                ironCrateBlockEntity.restoreInventoryData(inventoryData);
            }
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if(state.getBlock() != newState.getBlock()) {
            if(level.getBlockEntity(pos) instanceof IronCrateBlockEntity ironCrateBlockEntity) {
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}