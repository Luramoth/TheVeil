package com.luramoth.theveil.rifts;

import com.luramoth.theveil.TheVeilMod;
import com.luramoth.theveil.entities.RiftEntity;
import com.luramoth.theveil.entities.RiftState;
import com.luramoth.theveil.entities.TheVeilModEntities;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class RiftManager extends SavedData {
    private static final String DATA_NAME = TheVeilMod.MOD_ID + "_rifts";

    private static final int OPEN_TICKS = 20;
    private static final int IDLE_TICKS_BEFORE_CLOSE = 40;
    private static final int CLOSE_TICKS = 20;
    private static final double PRESENCE_RADIUS_SQR = 10.0 * 10.0;

    private final Map<UUID, RiftPair> rifts = new HashMap<>();

    public static void init() {
        TickEvent.SERVER_POST.register(server -> of(server).tick(server));
    }

    public static RiftManager of(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(RiftManager::new, RiftManager::load, null),
                DATA_NAME);
    }

    //------------------------------------------public API

    public RiftPair create(UUID owner, RiftAnchor outbound, RiftAnchor exit) {
        RiftPair pair = RiftPair.create(owner, outbound, exit);
        rifts.put(pair.id, pair);
        setDirty();
        return pair;
    }

    @Nullable
    public RiftPair get(UUID riftId) {
        return rifts.get(riftId);
    }

    public void close(UUID riftId) {
        RiftPair pair = rifts.get(riftId);
        if (pair != null && pair.state != RiftState.CLOSING) {
            pair.setState(RiftState.CLOSING);
            setDirty();
        }
    }

    //------------------------------------------ticking

    private void tick(MinecraftServer server) {
        if (rifts.isEmpty()) return;

        Iterator<RiftPair> it = rifts.values().iterator();
        while (it.hasNext()) {
            RiftPair pair = it.next();

            ServerLevel outLevel = server.getLevel(pair.outbound.dim());
            ServerLevel exitlevel = server.getLevel(pair.exit.dim());
            if (outLevel == null || exitlevel == null) {
                it.remove();
                continue;
            }

            RiftEntity outEntity = materialise(outLevel, pair, false);
            RiftEntity exitEntity = materialise(exitlevel, pair, true);

            boolean finished = advance(pair, outLevel, exitlevel);
            if (finished) {
                if (outEntity != null) outEntity.discard();
                if (exitEntity != null) exitEntity.discard();
                it.remove();
            } else {
                if (outEntity != null) outEntity.setState(pair.state);
                if (exitEntity != null) exitEntity.setState(pair.state);
            }
        }

        setDirty();
    }

    private static boolean advance(RiftPair pair, ServerLevel outLevel, ServerLevel exitLevel) {
        pair.stateTicks++;

        switch (pair.state) {
            case OPENING -> {
                if (pair.stateTicks >= OPEN_TICKS) pair.setState(RiftState.ACTIVE);
            }
            case ACTIVE -> {
                boolean occupied = anyPlayerNear(outLevel, pair.outbound.pos())
                        || anyPlayerNear(exitLevel, pair.exit.pos());
                if (occupied) {
                    pair.idleTicks = 0;
                } else if (++pair.idleTicks >= IDLE_TICKS_BEFORE_CLOSE) {
                    pair.setState(RiftState.CLOSING);
                }
            }
            case CLOSING -> {
                return pair.stateTicks >= CLOSE_TICKS;
            }
        }
        return false;
    }

    private static boolean anyPlayerNear(ServerLevel level, Vec3 pos) {
        for (ServerPlayer player : level.players()) {
            if (player.distanceToSqr(pos) <= PRESENCE_RADIUS_SQR) return true;
        }
        return false;
    }

    @Nullable
    private static RiftEntity materialise(ServerLevel level, RiftPair pair, boolean exit) {
        RiftAnchor anchor = exit ? pair.exit : pair.outbound;
        if (!level.isPositionEntityTicking(anchor.blockPos())) return null;

        UUID entityId = exit ? pair.exitEntityId : pair.outboundEntityId;
        if (level.getEntity(entityId) instanceof RiftEntity existing) return existing;

        RiftEntity rift = new RiftEntity(TheVeilModEntities.RIFT.get(), level);
        rift.setUUID(entityId);
        rift.bind(pair.id, exit);
        rift.moveTo(anchor.pos().x, anchor.pos().y, anchor.pos().z, anchor.yRot(), 0.0f);
        rift.setState(pair.state);
        return level.addFreshEntity(rift) ? rift : null;
    }

    //------------------------------------------persistence

    private static RiftManager load(CompoundTag tag, HolderLookup.Provider registries) {
        RiftManager manager = new RiftManager();
        if (tag.contains("rifts")) {
            RiftPair.CODEC.listOf()
                    .parse(NbtOps.INSTANCE, tag.get("rifts"))
                    .resultOrPartial(err -> TheVeilMod.LOGGER.error("Failed to load rifts: {}", err))
                    .ifPresent(list -> list.forEach(pair -> manager.rifts.put(pair.id, pair)));
        }
        return manager;
    }
    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        RiftPair.CODEC.listOf()
                .encodeStart(NbtOps.INSTANCE, List.copyOf(rifts.values()))
                .resultOrPartial(err -> TheVeilMod.LOGGER.error("Failed to save rifts: {}", err))
                .ifPresent(encoded -> tag.put("rifts", encoded));
        return tag;
    }
}
