package com.luramoth.theveil.networking;

import com.luramoth.theveil.TheVeilMod;
import com.luramoth.theveil.components.PendantData;
import com.luramoth.theveil.components.TheVeilModComponents;
import com.luramoth.theveil.items.PendentItem;
import com.luramoth.theveil.items.TheVeilModItems;
import com.luramoth.theveil.rifts.RiftAnchor;
import com.luramoth.theveil.rifts.RiftManager;
import com.luramoth.theveil.rifts.RiftPair;
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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

public class TheVeilModNetworking {
    public static final ResourceLocation PENDANT_ACTION_ID = ResourceLocation.fromNamespaceAndPath(TheVeilMod.MOD_ID, "pendant_action");

    private static void handleAction(ServerPlayer player, PendantAction action) {
        ItemStack stack = findPendant(player);

        if (stack == null || stack.isEmpty()) return;

        PendantData data = stack.getOrDefault(TheVeilModComponents.PENDENT_DATA.get(), PendantData.DEFAULT);
        List<ResourceKey<Level>> dims = data.unlockedDimensions();
        if (dims.isEmpty()) return;

        switch (action) {
            case NEXT -> {
                select(player, stack, data, (data.selectedIndex() + 1) % dims.size());
                player.playNotifySound(SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.PLAYERS, 0.4f, 1.6f);
            }
            case LAST -> {
                select(player, stack, data, (data.selectedIndex() -1 + dims.size()) % dims.size());
                player.playNotifySound(SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.PLAYERS, 0.4f, 1.3f);
            }
            case OPEN_RIFT -> openRift(player, stack, data);
        }
    }

    private static void select(ServerPlayer player, ItemStack stack, PendantData data, int newIndex) {
        stack.set(TheVeilModComponents.PENDENT_DATA.get(), data.withSelectedIndex(newIndex));

        ResourceKey<Level> dim = data.unlockedDimensions().get(newIndex);
        String translationKey = "dimension." + dim.location().getNamespace() + "." + dim.location().getPath();
        player.displayClientMessage(Component.translatable("message.the_veil.attuned").append(Component.translatable(translationKey)),true);
    }

    private static void openRift(ServerPlayer player, ItemStack stack, PendantData data) {
        RiftManager manager = RiftManager.of(player.server);

        data.activeRiftId().ifPresent(manager::close);

        ResourceKey<Level> currentDim = player.level().dimension();
        ResourceKey<Level> exitDim = currentDim.equals(TheVeilModDimensions.THE_VEIL_KEY)
                ? data.unlockedDimensions().get(data.selectedIndex())
                : TheVeilModDimensions.THE_VEIL_KEY;

        if (player.server.getLevel(exitDim) == null) return;

        Vec3 pos = player.position().add(player.getLookAngle().scale(3.5));
        float yRot = player.getYRot() + 180.0f;

        RiftPair pair = manager.create(player.getUUID(),
                new RiftAnchor(currentDim, pos, yRot),
                new RiftAnchor(exitDim, pos, yRot));

        stack.set(TheVeilModComponents.PENDENT_DATA.get(), data.withActiveRift(pair.id));
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
        NetworkManager.sendToServer(new PendantActionPacket(action));
    }

    public static void init() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S,
                PendantActionPacket.TYPE, PendantActionPacket.STREAM_CODEC,
                (packet, context) -> context.queue(() ->
                        handleAction((ServerPlayer) context.getPlayer(), packet.action())));
    }
}
