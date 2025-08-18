package net.plat12.itemdiscovery.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.network.PacketDistributor;
import net.plat12.itemdiscovery.util.packet.client.EffectNameMapPacket;
import net.plat12.itemdiscovery.util.packet.client.ItemNameMapPacket;
import net.plat12.itemdiscovery.util.savedata.EffectNamesSaveData;
import net.plat12.itemdiscovery.util.savedata.ItemNamesSaveData;

public class ModUtils {


    public static String titleCase(String string) {
        if (string == null || string.isEmpty()) {
            return string;
        }

        String[] words = string.toLowerCase().split("\\s+");
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < words.length; i++) {
            String word = words[i];


            if (i == 0 || word.length() > 2) {
                if (!word.isEmpty()) {
                    word = word.substring(0, 1).toUpperCase() + word.substring(1);
                }
            }

            result.append(word);

            if (i < words.length - 1) {
                result.append(" ");
            }
        }

        return result.toString();
    }


    public static void setItemName(ServerLevel level, ServerPlayer player, Item item, String name) {
        if (name == null || name.isEmpty()) return;
        name = titleCase(name);

        ItemNamesSaveData.getOrCreate(level).put(player, item, name);
        PacketDistributor.sendToPlayer(player, ItemNameMapPacket.single(item, name));
    }

    public static void setEffectName(ServerLevel level, ServerPlayer player, MobEffect effect, String name) {
        if (name == null || name.isEmpty()) return;
        name = titleCase(name);

        EffectNamesSaveData.getOrCreate(level).put(player, effect, name);
        PacketDistributor.sendToPlayer(player, EffectNameMapPacket.single(effect, name));
    }

    public static void updateItemNames(ServerLevel level, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player,
                new ItemNameMapPacket(
                        ItemNamesSaveData.getOrCreate(level)
                                .getPlayerNames(player)));
    }

    private static void updateEffectNames(ServerLevel level, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player,
                new EffectNameMapPacket(
                        EffectNamesSaveData.getOrCreate(level)
                                .getPlayerNames(player)));
    }


    public static void updateNames(ServerLevel level, ServerPlayer player) {
        updateItemNames(level, player);
        updateEffectNames(level, player);
    }

    public static boolean shouldDisplayCustomNames() {
        Player player = Minecraft.getInstance().player;
        return player != null && !(player.isCreative() || player.isSpectator());
    }


    /**
     * Reloads the current language asynchronously to pick up translation changes
     */
    public static void reloadLanguageAsync() {
        Minecraft mc = Minecraft.getInstance();
        mc.execute(() -> {
            LanguageManager languageManager = mc.getLanguageManager();

            languageManager.onResourceManagerReload(mc.getResourceManager());

            if (mc.screen != null) {
                mc.screen.init(mc, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
            }

            if (mc.player != null) {
                mc.player.inventoryMenu.broadcastChanges();
            }
        });
    }
}
