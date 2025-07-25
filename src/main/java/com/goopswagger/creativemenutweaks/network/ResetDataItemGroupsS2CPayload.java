package com.goopswagger.creativemenutweaks.network;

import com.goopswagger.creativemenutweaks.CreativeMenuTweaks;
import com.goopswagger.creativemenutweaks.data.DataItemGroup;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ResetDataItemGroupsS2CPayload(boolean reload) implements CustomPayload {
    public static final Identifier RESET_DATA_GROUPS_PAYLOAD_ID = Identifier.of(CreativeMenuTweaks.MOD_ID, "reset_data_groups");

    public static final Id<ResetDataItemGroupsS2CPayload> ID = new Id<>(RESET_DATA_GROUPS_PAYLOAD_ID);

    public static final PacketCodec<RegistryByteBuf, ResetDataItemGroupsS2CPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.BOOLEAN, ResetDataItemGroupsS2CPayload::reload,
            ResetDataItemGroupsS2CPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
