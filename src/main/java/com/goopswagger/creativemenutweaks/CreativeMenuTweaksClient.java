package com.goopswagger.creativemenutweaks;

import com.google.common.collect.Maps;
import com.goopswagger.creativemenutweaks.data.DataItemGroup;
import com.goopswagger.creativemenutweaks.data.DataItemGroupManager;
import com.goopswagger.creativemenutweaks.networking.CreativeMenuTweaksPackets;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CreativeMenuTweaksClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(CreativeMenuTweaksPackets.CLEAR_DATAGROUP_MANAGER_ID, (client, handler, buf, responseSender) -> client.execute(DataItemGroupManager::clear));

        ClientPlayNetworking.registerGlobalReceiver(CreativeMenuTweaksPackets.SYNC_DATAGROUP_CATEGORY_ID, (client, handler, buf, responseSender) -> {
            Identifier id = buf.readIdentifier();
            DataItemGroup group = buf.decodeAsJson(DataItemGroup.CODEC);

            client.execute(() -> DataItemGroupManager.groupData.put(id, group));
        });

        ClientPlayNetworking.registerGlobalReceiver(CreativeMenuTweaksPackets.SYNC_DATAGROUP_ENTRIES_ID, (client, handler, buf, responseSender) -> {
            List<ItemStack> stackList = new ArrayList<>();
            Identifier id = buf.readIdentifier();
            int count = buf.readShort();
            for (int i = 0; i < count; i++) {
                stackList.add(buf.readItemStack());
            }

            client.execute(() -> DataItemGroupManager.groupData.get(id).entries.addAll(stackList));
        });
    }
}
