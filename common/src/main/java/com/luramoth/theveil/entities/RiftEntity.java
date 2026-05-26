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
    private boolean countingTicks = false;
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
            if (this.getState() == RiftState.ACTIVE && !this.isExitRift()) {
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
        }
    }

    public void setDestination(ResourceKey<Level> dim) {this.destinationDim = dim;}

    public void setParentRift(RiftEntity rift) {
        this.parentRift = rift;
        this.destinationDim = rift.level().dimension();
    }

    public boolean isExitRift() {return parentRift != null;}

    public List<Player> getPotentialTravelers() {
        return this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(10));
    }

    public ResourceKey<Level> getDestinationDim() { return this.destinationDim; }

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
