package net.plat12.itemdiscovery.util.packet.client;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.plat12.itemdiscovery.util.ModUtils;

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
        public static final Map<String, String> TRANSLATION_OVERRIDES = new HashMap<>();

        public static void clear() {
            TRANSLATION_OVERRIDES.clear();
        }

        public static void mergeItemMap(Map<Item, String> map) {
            map.forEach(((item, name) -> {
                TRANSLATION_OVERRIDES.put(item.getDescriptionId(), name);
            }));
            // Reload language asynchronously to apply changes immediately
            ModUtils.reloadLanguageAsync();
        }

        public static void mergeEffectMap(Map<MobEffect, String> map) {
            map.forEach(((effect, name) -> {
                TRANSLATION_OVERRIDES.put(effect.getDescriptionId(), name);
            }));
            // Reload language asynchronously to apply changes immediately
            ModUtils.reloadLanguageAsync();
        }

    }
}
