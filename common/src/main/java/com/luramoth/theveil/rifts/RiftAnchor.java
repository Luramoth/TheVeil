package com.luramoth.theveil.rifts;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public record RiftAnchor(ResourceKey<Level> dim, Vec3 pos, float yRot) {
    public static final Codec<RiftAnchor> CODEC = RecordCodecBuilder.create(i -> i.group(
            ResourceKey.codec(Registries.DIMENSION).fieldOf("dim").forGetter(RiftAnchor::dim),
            Vec3.CODEC.fieldOf("pos").forGetter(RiftAnchor::pos),
            Codec.FLOAT.fieldOf("y_rot").forGetter(RiftAnchor::yRot)
    ).apply(i, RiftAnchor::new));

    public BlockPos blockPos() {
        return BlockPos.containing(pos);
    }
}
