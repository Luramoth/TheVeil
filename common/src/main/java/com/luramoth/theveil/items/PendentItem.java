package com.luramoth.theveil.items;

import com.luramoth.theveil.TheVeilMod;
import com.luramoth.theveil.components.PendantData;
import com.luramoth.theveil.components.TheVeilModComponents;
import com.luramoth.theveil.data.CatalystManager;
import io.wispforest.accessories.api.Accessory;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PendentItem extends Item implements Accessory {
    public PendentItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        TheVeilMod.LOGGER.info("Interaction!");
        ItemStack mainStack = player.getItemInHand(InteractionHand.MAIN_HAND);
        ItemStack offStack = player.getItemInHand(InteractionHand.OFF_HAND);

        // check if the pendant is in the offhand before running pressing logic
        if (usedHand == InteractionHand.MAIN_HAND && offStack.getItem() instanceof PendentItem){
            TheVeilMod.LOGGER.info("good Interaction!");
            // check to see if mainhand item is a valid catalyst
            ResourceKey<Level> targetDim = CatalystManager.getDimensionFor(mainStack.getItem());
            if (targetDim == null) return InteractionResultHolder.pass(player.getItemInHand(usedHand));

            // grabs PendantData data component from pendant
            PendantData currentData = offStack.getOrDefault(TheVeilModComponents.PENDENT_DATA.get(), PendantData.DEFAULT);

            // makes a mutable copy of the currently unlocked dimensions
            List<ResourceKey<Level>> dims = new ArrayList<>(currentData.unlockedDimensions());

            // if dimension isn't already registered in the pendant then continue
            if (!dims.contains(targetDim)){
                // add dimension and replace old list with new one
                dims.add(targetDim);
                offStack.set(TheVeilModComponents.PENDENT_DATA.get(), new PendantData(dims));

                // decrement stack on the server
                if(!level.isClientSide){
                    mainStack.shrink(1);
                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.DEEPSLATE_HIT, SoundSource.PLAYERS, 0.5f, 1.5f);
                }
                return InteractionResultHolder.sidedSuccess(mainStack, level.isClientSide());
            }
        }

        return super.use(level, player, usedHand);
    }
}
