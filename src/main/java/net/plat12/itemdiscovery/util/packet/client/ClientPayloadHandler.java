package net.plat12.itemdiscovery.util.packet.client;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;
import java.util.Map;

public class ClientPayloadHandler {


    public static void handleItemNameMap(ItemNameMapPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientStorage.mergeItemMap(packet.map());
        });
    }


    public static void handleEffectNameMap(EffectNameMapPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientStorage.mergeEffectMap(packet.map());
        });
    }

    public static class ClientStorage {

        private static final Map<Item, String> itemNameMap = new HashMap<>();
        private static final Map<MobEffect, String> effectNameMap = new HashMap<>();

        public static Map<Item, String> getItemMap() {
            return itemNameMap;
        }

        public static Map<MobEffect, String> getEffectMap() {
            return effectNameMap;
        }

        public static void clearMaps() {
            itemNameMap.clear();
            effectNameMap.clear();
        }

        public static String getName(Item item) {
            return itemNameMap.getOrDefault(item, null);
        }

        public static String getName(MobEffect effect) {
            return effectNameMap.getOrDefault(effect, null);
        }

        public static void mergeItemMap(Map<Item, String> map) {
            itemNameMap.putAll(map);
        }

        public static void mergeEffectMap(Map<MobEffect, String> map) {
            effectNameMap.putAll(map);
        }
    }
}
