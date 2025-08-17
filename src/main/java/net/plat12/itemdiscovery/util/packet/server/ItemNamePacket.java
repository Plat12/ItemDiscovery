package net.plat12.itemdiscovery.util.packet.server;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.plat12.itemdiscovery.ItemDiscovery;
import org.jetbrains.annotations.NotNull;

public record ItemNamePacket(Item element, String name) implements NamePacket<Item> {
    public static final Type<ItemNamePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath
            (ItemDiscovery.MOD_ID, "item_name_packet"));

    public static final StreamCodec<ByteBuf, ItemNamePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT.map( // Item
                    BuiltInRegistries.ITEM::byId,    // int ID -> Item
                    BuiltInRegistries.ITEM::getId    // Item -> int ID
            ),
            ItemNamePacket::element,
            ByteBufCodecs.STRING_UTF8,               // String name
            ItemNamePacket::name,
            ItemNamePacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }


}
