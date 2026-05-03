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
import net.minecraft.world.InteractionResult;
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

//    public static InteractionResult tryPressGem(Player player, ItemStack mainStack, ItemStack offStack) {
//        ItemStack pendantStack = (mainStack.getItem() instanceof PendentItem) ? mainStack :
//                (offStack.getItem() instanceof PendentItem) ? offStack: null;
//
//        if (pendantStack == null) return InteractionResult.PASS;
//
//        ItemStack gemStack = (pendantStack == mainStack) ? offStack : mainStack;
//
//        ResourceKey<Level> targetDim = CatalystManager.getDimensionFor(gemStack.getItem());
//
//        if (targetDim != null) {
//            PendantData currentData = pendantStack.getOrDefault(TheVeilModComponents.PENDENT_DATA.get(), PendantData.DEFAULT);
//            List<ResourceKey<Level>> dims = new ArrayList<>(currentData.unlockedDimensions());
//
//            if (dims.contains(targetDim)) {
//                if (!player.level().isClientSide){
//                    dims.add(targetDim);
//                    pendantStack.set(TheVeilModComponents.PENDENT_DATA.get(), new PendantData(dims));
//                    gemStack.shrink(1);
//
//                    player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
//                            SoundEvents.DEEPSLATE_HIT, SoundSource.PLAYERS, 0.5f, 1.5f);
//                }
//
//                return InteractionResult.SUCCESS;
//            }
//        }
//        return InteractionResult.PASS;
//    }
}
