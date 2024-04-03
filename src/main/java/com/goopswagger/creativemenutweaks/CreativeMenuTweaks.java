package com.goopswagger.creativemenutweaks;

import com.goopswagger.creativemenutweaks.data.DataItemGroupLoader;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreativeMenuTweaks implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("Creative Menu Tweaks");
    public static final Identifier SYNC_ID = new Identifier("creativemenutweaks", "sync");

    @Override
    public void onInitialize() {
        DataItemGroupLoader.init();
    }
}










