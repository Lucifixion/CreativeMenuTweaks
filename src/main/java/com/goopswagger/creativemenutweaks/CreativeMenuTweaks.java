package com.goopswagger.creativemenutweaks;

import com.goopswagger.creativemenutweaks.data.DataItemGroupManager;
import com.goopswagger.creativemenutweaks.network.AddDataItemGroupS2CPayload;
import com.goopswagger.creativemenutweaks.network.ResetDataItemGroupsS2CPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreativeMenuTweaks implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("Creative Menu Tweaks");
    public static final String MOD_ID = "creativemenutweaks";

    public static MinecraftServer serverInstance = null;

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playS2C().register(AddDataItemGroupS2CPayload.ID, AddDataItemGroupS2CPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ResetDataItemGroupsS2CPayload.ID, ResetDataItemGroupsS2CPayload.CODEC);

        DataItemGroupManager.init();

        ServerLifecycleEvents.SERVER_STARTED.register((server) -> serverInstance = server);
        ServerLifecycleEvents.SERVER_STOPPED.register((server) -> serverInstance = null);

        // CustomIngredientImpl.registerSerializer(LootIngredient.SERIALIZER);
    }
}










