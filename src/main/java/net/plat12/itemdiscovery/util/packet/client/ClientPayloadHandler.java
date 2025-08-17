package net.plat12.itemdiscovery.util.packet.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.LanguageManager;
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
        public static final Map<String, String> TRANSLATION_OVERRIDES = new HashMap<>();

        public static void clear() {
            TRANSLATION_OVERRIDES.clear();
        }

        public static void mergeItemMap(Map<Item, String> map) {
            map.forEach(((item, name) -> {
                TRANSLATION_OVERRIDES.put(item.getDescriptionId(), name);
            }));
            // Reload language asynchronously to apply changes immediately
            reloadLanguageAsync();
        }

        public static void mergeEffectMap(Map<MobEffect, String> map) {
            map.forEach(((effect, name) -> {
                TRANSLATION_OVERRIDES.put(effect.getDescriptionId(), name);
            }));
            // Reload language asynchronously to apply changes immediately
            reloadLanguageAsync();
        }

        /**
         * Reloads the current language asynchronously to pick up translation changes
         */
        private static void reloadLanguageAsync() {
            Minecraft mc = Minecraft.getInstance();
            mc.execute(() -> {
                LanguageManager languageManager = mc.getLanguageManager();

                // This is the method that gets called during resource reloads
                // It only reloads language data, not all resources
                languageManager.onResourceManagerReload(mc.getResourceManager());

                // Force UI updates without full resource reload
                if (mc.screen != null) {
                    mc.screen.init(mc, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
                }

                // Update inventories to refresh item names
                if (mc.player != null) {
                    mc.player.inventoryMenu.broadcastChanges();
                }
            });
        }
    }
}
