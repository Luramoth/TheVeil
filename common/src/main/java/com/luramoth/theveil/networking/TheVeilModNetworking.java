package com.luramoth.theveil.networking;

import com.luramoth.theveil.TheVeilMod;
import com.luramoth.theveil.Utils.Axis;
import com.luramoth.theveil.Utils.VecUtils;
import com.luramoth.theveil.components.PendantData;
import com.luramoth.theveil.components.TheVeilModComponents;
import com.luramoth.theveil.entities.RiftEntity;
import com.luramoth.theveil.entities.TheVeilModEntities;
import com.luramoth.theveil.items.PendentItem;
import com.luramoth.theveil.items.TheVeilModItems;
import com.luramoth.theveil.worldgen.TheVeilModDimensions;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import io.wispforest.accessories.api.AccessoriesCapability;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

public class TheVeilModNetworking {
    public static final ResourceLocation PENDANT_ACTION_ID = ResourceLocation.fromNamespaceAndPath(TheVeilMod.MOD_ID, "pendant_action");

    private static void handleAction(ServerPlayer player, PendantAction action) {
        ItemStack stack = findPendant(player);

        if (stack != null && !stack.isEmpty()) {
            PendantData data = stack.getOrDefault(TheVeilModComponents.PENDENT_DATA.get(), PendantData.DEFAULT);
            List<ResourceKey<Level>> dims = data.unlockedDimensions();
            if (dims.isEmpty()) return;

            int newIndex = data.selectedIndex();
            switch (action) {
                case NEXT -> {
                    newIndex = (newIndex + 1) % dims.size();
                    player.playNotifySound(SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.PLAYERS, 0.4f, 1.6f);
                }
                case LAST -> {
                    newIndex = (newIndex - 1 + dims.size()) % dims.size();
                    player.playNotifySound(SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.PLAYERS, 0.4f, 1.3f);
                }
                case OPEN_RIFT -> {
                    if (data.outboundRiftId().isPresent())
                        return;

                    ResourceKey<Level> currentDim = player.level().dimension();
                    ResourceKey<Level> targetDim = dims.get(data.selectedIndex());

                    ResourceKey<Level> exitDim;
                    if (currentDim.equals(TheVeilModDimensions.THE_VEIL_KEY)) {
                        exitDim = targetDim;
                    } else {
                        exitDim = TheVeilModDimensions.THE_VEIL_KEY;
                    }

                    Vec3 spawnPos = player.position().add(player.getLookAngle().scale(3.5));

                    if (exitDim == null) return;

                    // spawning departure rift
                    RiftEntity departingRift = new RiftEntity(TheVeilModEntities.RIFT.get(), player.level());
                    departingRift.setDestination(exitDim);
                    departingRift.moveTo(spawnPos, (float) player.getLookAngle().y + 180, player.onGround() ? (float) -player.getLookAngle().x : 0);
                    ChunkPos outboundChunkPos = new ChunkPos(departingRift.blockPosition());
                    player.server.getLevel(currentDim).setChunkForced(outboundChunkPos.x, outboundChunkPos.z, true);
                    player.level().addFreshEntity(departingRift);

                    // spawning of exit rift
                    RiftEntity exitRift = new RiftEntity(TheVeilModEntities.RIFT.get(), player.server.getLevel(exitDim));
                    exitRift.setParentRift(departingRift);
                    exitRift.moveTo(spawnPos, (float) player.getLookAngle().y + 180, player.onGround() ? (float) -player.getLookAngle().x : 0);
                    ChunkPos exitChunkPos = new ChunkPos(exitRift.blockPosition());
                    player.server.getLevel(exitDim).setChunkForced(exitChunkPos.x, exitChunkPos.z, true);
                    player.server.getLevel(exitDim).addFreshEntity(exitRift);


                    PendantData newData = new PendantData(
                            dims,
                            data.selectedIndex(),
                            Optional.of(departingRift.getUUID()),
                            Optional.of(currentDim),
                            Optional.of(exitRift.getUUID()),
                            Optional.of(exitDim)
                    );
                    stack.set(TheVeilModComponents.PENDENT_DATA.get(), newData);

                    return;
                }
            }

            stack.set(TheVeilModComponents.PENDENT_DATA.get(), new PendantData(dims, newIndex,
                    data.outboundRiftId(), data.outboundRiftDim(),
                    data.exitRiftId(), data.exitRiftDim()));

            ResourceKey<Level> dim = dims.get(newIndex);
            String translationKey = "dimension." + dim.location().getNamespace() + "." + dim.location().getPath();
            player.displayClientMessage(Component.translatable("message.the_veil.attuned").append(Component.translatable(translationKey)), true);
        }
    }

    private static ItemStack findPendant(ServerPlayer player) {
        if (player.getMainHandItem().getItem() instanceof PendentItem) return player.getMainHandItem();
        if (player.getOffhandItem().getItem() instanceof  PendentItem) return player.getOffhandItem();

        var capability = AccessoriesCapability.get(player);
        if (capability != null) {
            var equipped = capability.getEquipped(TheVeilModItems.PENDANT.get());
            if (!equipped.isEmpty()) {
                return equipped.get(0).stack();
            }
        }
        return null;
    }

    public static void sendAction(PendantAction action) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeEnum(action);
        NetworkManager.sendToServer(PENDANT_ACTION_ID, new RegistryFriendlyByteBuf(buf, RegistryAccess.EMPTY));
    }

    public static void init() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, PENDANT_ACTION_ID, (buf, context) -> {
            PendantAction action = buf.readEnum(PendantAction.class);

            context.queue(() -> {
                handleAction((ServerPlayer) context.getPlayer(), action);
            });
        });
    }
}
