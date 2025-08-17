package net.plat12.itemdiscovery.util.packet.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.plat12.itemdiscovery.ItemDiscovery;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public record ItemNameMapPacket(Map<Item, String> map) implements NameMapPacket<Item> {
    public static final Type<ItemNameMapPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(
            ItemDiscovery.MOD_ID, "item_name_map_packet"));

    public static final StreamCodec<ByteBuf, ItemNameMapPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(
                    HashMap::new,
                    ByteBufCodecs.VAR_INT.map( // Key
                            BuiltInRegistries.ITEM::byId,    // int ID -> Item
                            BuiltInRegistries.ITEM::getId    // Item -> int ID
                    ),
                    ByteBufCodecs.STRING_UTF8                // Value: String
            ),
            ItemNameMapPacket::map,
            ItemNameMapPacket::new
    );

    public static ItemNameMapPacket single(Item item, String name) {
        return new ItemNameMapPacket(Map.of(item, name));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
