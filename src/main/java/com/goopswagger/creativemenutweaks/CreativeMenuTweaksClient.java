package com.goopswagger.creativemenutweaks;

import com.goopswagger.creativemenutweaks.data.DataItemGroup;
import com.goopswagger.creativemenutweaks.data.DataItemGroupEntry;
import com.goopswagger.creativemenutweaks.data.DataItemGroupManager;
import com.goopswagger.creativemenutweaks.event.AddItemGroupEvent;
import com.goopswagger.creativemenutweaks.event.GetItemGroupIconEvent;
import com.goopswagger.creativemenutweaks.event.GetItemGroupNameEvent;
import com.goopswagger.creativemenutweaks.event.PopulateItemGroupEvent;
import com.goopswagger.creativemenutweaks.network.AddDataItemGroupS2CPayload;
import com.goopswagger.creativemenutweaks.network.ResetDataItemGroupsS2CPayload;
import com.goopswagger.creativemenutweaks.util.DummyItemGroup;
import com.goopswagger.creativemenutweaks.util.ItemGroupUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.text.Text;

public class CreativeMenuTweaksClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        AddItemGroupEvent.EVENT.register((player, group) -> {
            DataItemGroup dataItemGroup = DataItemGroupManager.getDataGroups().get(ItemGroupUtil.getGroupIdentifier(group));
            return dataItemGroup == null || !dataItemGroup.disable;
        });

        PopulateItemGroupEvent.EVENT.register((player, group, list) -> {
            DataItemGroup dataItemGroup = DataItemGroupManager.getDataGroups().get(ItemGroupUtil.getGroupIdentifier(group));

            if (dataItemGroup != null) {
                if (dataItemGroup.replace) {
                    list.clear();
                }
                if (dataItemGroup.values != null) {
                    for (DataItemGroupEntry value : dataItemGroup.values) {
                        value.apply(list);
                    }
                }
            }

            return list;
        });

        GetItemGroupNameEvent.EVENT.register((player, group, name) -> {
            DataItemGroup dataItemGroup = DataItemGroupManager.getDataGroups().get(ItemGroupUtil.getGroupIdentifier(group));

            if (dataItemGroup != null && dataItemGroup.name != null) {
                return dataItemGroup.name;
            }

            return name;
        });

        GetItemGroupIconEvent.EVENT.register((player, group, icon) -> {
            DataItemGroup dataItemGroup = DataItemGroupManager.getDataGroups().get(ItemGroupUtil.getGroupIdentifier(group));

            if (dataItemGroup != null && dataItemGroup.icon != null) {
                return dataItemGroup.icon;
            }

            return icon;
        });

        ClientPlayNetworking.registerGlobalReceiver(ResetDataItemGroupsS2CPayload.ID, (payload, context) -> context.client().execute(() -> {
            if (MinecraftClient.getInstance().currentScreen instanceof CreativeInventoryScreen)
                MinecraftClient.getInstance().setScreen(null);
            DataItemGroupManager.clear();
        }));

        ClientPlayNetworking.registerGlobalReceiver(AddDataItemGroupS2CPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                if (ItemGroupUtil.getGroup(payload.identifier()) == null)
                    DataItemGroupManager.itemGroups.add(new DummyItemGroup(payload.identifier()));
                DataItemGroupManager.dataGroups.put(payload.identifier(), payload.dataItemGroup());
            });
        });
    }
}
