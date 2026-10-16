package com.luramoth.theveil.entities;

import com.luramoth.theveil.rifts.RiftManager;
import com.luramoth.theveil.rifts.RiftPair;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;


import java.util.UUID;

public class RiftEntity extends Entity {
    private static final EntityDataAccessor<Integer> STATE =
            SynchedEntityData.defineId(RiftEntity.class, EntityDataSerializers.INT);

    @Nullable
    private UUID riftId;
    private boolean exit;

    public RiftEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public void bind(UUID riftid, boolean exit) {
        this.riftId = riftid;
        this.exit = exit;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(STATE, RiftState.OPENING.getValue());
    }

    @Override
    public void tick() {
        super.tick();

        if(this.level().isClientSide) {
            spawnParticles();
            return;
        }

        if (!(this.level() instanceof ServerLevel serverLevel)) return;

        RiftPair pair = riftId == null ? null : RiftManager.of(serverLevel.getServer()).get(riftId);
        if (pair == null) {
            this.discard();
            return;
        }

        if (exit || getState() != RiftState.ACTIVE) return;

        ServerLevel dest = serverLevel.getServer().getLevel(pair.exit.dim());
        if (dest == null) return;
        Vec3 target = pair.exit.pos();

        for (ServerPlayer traveller : serverLevel.getEntitiesOfClass(ServerPlayer.class, this.getBoundingBox().inflate(0.5))) {
            if (traveller.isOnPortalCooldown()) continue;
            traveller.setPortalCooldown();

            traveller.teleportTo(dest, target.x, target.y, target.z, traveller.getYRot(), traveller.getXRot());
            dest.playSound(null, target.x, target.y, target.z,
                    SoundEvents.PORTAL_TRAVEL, SoundSource.AMBIENT, 1.0f, 0.5f);
        }
    }

    private void spawnParticles() {
        for (int i = 0; i < 3; i++) {
            double offsetX = (this.random.nextDouble() - 0.5) * 2.0;
            double offsetY = this.random.nextDouble() * 3.0;
            double offsetZ = (this.random.nextDouble() -0.5) * 2.0;
            this.level().addParticle(ParticleTypes.PORTAL,
                    this.getX() + offsetX, this.getY() + offsetY, this.getZ() + offsetZ,
                    0,0,0);
        }
    }

    /** never write to chunk storage, RiftManager will deal with persistence */
    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        // Intentionally empty
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        // Intentionally empty
    }

    public boolean isExitRift() {
        return exit;
    }

    public RiftState getState() {
        return RiftState.valueOf(this.entityData.get(STATE));
    }

    public void setState(RiftState state) {
        this.entityData.set(STATE, state.getValue());
    }
}
