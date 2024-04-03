package com.goopswagger.creativemenutweaks.data;

import com.google.common.collect.Maps;
import com.goopswagger.creativemenutweaks.CreativeMenuTweaks;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.impl.client.itemgroup.CreativeGuiExtensions;
import net.fabricmc.fabric.impl.client.itemgroup.FabricCreativeGuiComponents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataItemGroupManager {
    public static final HashMap<Identifier, DataItemGroup> groupData = Maps.newHashMap();

    public static List<Map.Entry<Identifier, DataItemGroup>> getModifiedGroups() {
        return groupData.entrySet().stream().filter(dataItemGroup -> Registries.ITEM_GROUP.get(dataItemGroup.getKey()) != null).toList();
    }

    public static List<Map.Entry<Identifier, DataItemGroup>> getCustomGroups() {
         return groupData.entrySet().stream().filter(dataItemGroup -> Registries.ITEM_GROUP.get(dataItemGroup.getKey()) == null).toList();
    }

    public static boolean update = false;

    public static void clear() {
        groupData.clear();
        update = true;
    }

    public static void sync(ServerPlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        groupData.forEach((identifier, dataItemGroup) -> {
            buf.writeIdentifier(identifier);
            buf.encodeAsJson(DataItemGroup.CODEC, dataItemGroup);
        });
        ServerPlayNetworking.send(player, CreativeMenuTweaks.SYNC_ID, buf);
    }
}
