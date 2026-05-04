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

public record PendantData(List<ResourceKey<Level>> unlockedDimensions, int selectedIndex) {
    public static final PendantData DEFAULT = new PendantData(List.of(), 0);

    public static final Codec<PendantData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceKey.codec(Registries.DIMENSION).listOf().fieldOf("dimensions").forGetter(PendantData::unlockedDimensions),
            Codec.INT.optionalFieldOf("selected_index", 0).forGetter(PendantData::selectedIndex)
    ).apply(instance, PendantData::new));

    public static final StreamCodec<ByteBuf, PendantData> STREAM_CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(Registries.DIMENSION).apply(ByteBufCodecs.list()), PendantData::unlockedDimensions,
            ByteBufCodecs.VAR_INT, PendantData::selectedIndex,
            PendantData::new
    );
}
