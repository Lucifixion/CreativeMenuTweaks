package com.goopswagger.creativemenutweaks.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public interface GetItemGroupIconEvent {
    Event<GetItemGroupIconEvent> EVENT = EventFactory.createArrayBacked(GetItemGroupIconEvent.class,
            (listeners) -> (player, group, icon) -> {
                for (GetItemGroupIconEvent listener : listeners) {
                    icon = listener.getIcon(player, group, icon);
                }

                return icon;
            });

    ItemStack getIcon(PlayerEntity player, ItemGroup group, ItemStack icon);
}
