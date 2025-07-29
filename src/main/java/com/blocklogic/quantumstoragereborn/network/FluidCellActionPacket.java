package com.blocklogic.quantumstoragereborn.network;

import com.blocklogic.quantumstoragereborn.container.menu.QuantumFluidCellMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record FluidCellActionPacket(ActionType actionType) implements CustomPacketPayload {

    public static final Type<FluidCellActionPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath("quantumstoragereborn", "fluid_cell_action"));

    public static final StreamCodec<FriendlyByteBuf, FluidCellActionPacket> STREAM_CODEC =
            StreamCodec.composite(
                    StreamCodec.of(FriendlyByteBuf::writeVarInt, FriendlyByteBuf::readVarInt),
                    FluidCellActionPacket::typeOrdinal,
                    FluidCellActionPacket::fromOrdinal
            );

    public enum ActionType {
        TOGGLE_LOCK
    }

    public int typeOrdinal() {
        return actionType.ordinal();
    }

    public static FluidCellActionPacket fromOrdinal(int ordinal) {
        return new FluidCellActionPacket(ActionType.values()[ordinal]);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(FluidCellActionPacket packet, IPayloadContext context) {
        if (context.player() instanceof ServerPlayer serverPlayer) {
            if (serverPlayer.containerMenu instanceof QuantumFluidCellMenu menu) {
                switch (packet.actionType) {
                    case TOGGLE_LOCK -> menu.toggleLock();
                }
            }
        }
    }
}