package net.plat12.itemdiscovery.event;

import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.plat12.itemdiscovery.ItemDiscovery;
import net.plat12.itemdiscovery.screen.NameItemScreen;
import net.plat12.itemdiscovery.util.ModUtils;
import net.plat12.itemdiscovery.util.packet.ClientPayloadHandler;
import net.plat12.itemdiscovery.util.savedata.ItemNamesSaveData;

@EventBusSubscriber
public class ModEvents {

    @SubscribeEvent
    public static void updatePlayerItemNamesOnJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player
                && player.level() instanceof ServerLevel level) {
            ModUtils.updateItemNames(level, player);
        }
    }

    @SubscribeEvent
    public static void clearPlayerItemNamesOnQuit(PlayerEvent.PlayerLoggedOutEvent event) {
        ClientPayloadHandler.ClientStorage.clearMap();
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        while (ItemDiscovery.NAME_CHANGE_KEY_MAPPING.get().consumeClick()) {
            Minecraft minecraft = Minecraft.getInstance();
            Player player = minecraft.player;
            if (player != null) {
                ItemStack mainHandItem = player.getMainHandItem();
                ItemStack offHandItem = player.getOffhandItem();
                Item heldItem = !mainHandItem.isEmpty() ? mainHandItem.getItem() :
                        (!offHandItem.isEmpty() ? offHandItem.getItem() : null);
                if (heldItem != null) {
                    minecraft.setScreen(new NameItemScreen(heldItem));
                }
            }

        }
    }
}
