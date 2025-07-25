package com.goopswagger.creativemenutweaks.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.text.Text;

public interface GetItemGroupNameEvent {
    Event<GetItemGroupNameEvent> EVENT = EventFactory.createArrayBacked(GetItemGroupNameEvent.class,
            (listeners) -> (player, group, text) -> {
                for (GetItemGroupNameEvent listener : listeners) {
                    text = listener.getName(player, group, text);
                }

                return text;
            });

    Text getName(PlayerEntity player, ItemGroup group, Text text);
}
