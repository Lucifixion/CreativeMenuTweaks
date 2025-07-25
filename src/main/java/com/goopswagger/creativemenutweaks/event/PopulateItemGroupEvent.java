package com.goopswagger.creativemenutweaks.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface PopulateItemGroupEvent {
    Event<PopulateItemGroupEvent> EVENT = EventFactory.createArrayBacked(PopulateItemGroupEvent.class,
            (listeners) -> (player, group, list) -> {
                for (PopulateItemGroupEvent listener : listeners) {
                    list = listener.populate(player, group, list);
                }

                return list;
            });

    List<ItemStack> populate(PlayerEntity player, ItemGroup group, List<ItemStack> list);
}
