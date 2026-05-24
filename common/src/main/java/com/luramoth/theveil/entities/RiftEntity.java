package com.luramoth.theveil.entities;

import com.luramoth.theveil.TheVeilMod;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Objects;

public class RiftEntity extends Entity {
    private static final EntityDataAccessor<Integer> STATE = SynchedEntityData.defineId(RiftEntity.class, EntityDataSerializers.INT);

    private ResourceKey<Level> destinationDim;
    private int ticks = 0;
    private boolean isDestinationLoaded = false;
    private RiftEntity parentRift;

    public RiftEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(STATE, RiftState.OPENING.getValue()); // 0 Opening, 1 Active, 2 Closing
    }

    @Override
    public void tick() {
        super.tick();

        if (this.isExitRift() && parentRift == null)
            this.setState(RiftState.CLOSING);
        else if (this.isExitRift())
            TheVeilMod.LOGGER.info("parent rift still alive");

        if (this.level().isClientSide) {
            for (int i = 0; i <3; i++) {
                double offsetX = (this.random.nextDouble() - 0.5) * 2.0;
                double offsetY = this.random.nextDouble() * 3.0;
                double offsetZ = (this.random.nextDouble() - 0.5) * 2.0;

                this.level().addParticle(
                        ParticleTypes.PORTAL,
                        this.getX() + offsetX,
                        this.getY() + offsetY,
                        this.getZ() + offsetZ,
                        0, 0, 0
                );
            }
        } else {
            ServerLevel level = (ServerLevel) this.level();

            switch (getState()) {
                case OPENING -> {
                    if (this.ticks == 20) {
                        this.setState(RiftState.ACTIVE);
                        break;
                    }

                    checkDestinationReady(level);

                    if (this.isDestinationLoaded || this.isExitRift() && this.parentRift.getState() == RiftState.OPENING)
                        this.ticks++;
                }
                case ACTIVE -> {
                    if (this.isExitRift()) {
                        if (parentRift.getState() == RiftState.CLOSING)
                            this.setState(RiftState.CLOSING);
                        break;
                    }
                    if (this.getPotentialTravelers().isEmpty()){
                        this.ticks++;
                        if (this.ticks == 40){
                            this.setState(RiftState.CLOSING);
                            this.ticks = 0;
                        }
                        break;
                    }
                    this.ticks = 0;

                    List<Player> travelingPlayers = this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(0.5f));
                    for (Player traveler : travelingPlayers) {
                        if (traveler instanceof ServerPlayer serverTraveller) {
                            ServerLevel destLevel = serverTraveller.server.getLevel(this.destinationDim);
                            if (destLevel == null) break;

                            serverTraveller.teleportTo(
                                    destLevel,
                                    this.getX(),
                                    this.getY(),
                                    this.getZ(),
                                    serverTraveller.getYRot(),
                                    serverTraveller.getXRot()
                            );
                            destLevel.playSound(
                                    null,
                                    this.getX(),
                                    this.getY(),
                                    this.getZ(),
                                    SoundEvents.PORTAL_TRAVEL,
                                    SoundSource.AMBIENT,
                                    1.0f,
                                    0.5f
                            );
                        }
                    }
                }
                case CLOSING -> {
                    this.ticks++;
                    if (this.ticks == 20)
                        this.discard();
                }
            }
        }
    }

    public void setDestination(ResourceKey<Level> dim) {this.destinationDim = dim;}

    public void setParentRift(RiftEntity rift) {
        this.parentRift = rift;
        this.destinationDim = rift.level().dimension();
    }

    public boolean isExitRift() {return parentRift != null;}

    private List<Player> getPotentialTravelers() {
        return this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(10));
    }

    private void checkDestinationReady (ServerLevel currentLevel) {
        if (destinationDim == null) return;

        ServerLevel destLevel = currentLevel.getServer().getLevel(destinationDim);
        if (destLevel != null) {
            ChunkPos chunkPos = new ChunkPos(this.blockPosition());

            destLevel.setChunkForced(chunkPos.x, chunkPos.z, true);

            if (destLevel.isPositionEntityTicking(this.blockPosition())) {
                this.isDestinationLoaded = true;
            }
        }
    }

    @Override
    public void onClientRemoval() {
        if ( !this.level().isClientSide){
            if (destinationDim == null) return;

            ServerLevel level = Objects.requireNonNull(this.level().getServer()).getLevel(destinationDim);
            ChunkPos chunkPos = new ChunkPos(this.blockPosition());

            assert level != null;
            level.setChunkForced(chunkPos.x, chunkPos.z, false);
        }
        super.onClientRemoval();
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        if (compound.contains("Destination")) {
            this.destinationDim = ResourceKey.create(
                    Registries.DIMENSION,
                    ResourceLocation.parse(compound.getString("Destination"))
            );
        }

        if (compound.contains("RiftState")) {
            this.setState(RiftState.valueOf(compound.getInt("RiftState")));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        if (this.destinationDim != null) {
            compound.putString("Destination", this.destinationDim.location().toString());
        }

        compound.putInt("RiftState", this.getState().getValue());
    }

    public RiftState getState() {return RiftState.valueOf(this.entityData.get(STATE));}
    public void setState(RiftState state) {this.entityData.set(STATE, state.getValue());}
}
