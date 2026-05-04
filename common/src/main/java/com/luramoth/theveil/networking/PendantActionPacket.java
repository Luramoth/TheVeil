package com.luramoth.theveil.networking;

import com.luramoth.theveil.TheVeilMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PendantActionPacket(PendantAction action) implements CustomPacketPayload {
    public static final Type<PendantActionPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(TheVeilMod.MOD_ID, "pendant_action")
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, PendantActionPacket> STREAM_CODEC =
            StreamCodec.composite(
                    PendantAction.STREAM_CODEC.cast(), PendantActionPacket::action,
                    PendantActionPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
