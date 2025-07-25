package com.goopswagger.creativemenutweaks.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;

public interface AddItemGroupEvent {
    Event<AddItemGroupEvent> EVENT = EventFactory.createArrayBacked(AddItemGroupEvent.class,
            (listeners) -> (player, group) -> {
                boolean val = true;

                for (AddItemGroupEvent listener : listeners) {
                    boolean result = listener.add(player, group);

                    if (!result) {
                        return false;
                    }
                }

                return val;
            });

    boolean add(PlayerEntity player, ItemGroup group);
}
