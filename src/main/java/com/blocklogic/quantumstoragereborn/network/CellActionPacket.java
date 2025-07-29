package com.blocklogic.quantumstoragereborn.network;

import com.blocklogic.quantumstoragereborn.container.menu.QuantumItemCellMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CellActionPacket(ActionType actionType) implements CustomPacketPayload {

    public static final Type<CellActionPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath("quantumstoragereborn", "cell_action"));

    public static final StreamCodec<FriendlyByteBuf, CellActionPacket> STREAM_CODEC =
            StreamCodec.composite(
                    StreamCodec.of(FriendlyByteBuf::writeVarInt, FriendlyByteBuf::readVarInt),
                    CellActionPacket::typeOrdinal,
                    CellActionPacket::fromOrdinal
            );

    public enum ActionType {
        EXTRACT,
        TOGGLE_LOCK
    }

    public int typeOrdinal() {
        return actionType.ordinal();
    }

    public static CellActionPacket fromOrdinal(int ordinal) {
        return new CellActionPacket(ActionType.values()[ordinal]);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(CellActionPacket packet, IPayloadContext context) {
        if (context.player() instanceof ServerPlayer serverPlayer) {
            if (serverPlayer.containerMenu instanceof QuantumItemCellMenu menu) {
                switch (packet.actionType) {
                    case EXTRACT -> menu.extractItems();
                    case TOGGLE_LOCK -> menu.toggleLock();
                }
            }
        }
    }
}