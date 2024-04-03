package com.goopswagger.creativemenutweaks.data;

import com.goopswagger.creativemenutweaks.util.DummyItemGroup;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.*;

public class DataItemGroup {
    public static final Codec<DataItemGroup> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                            Identifier.CODEC.fieldOf("id").forGetter(DataItemGroup::id),
                            Codec.STRING.optionalFieldOf("name").forGetter(dataItemGroup -> Optional.ofNullable(dataItemGroup.name())),
                            ItemStack.CODEC.optionalFieldOf("icon").forGetter(dataItemGroup -> Optional.ofNullable(dataItemGroup.icon)),
                            Codec.BOOL.fieldOf("replace").forGetter(DataItemGroup::replace),
                            ItemStack.CODEC.listOf().fieldOf("entries").forGetter(DataItemGroup::entries))
                    .apply(instance, DataItemGroup::new));

    Identifier id;
    String name;
    ItemStack icon;
    boolean replace;
    List<ItemStack> entries;

    public DataItemGroup(Identifier id, Optional<String> name, Optional<ItemStack> icon, boolean replace, List<ItemStack> entries) {
        this.id = id;
        this.name = name.orElse(null);
        this.icon = icon.orElse(null);
        this.replace = replace;
        this.entries = new ArrayList<>(entries);

        makeDummyGroup(id);
        DataItemGroupManager.groupData.put(id, this);
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
}
