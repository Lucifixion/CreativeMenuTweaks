package com.goopswagger.creativemenutweaks.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DataItemGroupEntry {
    public static final Codec<DataItemGroupEntry> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                            Target.CODEC.fieldOf("target").forGetter(DataItemGroupEntry::target),
                            Identifier.CODEC.optionalFieldOf("anchor").forGetter(dataItemGroup -> Optional.ofNullable(dataItemGroup.anchor)),
                            ItemStack.CODEC.listOf().fieldOf("values").forGetter(DataItemGroupEntry::values))
                    .apply(instance, DataItemGroupEntry::new));

    public static final PacketCodec<RegistryByteBuf, DataItemGroupEntry> PACKET_CODEC = PacketCodec.tuple(
            Target.PACKET_CODEC, DataItemGroupEntry::target,
            PacketCodecs.optional(Identifier.PACKET_CODEC), dataItemGroup -> Optional.of(dataItemGroup.anchor),
            ItemStack.PACKET_CODEC.collect(PacketCodecs.toList()), DataItemGroupEntry::values,
            DataItemGroupEntry::new
    );

    public final Target target;

    public final Identifier anchor;
    public final List<ItemStack> values;

    public DataItemGroupEntry(Target target, Optional<Identifier> anchor, List<ItemStack> values) {
        this.target = target;
        this.anchor = anchor.orElse(null);
        this.values = values;
    }

    public void apply(List<ItemStack> stackList) {
        Target applyTarget = target;

        int index = findAnchor(stackList, anchor, target);
        if (anchor == null || index == -1)
            applyTarget = Target.TAIL;

//        for (Ingredient value : values) {
//            List<ItemStack> stacks = new ArrayList<>();
//            value.getMatchingItems().forEach(entry -> stacks.add(entry.value().getDefaultStack()));
//            switch (applyTarget) {
//                case HEAD -> stackList.addAll(0, stacks);
//                case TAIL -> stackList.addAll(stacks);
//                case BEFORE -> stackList.addAll(index, stacks);
//                case AFTER -> stackList.addAll(index + 1, stacks);
//            }
//        }

        switch (applyTarget) {
            case HEAD -> stackList.addAll(0, values());
            case TAIL -> stackList.addAll(values());
            case BEFORE -> stackList.addAll(index, values());
            case AFTER -> stackList.addAll(index + 1, values());
        }
    }

    protected int findAnchor(List<ItemStack> stackList, Identifier anchorItem, Target anchorTarget) {
        if (anchorItem != null) {
            Optional<RegistryEntry.Reference<Item>> item = Registries.ITEM.getEntry(anchorItem);

            if (item.isEmpty())
                return -1;

            int index = -1;

            if (anchorTarget != Target.AFTER) {
                for (int i = 0; i < stackList.size(); i++) {
                    if (stackList.get(i).isOf(item.get().value())) {
                        index = i;
                        break;
                    }
                }
            } else {
                for (int i = stackList.size() - 1; i >= 0; i--) {
                    if (stackList.get(i).isOf(item.get().value())) {
                        index = i;
                        break;
                    }
                }
            }

            return index;
        } else {
            return -1;
        }
    }

    public Target target() {
        return target;
    }

    public @Nullable Identifier anchor() {
        return anchor;
    }

    public List<ItemStack> values() {
        return values;
    }

    public enum Target {
        HEAD,
        TAIL,
        BEFORE,
        AFTER;

        public static final Codec<Target> CODEC = Codec.STRING.comapFlatMap(
                Target::validate, Target::toString
        );

        public static final PacketCodec<ByteBuf, Target> PACKET_CODEC = PacketCodecs.indexed(ordinal -> Target.values()[ordinal], Enum::ordinal);

        public static DataResult<Target> validate(String id) {
            return DataResult.success(Target.fromString(id));
        }

        public static Target fromString(String name) {
            return valueOf(name.toUpperCase());
        }
    }
}
