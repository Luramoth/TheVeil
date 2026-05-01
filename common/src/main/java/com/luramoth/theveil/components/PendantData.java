package com.luramoth.theveil.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.List;

public record PendantData(List<ResourceKey<Level>> unlockedDimensions) {
    public static final PendantData DEFAULT = new PendantData(List.of());

    public static final Codec<PendantData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceKey.codec(Registries.DIMENSION).listOf().fieldOf("dimensions").forGetter(PendantData::unlockedDimensions)
    ).apply(instance, PendantData::new));

    public static final StreamCodec<ByteBuf, PendantData> STREAM_CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(Registries.DIMENSION).apply(ByteBufCodecs.list()), PendantData::unlockedDimensions,
            PendantData::new
    );
}
