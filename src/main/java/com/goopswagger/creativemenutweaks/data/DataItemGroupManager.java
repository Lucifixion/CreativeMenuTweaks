package com.goopswagger.creativemenutweaks.data;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.goopswagger.creativemenutweaks.CreativeMenuTweaks;
import com.goopswagger.creativemenutweaks.network.AddDataItemGroupS2CPayload;
import com.goopswagger.creativemenutweaks.network.ResetDataItemGroupsS2CPayload;
import com.goopswagger.creativemenutweaks.util.DummyItemGroup;
import com.goopswagger.creativemenutweaks.util.ItemGroupUtil;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.apache.commons.compress.utils.Lists;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class DataItemGroupManager {
    public static final String PATH = "itemgroup";
    public static final String PATH_SUFFIX = ".json";

    public static void init() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return Identifier.of(CreativeMenuTweaks.MOD_ID, PATH);
            }

            @Override
            public void reload(ResourceManager manager) {
                DataItemGroupManager.clear();
                Predicate<Identifier> predicate = identifier -> identifier.toString().endsWith(PATH_SUFFIX);
                manager.findResources(PATH, predicate).forEach((identifier, resource) -> {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream())) ) {
                        JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                        DataResult<DataItemGroup> result = DataItemGroup.CODEC.parse(JsonOps.INSTANCE, json);
                        DataItemGroup groupOutput = result.getOrThrow();
                        Identifier path = trimPath(identifier);
                        DataItemGroup val = dataGroups.get(path);
                        if (val != null && !groupOutput.replace) {
                            val.disable = groupOutput.disable || val.disable;

                            val.name = groupOutput.name == null ? val.name : groupOutput.name;
                            val.icon = groupOutput.icon == null ? val.icon : groupOutput.icon;

                            if (groupOutput.values != null)
                                val.values.addAll(groupOutput.values);
                        } else {
                            dataGroups.put(path, groupOutput);
                        }
                    } catch (IOException e) {
                        CreativeMenuTweaks.LOGGER.error("Error occurred while loading itemgroup json: {}", identifier.toString(), e);
                    }
                });

                dataGroups.forEach((path, dataItemGroup) -> {
                    System.out.println(path);
                    if (ItemGroupUtil.getGroup(path) == null)
                        itemGroups.add(new DummyItemGroup(path));
                });
            }
        });

        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> DataItemGroupManager.sync(player));
    }

    public static final HashMap<Identifier, DataItemGroup> dataGroups = new HashMap<>();
    public static final ArrayList<DummyItemGroup> itemGroups = new ArrayList<>();

    public static Map<Identifier, DataItemGroup> getDataGroups() {
        return dataGroups;
    }

    public static ArrayList<DummyItemGroup> getItemGroups() {
        return itemGroups;
    }

    public static boolean dirty = true;

    public static void clear() {
        dataGroups.clear();
        itemGroups.clear();

        dirty = true;
    }

    public static Identifier trimPath(Identifier identifier) {
        return Identifier.of(identifier.getNamespace(), identifier.getPath().substring(PATH.length() + 1, identifier.getPath().length() - PATH_SUFFIX.length()));
    }

    public static void sync(ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, new ResetDataItemGroupsS2CPayload(true));
        getDataGroups().forEach((identifier, dataItemGroup) -> ServerPlayNetworking.send(player, new AddDataItemGroupS2CPayload(identifier, dataItemGroup)));
    }
}
