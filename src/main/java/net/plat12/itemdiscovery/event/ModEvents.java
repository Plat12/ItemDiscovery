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
import net.plat12.itemdiscovery.util.packet.client.ClientPayloadHandler;

import static net.plat12.itemdiscovery.util.ModUtils.shouldDisplayCustomNames;

@EventBusSubscriber
public class ModEvents {

    @SubscribeEvent
    public static void updatePlayerNamesOnJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player
                && player.level() instanceof ServerLevel level) {
            ModUtils.updateNames(level, player);
        }
    }

    @SubscribeEvent
    public static void clearPlayerNamesOnQuit(PlayerEvent.PlayerLoggedOutEvent event) {
        ClientPayloadHandler.ClientStorage.clear();
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        while (ItemDiscovery.NAME_CHANGE_KEY_MAPPING.get().consumeClick()) {
            if (shouldDisplayCustomNames()) {
                Minecraft minecraft = Minecraft.getInstance();
                Player player = minecraft.player;
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

    @SubscribeEvent
    public static void reloadLangOnGameModeChange(PlayerEvent.PlayerChangeGameModeEvent event) {
        ModUtils.reloadLanguageAsync();
    }


}
