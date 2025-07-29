package com.blocklogic.quantumstoragereborn.entity.custom;

import com.blocklogic.quantumstoragereborn.config.Config;
import com.blocklogic.quantumstoragereborn.container.menu.QuantumFluidCellMenu;
import com.blocklogic.quantumstoragereborn.entity.QSRBlockEntities;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.Optional;

public class QuantumFluidCellBlockEntity extends BlockEntity implements MenuProvider {

    public record CellContents(Optional<ResourceLocation> storedFluidId, int amount, boolean locked) {}

    public static final Codec<CellContents> CELL_CONTENTS_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ResourceLocation.CODEC.optionalFieldOf("storedFluidId").forGetter(CellContents::storedFluidId),
                    Codec.INT.fieldOf("amount").forGetter(CellContents::amount),
                    Codec.BOOL.fieldOf("locked").forGetter(CellContents::locked)
            ).apply(instance, CellContents::new)
    );

    private CellContents contents = new CellContents(Optional.empty(), 0, false);

    public final ItemStackHandler inventory = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (slot == 0) {
                processInputSlot();
            }
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot == 0) {
                return hasFluidCapability(stack);
            }
            return slot == 1; // Output slot accepts anything
        }
    };

    public QuantumFluidCellBlockEntity(BlockPos pos, BlockState blockState) {
        super(QSRBlockEntities.QUANTUM_FLUID_CELL_BE.get(), pos, blockState);
    }

    private boolean hasFluidCapability(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return stack.getCapability(Capabilities.FluidHandler.ITEM) != null;
    }

    private void processInputSlot() {
        if (level == null || level.isClientSide()) return;

        ItemStack inputStack = inventory.getStackInSlot(0);
        ItemStack outputStack = inventory.getStackInSlot(1);

        if (inputStack.isEmpty()) return;

        IFluidHandlerItem fluidHandler = inputStack.getCapability(Capabilities.FluidHandler.ITEM);
        if (fluidHandler == null) return;

        // Try to drain fluid from the item
        FluidStack drainedFluid = fluidHandler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE);
        if (!drainedFluid.isEmpty() && canStoreFluid(drainedFluid)) {
            int stored = storeFluid(drainedFluid);
            if (stored > 0) {
                FluidStack actualDrained = fluidHandler.drain(stored, IFluidHandler.FluidAction.EXECUTE);
                ItemStack result = fluidHandler.getContainer();

                if (outputStack.isEmpty()) {
                    inventory.setStackInSlot(1, result);
                    inventory.setStackInSlot(0, ItemStack.EMPTY);
                } else if (ItemStack.isSameItemSameComponents(outputStack, result) &&
                        outputStack.getCount() < outputStack.getMaxStackSize()) {
                    outputStack.grow(1);
                    inventory.setStackInSlot(0, ItemStack.EMPTY);
                }
            }
            return;
        }

        // Try to fill the item with stored fluid
        if (contents.storedFluidId().isPresent() && contents.amount() > 0) {
            Fluid storedFluid = BuiltInRegistries.FLUID.get(contents.storedFluidId().get());
            FluidStack fluidToFill = new FluidStack(storedFluid, contents.amount());

            int filled = fluidHandler.fill(fluidToFill, IFluidHandler.FluidAction.SIMULATE);
            if (filled > 0) {
                fluidHandler.fill(new FluidStack(storedFluid, filled), IFluidHandler.FluidAction.EXECUTE);
                ItemStack result = fluidHandler.getContainer();

                if (outputStack.isEmpty()) {
                    inventory.setStackInSlot(1, result);
                    inventory.setStackInSlot(0, ItemStack.EMPTY);
                    contents = new CellContents(contents.storedFluidId(), contents.amount() - filled, contents.locked());
                    setChanged();
                    level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
                } else if (ItemStack.isSameItemSameComponents(outputStack, result) &&
                        outputStack.getCount() < outputStack.getMaxStackSize()) {
                    outputStack.grow(1);
                    inventory.setStackInSlot(0, ItemStack.EMPTY);
                    contents = new CellContents(contents.storedFluidId(), contents.amount() - filled, contents.locked());
                    setChanged();
                    level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
                }
            }
        }
    }

    public boolean canStoreFluid(FluidStack fluidStack) {
        if (fluidStack.isEmpty()) return false;

        if (contents.storedFluidId().isEmpty()) {
            return true;
        }

        if (contents.amount() <= 0 && !contents.locked()) {
            return true;
        }

        ResourceLocation fluidId = BuiltInRegistries.FLUID.getKey(fluidStack.getFluid());
        return contents.storedFluidId().get().equals(fluidId);
    }

    public int storeFluid(FluidStack fluidStack) {
        if (!canStoreFluid(fluidStack)) {
            return 0;
        }

        int maxCapacity = Config.getMaxQuantumFluidCellStorage();
        int spaceAvailable = maxCapacity - contents.amount();
        int toStore = Math.min(fluidStack.getAmount(), spaceAvailable);

        if (toStore <= 0) {
            return 0;
        }

        ResourceLocation fluidId = BuiltInRegistries.FLUID.getKey(fluidStack.getFluid());
        contents = new CellContents(Optional.of(fluidId), contents.amount() + toStore, contents.locked());

        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }

        return toStore;
    }

    public FluidStack extractFluid(int amount) {
        if (contents.storedFluidId().isEmpty() || contents.amount() <= 0) {
            return FluidStack.EMPTY;
        }

        Fluid fluid = BuiltInRegistries.FLUID.get(contents.storedFluidId().get());
        int toExtract = Math.min(amount, contents.amount());

        if (toExtract <= 0) {
            return FluidStack.EMPTY;
        }

        FluidStack extracted = new FluidStack(fluid, toExtract);
        contents = new CellContents(contents.storedFluidId(), contents.amount() - toExtract, contents.locked());

        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }

        return extracted;
    }

    public void toggleLock(Player player) {
        if (contents.storedFluidId().isEmpty()) {
            player.displayClientMessage(Component.translatable("message.quantumstoragereborn.fluid_cell_cannot_lock_empty"), true);
            return;
        }

        contents = new CellContents(contents.storedFluidId(), contents.amount(), !contents.locked());

        Component message = contents.locked() ?
                Component.translatable("message.quantumstoragereborn.fluid_cell_locked") :
                Component.translatable("message.quantumstoragereborn.fluid_cell_unlocked");
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
        return contents.storedFluidId().isEmpty() || contents.amount() <= 0;
    }

    public boolean isLocked() {
        return contents.locked();
    }

    public float getCapacityPercentage() {
        if (contents.amount() <= 0) return 0.0f;
        return (float) contents.amount() / Config.getMaxQuantumFluidCellStorage();
    }

    public void restoreContents(CellContents restoredContents) {
        this.contents = restoredContents;
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", inventory.serializeNBT(registries));
        if (contents.storedFluidId().isPresent()) {
            tag.putString("StoredFluidId", contents.storedFluidId().get().toString());
        }
        tag.putInt("Amount", contents.amount());
        tag.putBoolean("Locked", contents.locked());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound("inventory"));
        Optional<ResourceLocation> fluidId = tag.contains("StoredFluidId") ?
                Optional.of(ResourceLocation.parse(tag.getString("StoredFluidId"))) :
                Optional.empty();
        int amount = tag.getInt("Amount");
        boolean locked = tag.getBoolean("Locked");
        contents = new CellContents(fluidId, amount, locked);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("gui.quantumstoragereborn.quantum_fluid_cell");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new QuantumFluidCellMenu(containerId, playerInventory, this);
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
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, QSRBlockEntities.QUANTUM_FLUID_CELL_BE.get(),
                (blockEntity, direction) -> {
                    if (blockEntity instanceof QuantumFluidCellBlockEntity cellEntity) {
                        return cellEntity.getFluidHandler();
                    }
                    return null;
                });
    }

    private IFluidHandler getFluidHandler() {
        return new IFluidHandler() {
            @Override
            public int getTanks() {
                return 1;
            }

            @Override
            public FluidStack getFluidInTank(int tank) {
                if (contents.storedFluidId().isEmpty() || contents.amount() <= 0) {
                    return FluidStack.EMPTY;
                }
                Fluid fluid = BuiltInRegistries.FLUID.get(contents.storedFluidId().get());
                return new FluidStack(fluid, contents.amount());
            }

            @Override
            public int getTankCapacity(int tank) {
                return Config.getMaxQuantumFluidCellStorage();
            }

            @Override
            public boolean isFluidValid(int tank, FluidStack stack) {
                return canStoreFluid(stack);
            }

            @Override
            public int fill(FluidStack resource, FluidAction action) {
                if (!canStoreFluid(resource)) return 0;

                int maxCapacity = Config.getMaxQuantumFluidCellStorage();
                int spaceAvailable = maxCapacity - contents.amount();
                int toStore = Math.min(resource.getAmount(), spaceAvailable);

                if (toStore <= 0) return 0;

                if (action.execute()) {
                    ResourceLocation fluidId = BuiltInRegistries.FLUID.getKey(resource.getFluid());
                    contents = new CellContents(Optional.of(fluidId), contents.amount() + toStore, contents.locked());
                    setChanged();
                    if (level != null && !level.isClientSide()) {
                        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
                    }
                }

                return toStore;
            }

            @Override
            public FluidStack drain(FluidStack resource, FluidAction action) {
                if (contents.storedFluidId().isEmpty() || contents.amount() <= 0) return FluidStack.EMPTY;

                Fluid storedFluid = BuiltInRegistries.FLUID.get(contents.storedFluidId().get());
                if (!resource.getFluid().equals(storedFluid)) return FluidStack.EMPTY;

                return drain(resource.getAmount(), action);
            }

            @Override
            public FluidStack drain(int maxDrain, FluidAction action) {
                if (contents.storedFluidId().isEmpty() || contents.amount() <= 0) return FluidStack.EMPTY;

                Fluid fluid = BuiltInRegistries.FLUID.get(contents.storedFluidId().get());
                int toDrain = Math.min(maxDrain, contents.amount());

                if (toDrain <= 0) return FluidStack.EMPTY;

                if (action.execute()) {
                    contents = new CellContents(contents.storedFluidId(), contents.amount() - toDrain, contents.locked());
                    setChanged();
                    if (level != null && !level.isClientSide()) {
                        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
                    }
                }

                return new FluidStack(fluid, toDrain);
            }
        };
    }
}