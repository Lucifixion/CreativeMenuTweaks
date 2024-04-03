package com.goopswagger.creativemenutweaks;

import com.google.common.collect.Maps;
import com.goopswagger.creativemenutweaks.data.DataItemGroup;
import com.goopswagger.creativemenutweaks.data.DataItemGroupManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public class CreativeMenuTweaksClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(CreativeMenuTweaks.SYNC_ID, (client, handler, buf, responseSender) -> {
            HashMap<Identifier, DataItemGroup> groupData = Maps.newHashMap();

            while (buf.readableBytes() > 0) {
                groupData.put(buf.readIdentifier(), buf.decodeAsJson(DataItemGroup.CODEC));
            }

            client.execute(() -> {
                DataItemGroupManager.clear();
                DataItemGroupManager.groupData.putAll(groupData);
            });
        });
    }
}
