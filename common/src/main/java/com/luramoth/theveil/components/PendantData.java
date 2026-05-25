package com.luramoth.theveil.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public record PendantData(
        List<ResourceKey<Level>> unlockedDimensions,
        int selectedIndex,
        Optional<UUID> outboundRiftId,
        Optional<ResourceKey<Level>> outboundRiftDim,
        Optional<UUID> exitRiftId,
        Optional<ResourceKey<Level>> exitRiftDim
) {
    public static final PendantData DEFAULT = new PendantData(
            List.of(),
            0,
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty()
    );

    public static final Codec<PendantData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceKey.codec(Registries.DIMENSION).listOf().fieldOf("dimensions").forGetter(PendantData::unlockedDimensions),
            Codec.INT.optionalFieldOf("selected_index", 0).forGetter(PendantData::selectedIndex),

            UUIDUtil.CODEC.optionalFieldOf("outbound_rift_id").forGetter(PendantData::outboundRiftId),
            ResourceKey.codec(Registries.DIMENSION).optionalFieldOf("outbound_rift_dim").forGetter(PendantData::outboundRiftDim),
            UUIDUtil.CODEC.optionalFieldOf("exit_rift_id").forGetter(PendantData::exitRiftId),
            ResourceKey.codec(Registries.DIMENSION).optionalFieldOf("exit_rift_dim").forGetter(PendantData::exitRiftDim)
    ).apply(instance, PendantData::new));

    public static final StreamCodec<ByteBuf, PendantData> STREAM_CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(Registries.DIMENSION).apply(ByteBufCodecs.list()), PendantData::unlockedDimensions,
            ByteBufCodecs.VAR_INT, PendantData::selectedIndex,

            ByteBufCodecs.optional(UUIDUtil.STREAM_CODEC), PendantData::outboundRiftId,
            ByteBufCodecs.optional(ResourceKey.streamCodec(Registries.DIMENSION)).cast(), PendantData::outboundRiftDim,
            ByteBufCodecs.optional(UUIDUtil.STREAM_CODEC), PendantData::exitRiftId,
            ByteBufCodecs.optional(ResourceKey.streamCodec(Registries.DIMENSION)).cast(), PendantData::exitRiftDim,
            PendantData::new
    );

    public PendantData withClearedRifts() {
        return new PendantData(unlockedDimensions, selectedIndex,
                Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty());
    }
}
