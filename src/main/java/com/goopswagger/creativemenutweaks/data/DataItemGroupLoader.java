package com.goopswagger.creativemenutweaks.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.goopswagger.creativemenutweaks.CreativeMenuTweaks;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.Predicate;

public class DataItemGroupLoader {

    public static void init() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return new Identifier("creativemenutweaks", "itemgroups");
            }

            @Override
            public void reload(ResourceManager manager) {
                DataItemGroupManager.clear();
                Predicate<Identifier> predicate = identifier -> identifier.toString().endsWith(".json");
                manager.findResources("itemgroups", predicate).forEach((identifier, resource) -> {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream())) ) {
                        JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                        DataResult<DataItemGroup> result = DataItemGroup.CODEC.parse(JsonOps.INSTANCE, json);
                        DataItemGroup groupOutput = result.getOrThrow(false, System.out::println);
                    } catch (IOException e) {
                        CreativeMenuTweaks.LOGGER.error("Error occurred while loading itemgroup json: " + identifier.toString(), e);
                    }
                });
            }
        });

        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> {
            if (joined) {
                DataItemGroupManager.sync(player);
            }
        });

        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resource, success) -> {
            if (success) {
                server.getPlayerManager().getPlayerList().forEach(DataItemGroupManager::sync);
            }
        });
    }
}
