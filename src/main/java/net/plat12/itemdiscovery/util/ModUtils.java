package net.plat12.itemdiscovery.util;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.network.PacketDistributor;
import net.plat12.itemdiscovery.util.packet.ClientPayloadHandler;
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
        PacketDistributor.sendToPlayer(player, ClientPayloadHandler.ItemNameMapPacket.single(item, name));
    }

    public static void updateItemNames(ServerLevel level, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player,
                new ClientPayloadHandler.ItemNameMapPacket(
                        ItemNamesSaveData.getOrCreate(level)
                                .getPlayerNames(player)));
    }


}
