package com.blocklogic.quantumstoragereborn.entity.custom;

import com.blocklogic.quantumstoragereborn.config.Config;
import com.blocklogic.quantumstoragereborn.container.menu.QuantumItemCellMenu;
import com.blocklogic.quantumstoragereborn.entity.QSRBlockEntities;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.Optional;

public class QuantumItemCellBlockEntity extends BlockEntity implements MenuProvider {

    public record CellContents(Optional<ResourceLocation> storedItemId, int count, boolean locked) {}
    private float rotation;

    public static final Codec<CellContents> CELL_CONTENTS_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ResourceLocation.CODEC.optionalFieldOf("storedItemId").forGetter(CellContents::storedItemId),
                    Codec.INT.fieldOf("count").forGetter(CellContents::count),
                    Codec.BOOL.fieldOf("locked").forGetter(CellContents::locked)
            ).apply(instance, CellContents::new)
    );

    private CellContents contents = new CellContents(Optional.empty(), 0, false);

    public QuantumItemCellBlockEntity(BlockPos pos, BlockState blockState) {
        super(QSRBlockEntities.QUANTUM_ITEM_CELL_BE.get(), pos, blockState);
    }

    public static boolean hasSignificantNBT(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (stack.isDamaged()) return true;

        ItemEnchantments enchantments = stack.get(DataComponents.ENCHANTMENTS);
        if (enchantments != null && !enchantments.isEmpty()) return true;

        ItemEnchantments stored = stack.get(DataComponents.STORED_ENCHANTMENTS);
        if (stored != null && !stored.isEmpty()) return true;

        if (stack.get(DataComponents.CUSTOM_NAME) != null) return true;

        ItemLore lore = stack.get(DataComponents.LORE);
        if (lore != null && !lore.lines().isEmpty()) return true;

        return false;
    }

    public boolean canStoreItem(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (hasSignificantNBT(stack)) return false;

        if (contents.storedItemId().isEmpty()) {
            return true;
        }

        if (contents.count() <= 0 && !contents.locked()) {
            return true;
        }

        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return contents.storedItemId().get().equals(itemId);
    }

    public int storeItems(ItemStack stack, Player player) {
        if (!canStoreItem(stack)) {
            if (hasSignificantNBT(stack)) {
                player.displayClientMessage(Component.translatable("message.quantumstoragereborn.cell_no_nbt"), true);
            } else if (contents.storedItemId().isPresent()) {
                Item storedItem = BuiltInRegistries.ITEM.get(contents.storedItemId().get());
                player.displayClientMessage(Component.translatable("message.quantumstoragereborn.cell_wrong_item",
                        storedItem.getDescription()), true);
            }
            return 0;
        }

        int maxCapacity = Config.getMaxQuantumItemCellStorage();
        int spaceAvailable = maxCapacity - contents.count();
        int toStore = Math.min(stack.getCount(), spaceAvailable);

        if (toStore <= 0) {
            player.displayClientMessage(Component.translatable("message.quantumstoragereborn.cell_full"), true);
            return 0;
        }

        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        contents = new CellContents(Optional.of(itemId), contents.count() + toStore, contents.locked());

        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }

        return toStore;
    }

    public ItemStack extractItems(int amount, Player player) {
        if (contents.storedItemId().isEmpty() || contents.count() <= 0) {
            player.displayClientMessage(Component.translatable("message.quantumstoragereborn.cell_empty"), true);
            return ItemStack.EMPTY;
        }

        Item item = BuiltInRegistries.ITEM.get(contents.storedItemId().get());
        int maxStackSize = item.getDefaultMaxStackSize();
        int toExtract = Math.min(Math.min(amount, contents.count()), maxStackSize);

        if (toExtract <= 0) {
            return ItemStack.EMPTY;
        }

        ItemStack extracted = new ItemStack(item, toExtract);
        contents = new CellContents(contents.storedItemId(), contents.count() - toExtract, contents.locked());

        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }

        return extracted;
    }

    public void toggleLock(Player player) {
        if (contents.storedItemId().isEmpty()) {
            player.displayClientMessage(Component.translatable("message.quantumstoragereborn.cell_cannot_lock_empty"), true);
            return;
        }

        contents = new CellContents(contents.storedItemId(), contents.count(), !contents.locked());

        Component message = contents.locked() ?
                Component.translatable("message.quantumstoragereborn.cell_locked") :
                Component.translatable("message.quantumstoragereborn.cell_unlocked");
        player.displayClientMessage(message, true);

        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    public CellContents getContents() {
        return contents;
    }

    public boolean isEmpty() {
        return contents.storedItemId().isEmpty() || contents.count() <= 0;
    }

    public boolean isLocked() {
        return contents.locked();
    }

    public float getCapacityPercentage() {
        if (contents.count() <= 0) return 0.0f;
        return (float) contents.count() / Config.getMaxQuantumItemCellStorage();
    }

    public void restoreContents(CellContents restoredContents) {
        this.contents = restoredContents;
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    public float renderRotation() {
        rotation += 0.5F;
        if (rotation >= 360) {
            rotation = 0;
        }
        return rotation;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (contents.storedItemId().isPresent()) {
            tag.putString("StoredItemId", contents.storedItemId().get().toString());
        }
        tag.putInt("Count", contents.count());
        tag.putBoolean("Locked", contents.locked());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        Optional<ResourceLocation> itemId = tag.contains("StoredItemId") ?
                Optional.of(ResourceLocation.parse(tag.getString("StoredItemId"))) :
                Optional.empty();
        int count = tag.getInt("Count");
        boolean locked = tag.getBoolean("Locked");
        contents = new CellContents(itemId, count, locked);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("gui.quantumstoragereborn.quantum_item_cell");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new QuantumItemCellMenu(containerId, playerInventory, this);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, QSRBlockEntities.QUANTUM_ITEM_CELL_BE.get(),
                (blockEntity, direction) -> {
                    if (blockEntity instanceof QuantumItemCellBlockEntity cellEntity) {
                        return cellEntity.getItemHandler();
                    }
                    return null;
                });
    }

    private ItemStackHandler getItemHandler() {
        return new ItemStackHandler(1) {
            @Override
            public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                if (!canStoreItem(stack)) return stack;

                int maxCapacity = Config.getMaxQuantumItemCellStorage();
                int spaceAvailable = maxCapacity - contents.count();
                int toStore = Math.min(stack.getCount(), spaceAvailable);

                if (toStore <= 0) return stack;

                if (!simulate) {
                    ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
                    contents = new CellContents(Optional.of(itemId), contents.count() + toStore, contents.locked());
                    setChanged();
                    if (level != null && !level.isClientSide()) {
                        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
                    }
                }

                ItemStack remainder = stack.copy();
                remainder.shrink(toStore);
                return remainder;
            }

            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                if (contents.storedItemId().isEmpty() || contents.count() <= 0) return ItemStack.EMPTY;

                Item item = BuiltInRegistries.ITEM.get(contents.storedItemId().get());
                int toExtract = Math.min(Math.min(amount, contents.count()), item.getDefaultMaxStackSize());

                if (toExtract <= 0) return ItemStack.EMPTY;

                if (!simulate) {
                    contents = new CellContents(contents.storedItemId(), contents.count() - toExtract, contents.locked());
                    setChanged();
                    if (level != null && !level.isClientSide()) {
                        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
                    }
                }

                return new ItemStack(item, toExtract);
            }
        };
    }
}