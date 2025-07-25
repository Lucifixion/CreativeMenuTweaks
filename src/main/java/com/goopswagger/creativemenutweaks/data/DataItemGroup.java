package com.goopswagger.creativemenutweaks.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

import java.util.List;
import java.util.Optional;

public class DataItemGroup {
    public static final Codec<DataItemGroup> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                            Codec.BOOL.optionalFieldOf("replace").forGetter(dataItemGroup -> Optional.of(dataItemGroup.replace)),
                            Codec.BOOL.optionalFieldOf("disable").forGetter(dataItemGroup -> Optional.of(dataItemGroup.disable)),
                            TextCodecs.CODEC.optionalFieldOf("name").forGetter(dataItemGroup -> Optional.ofNullable(dataItemGroup.name)),
                            ItemStack.CODEC.optionalFieldOf("icon").forGetter(dataItemGroup -> Optional.ofNullable(dataItemGroup.icon)),
                            DataItemGroupEntry.CODEC.listOf().optionalFieldOf("values").forGetter(dataItemGroup -> Optional.ofNullable(dataItemGroup.values)))
                    .apply(instance, DataItemGroup::new));

    public static final PacketCodec<RegistryByteBuf, DataItemGroup> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.optional(PacketCodecs.BOOLEAN), (dataItemGroup -> Optional.of(dataItemGroup.replace)),
            PacketCodecs.optional(PacketCodecs.BOOLEAN), (dataItemGroup -> Optional.of(dataItemGroup.disable)),
            PacketCodecs.optional(TextCodecs.PACKET_CODEC), (dataItemGroup -> Optional.ofNullable(dataItemGroup.name)),
            PacketCodecs.optional(ItemStack.PACKET_CODEC), (dataItemGroup -> Optional.ofNullable(dataItemGroup.icon)),
            PacketCodecs.optional(DataItemGroupEntry.PACKET_CODEC.collect(PacketCodecs.toList())), (dataItemGroup -> Optional.ofNullable(dataItemGroup.values)),
            DataItemGroup::new
    );

    public boolean replace;
    public boolean disable;
    public Text name;
    public ItemStack icon;
    public List<DataItemGroupEntry> values;

    public DataItemGroup(Optional<Boolean> replace, Optional<Boolean> disable, Optional<Text> name, Optional<ItemStack> icon, Optional<List<DataItemGroupEntry>> values) {
        this.replace = replace.orElse(false);
        this.disable = disable.orElse(false);
        this.name = name.orElse(null);
        this.icon = icon.orElse(null);
        this.values = values.orElse(null);
    }

    public boolean replace() {
        return replace;
    }

    public boolean disable() {
        return disable;
    }

    public Text name() {
        return name;
    }

    public ItemStack icon() {
        return icon;
    }

    public List<DataItemGroupEntry> values() {
        return values;
    }
}
