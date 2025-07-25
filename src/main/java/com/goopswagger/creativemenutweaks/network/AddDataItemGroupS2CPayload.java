package com.goopswagger.creativemenutweaks.network;

import com.goopswagger.creativemenutweaks.CreativeMenuTweaks;
import com.goopswagger.creativemenutweaks.data.DataItemGroup;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record AddDataItemGroupS2CPayload(Identifier identifier, DataItemGroup dataItemGroup) implements CustomPayload {
    public static final Identifier ADD_DATA_GROUP_PAYLOAD_ID = Identifier.of(CreativeMenuTweaks.MOD_ID, "add_data_group");

    public static final CustomPayload.Id<AddDataItemGroupS2CPayload> ID = new CustomPayload.Id<>(ADD_DATA_GROUP_PAYLOAD_ID);

    public static final PacketCodec<RegistryByteBuf, AddDataItemGroupS2CPayload> CODEC = PacketCodec.tuple(
            Identifier.PACKET_CODEC, AddDataItemGroupS2CPayload::identifier,
            DataItemGroup.PACKET_CODEC, AddDataItemGroupS2CPayload::dataItemGroup,
            AddDataItemGroupS2CPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
