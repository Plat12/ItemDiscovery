package net.plat12.itemdiscovery.util.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.plat12.itemdiscovery.ItemDiscovery;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ClientPayloadHandler {


    public static void handleItemNameMap(ItemNameMapPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientStorage.mergeMap(packet.map());
        });
    }


    public record ItemNameMapPacket(Map<Item, String> map) implements CustomPacketPayload {
        public static final Type<ItemNameMapPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath
                (ItemDiscovery.MOD_ID, "item_name_map_packet"));

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


    public record EnchantmentNameMapPacket(Map<Enchantment, String> map) implements CustomPacketPayload {
        public static final Type<EnchantmentNameMapPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath
                (ItemDiscovery.MOD_ID, "enchantment_name_map_packet"));

//        public static final StreamCodec<ByteBuf, EnchantmentNameMapPacket> STREAM_CODEC = StreamCodec.composite(
//                ByteBufCodecs.map(
//                        HashMap::new,
//                        Enchantment.STREAM_CODEC,     // Key: Enchantment
//                        ByteBufCodecs.STRING_UTF8     // Value: String
//                ),
//                EnchantmentNameMapPacket::map,
//                EnchantmentNameMapPacket::new
//        );

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }





    public static class ClientStorage {

        private static final Map<Item, String> itemNameMap = new HashMap<>();

        public static Map<Item, String> getMap() {
            return itemNameMap;
        }

        public static void clearMap() {
            itemNameMap.clear();
        }

        public static String getName(Item item) {
            return itemNameMap.getOrDefault(item, null);
        }

        public static void mergeMap(Map<Item, String> map) {
            itemNameMap.putAll(map);
        }
    }
}
