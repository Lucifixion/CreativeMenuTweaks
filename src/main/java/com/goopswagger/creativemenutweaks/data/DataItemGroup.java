package com.goopswagger.creativemenutweaks.data;

import com.goopswagger.creativemenutweaks.util.DummyItemGroup;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.util.Identifier;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DataItemGroup {
    public static final Codec<DataItemGroup> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                            Identifier.CODEC.fieldOf("id").forGetter(DataItemGroup::id),
                            Codec.STRING.optionalFieldOf("name").forGetter(dataItemGroup -> Optional.ofNullable(dataItemGroup.name())),
                            ItemStack.CODEC.optionalFieldOf("icon").forGetter(dataItemGroup -> Optional.ofNullable(dataItemGroup.icon)),
                            Codec.BOOL.optionalFieldOf("replace").forGetter(dataItemGroup -> Optional.of(dataItemGroup.replace)),
                            ItemStack.CODEC.listOf().fieldOf("entries").forGetter(DataItemGroup::entries),
                            Identifier.CODEC.listOf().optionalFieldOf("loot_tables").forGetter(dataItemGroup -> Optional.ofNullable(dataItemGroup.lootTables)))
                    .apply(instance, DataItemGroup::new));

    Identifier id;
    String name;
    ItemStack icon;
    boolean replace;
    List<ItemStack> entries;
    List<Identifier> lootTables;

    public DataItemGroup(Identifier id, Optional<String> name, Optional<ItemStack> icon, Optional<Boolean> replace, List<ItemStack> entries, Optional<List<Identifier>> lootTables) {
        this.id = id;
        this.name = name.orElse(null);
        this.icon = icon.orElse(null);
        this.replace = replace.orElse(false);
        this.entries = new ArrayList<>(entries);
        this.lootTables = lootTables.orElse(new ArrayList<>());

        makeDummyGroup(id);
    }

    public void parseLootTable(MinecraftServer server) {
        List<ItemStack> lootTables = new ArrayList<>();
        LootContextParameterSet context = new LootContextParameterSet.Builder(server.getWorld(World.OVERWORLD)).build(LootContextTypes.EMPTY);
        for (Identifier lootTable : this.lootTables) {
            lootTables.addAll(server.getLootManager().getLootTable(lootTable).generateLoot(context));
        }
        this.entries.addAll(lootTables);
    }

    private DummyItemGroup dummyItemGroup;

    private void makeDummyGroup(Identifier id) {
        dummyItemGroup = new DummyItemGroup(id);
    }

    public DummyItemGroup getDummyItemGroup() {
        return this.dummyItemGroup;
    }

    public Identifier id() {
        return id;
    }

    public String name() {
        return name;
    }

    public Optional<String> optionalName() {
        return Optional.ofNullable(name);
    }

    public ItemStack icon() {
        return icon;
    }

    public Optional<ItemStack> optionalIcon() {
        return Optional.ofNullable(icon);
    }

    public boolean replace() {
        return replace;
    }

    public List<ItemStack> entries() {
        return entries;
    }

    public List<Identifier> lootTables() {
        return lootTables;
    }
}
