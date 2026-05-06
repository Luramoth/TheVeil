package com.luramoth.theveil.networking;

import com.luramoth.theveil.TheVeilMod;
import com.luramoth.theveil.components.PendantData;
import com.luramoth.theveil.components.TheVeilModComponents;
import com.luramoth.theveil.items.PendentItem;
import com.luramoth.theveil.items.TheVeilModItems;
import com.luramoth.theveil.worldgen.TheVeilModDimensions;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import io.wispforest.accessories.api.AccessoriesCapability;
import net.minecraft.ChatFormatting;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

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
                case NEXT -> newIndex = (newIndex + 1) % dims.size();
                case LAST -> newIndex = (newIndex - 1 + dims.size()) % dims.size();
                case OPEN_RIFT -> {
                    ResourceKey<Level> currentDim = player.level().dimension();
                    ResourceKey<Level> targetDim = dims.get(data.selectedIndex());

                    ServerLevel targetWorld;
                    if (currentDim.equals(TheVeilModDimensions.THE_VEIL_KEY)) {
                        targetWorld = player.server.getLevel(targetDim);
                    } else {
                        targetWorld = player.server.getLevel(TheVeilModDimensions.THE_VEIL_KEY);
                    }

                    if (targetWorld != null) {
                        player.teleportTo(
                                targetWorld,
                                player.getX(),
                                player.getY(),
                                player.getZ(),
                                player.getYRot(),
                                player.getXRot()
                        );
                    }

                    return;
                }
            }

            stack.set(TheVeilModComponents.PENDENT_DATA.get(), new PendantData(dims, newIndex));

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
