package com.luramoth.theveil.rifts;

import com.luramoth.theveil.entities.RiftState;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;

import java.util.Objects;
import java.util.UUID;

public final class RiftPair {
    private static final Codec<RiftState> STATE_CODEC = Codec.INT.xmap(
            i -> Objects.requireNonNullElse(RiftState.valueOf(i), RiftState.CLOSING),
            RiftState::getValue);

    public static final Codec<RiftPair> CODEC = RecordCodecBuilder.create(i -> i.group(
            UUIDUtil.CODEC.fieldOf("id").forGetter(p -> p.id),
            UUIDUtil.CODEC.fieldOf("owner").forGetter(p -> p.owner),
            RiftAnchor.CODEC.fieldOf("outbound").forGetter(p -> p.outbound),
            RiftAnchor.CODEC.fieldOf("exit").forGetter(p -> p.exit),
            UUIDUtil.CODEC.fieldOf("outbound_entity").forGetter(p -> p.outboundEntityId),
            UUIDUtil.CODEC.fieldOf("exit_entity").forGetter(p -> p.exitEntityId),
            STATE_CODEC.fieldOf("state").forGetter(p -> p.state),
            Codec.INT.fieldOf("state_ticks").forGetter(p -> p.stateTicks),
            Codec.INT.fieldOf("idle_ticks").forGetter(p -> p.idleTicks)
    ).apply(i, RiftPair::new));

    public final UUID id;
    public final UUID owner;
    public final RiftAnchor outbound;
    public final RiftAnchor exit;
    public final UUID outboundEntityId;
    public final UUID exitEntityId;

    RiftState state;
    int stateTicks;
    int idleTicks;

    private RiftPair(UUID id, UUID owner, RiftAnchor outbound, RiftAnchor exit,
                     UUID outboundEntityId, UUID exitEntityId,
                     RiftState state, int stateTicks, int idleTicks) {
        this.id = id;
        this.owner = owner;
        this.outbound = outbound;
        this.exit = exit;
        this.outboundEntityId = outboundEntityId;
        this.exitEntityId = exitEntityId;
        this.state = state;
        this.stateTicks = stateTicks;
        this.idleTicks = idleTicks;
    }

    static RiftPair create(UUID owner, RiftAnchor outbound, RiftAnchor exit) {
        return new RiftPair(UUID.randomUUID(), owner, outbound, exit,
                UUID.randomUUID(), UUID.randomUUID(),
                RiftState.OPENING, 0, 0);
    }

    public RiftState state() {
        return state;
    }

    void setState(RiftState newState) {
        this.state = newState;
        this.stateTicks = 0;
        this.idleTicks = 0;
    }
}
