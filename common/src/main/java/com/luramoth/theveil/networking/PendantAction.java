package com.luramoth.theveil.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public enum PendantAction {
    NEXT, LAST, OPEN_RIFT;

    private static final PendantAction[] VALUES = values();

    public static final StreamCodec<ByteBuf, PendantAction> STREAM_CODEC =
            ByteBufCodecs.idMapper(i -> VALUES[i], Enum::ordinal);
}
