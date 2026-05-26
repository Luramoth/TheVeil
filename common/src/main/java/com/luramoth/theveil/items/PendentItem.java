package com.luramoth.theveil.items;

import com.luramoth.theveil.TheVeilMod;
import com.luramoth.theveil.components.PendantData;
import com.luramoth.theveil.components.TheVeilModComponents;
import com.luramoth.theveil.data.CatalystManager;
import com.luramoth.theveil.entities.RiftEntity;
import com.luramoth.theveil.entities.RiftState;
import io.wispforest.accessories.api.Accessory;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PendentItem extends Item implements Accessory {
    public PendentItem(Properties properties) {
        super(properties);
    }

    int ticks = 0;

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if (entity instanceof ServerPlayer serverPlayer)
            manageRifts(serverPlayer, stack);
    }

    public void manageRifts(ServerPlayer player, ItemStack stack) {
        PendantData data = stack.getOrDefault(TheVeilModComponents.PENDENT_DATA.get(), PendantData.DEFAULT);

        if (data.outboundRiftId().isEmpty() || data.exitRiftId().isEmpty()) return;

        RiftEntity outboundRift = getRift(player.server.getLevel(data.outboundRiftDim().get()),
                data.outboundRiftId().get(), data.outboundRiftDim().get());
        RiftEntity exitRift = getRift(player.server.getLevel(data.exitRiftDim().get()),
                data.exitRiftId().get(), data.exitRiftDim().get());

        if (exitRift == null || outboundRift == null) {
            if (ticks == 200) {
                stack.set(TheVeilModComponents.PENDENT_DATA.get(), data.withClearedRifts());
                return;
            }

            ticks++;
            return;
        }

        switch (outboundRift.getState()) {
            case OPENING -> {
                TheVeilMod.LOGGER.info("Rift opening");
                if (ticks == 20) {
                    outboundRift.setState(RiftState.ACTIVE);
                    exitRift.setState(RiftState.ACTIVE);
                    ticks = 0;
                    break;
                }

                if (isRiftChunkLoaded(outboundRift.blockPosition(), player.server.getLevel(outboundRift.getDestinationDim()))) {
                    ticks++;
                    return;
                }
            }
            case ACTIVE -> {
                TheVeilMod.LOGGER.info("Rift active");
                if (outboundRift.getPotentialTravelers().isEmpty()){
                    ticks++;
                    if (ticks == 40) {
                        outboundRift.setState(RiftState.CLOSING);
                        exitRift.setState(RiftState.CLOSING);
                        ticks = 0;
                    }
                    break;
                }
                ticks = 0;
            }
            case CLOSING -> {
                TheVeilMod.LOGGER.info("Rift closing");
                ticks++;
                if (ticks == 20) {
                    setForceLoadChunk(outboundRift.blockPosition(), player.server.getLevel(player.level().dimension()), false);
                    setForceLoadChunk(outboundRift.blockPosition(), player.server.getLevel(outboundRift.getDestinationDim()), false);
                    outboundRift.discard();
                    exitRift.discard();
                }
            }
        }
    }

    public RiftEntity getRift(ServerLevel level, UUID riftId, ResourceKey<Level> dim) {
        if (level != null) {
            return (RiftEntity) level.getEntity(riftId);
        }
        return null;
    }

    private boolean isRiftChunkLoaded(BlockPos pos, ServerLevel level) {
        if (level == null) return false;

        return level.isPositionEntityTicking(pos);
    }

    public void setForceLoadChunk(BlockPos pos, ServerLevel level, boolean shouldForceLoad) {
        if (level == null) return;

        ChunkPos riftChunkPos = new ChunkPos(pos);
        level.setChunkForced(riftChunkPos.x, riftChunkPos.z, shouldForceLoad);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        PendantData data = stack.getOrDefault(TheVeilModComponents.PENDENT_DATA.get(), PendantData.DEFAULT);

        tooltipComponents.add(Component.translatable("item.the_veil.pendant.description1").withStyle(ChatFormatting.GOLD));
        tooltipComponents.add(Component.translatable("item.the_veil.pendant.description2").withStyle(ChatFormatting.GOLD));

        if (!data.unlockedDimensions().isEmpty()) {
            tooltipComponents.add(Component.translatable("item.the_veil.pendant.unlocked_dimensions"));

            for (ResourceKey<Level> dim : data.unlockedDimensions()){
                String translationKey = "dimension." + dim.location().getNamespace() + "." + dim.location().getPath();

                if (dim == data.unlockedDimensions().get(data.selectedIndex())) {
                    tooltipComponents.add(Component.literal(" -> ")
                            .append(Component.translatable(translationKey)));
                } else {
                    tooltipComponents.add(Component.literal(" - ")
                            .append(Component.translatable(translationKey))
                            .withStyle(ChatFormatting.GRAY));
                }
            }
        } else {
            tooltipComponents.add(Component.translatable("item.the_veil.pendant.inert").withStyle(ChatFormatting.DARK_GRAY).withStyle(ChatFormatting.ITALIC));
        }
    }

    public static int getGemColor(ItemStack stack) {
        PendantData data = stack.get(TheVeilModComponents.PENDENT_DATA.get());
        if (data == null || data.unlockedDimensions().isEmpty()) return 0xffffff;

        ResourceKey<Level> selected = data.unlockedDimensions().get(data.selectedIndex());

        return CatalystManager.getColorFor(selected);
    }
}
