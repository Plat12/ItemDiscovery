package net.plat12.itemdiscovery.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.plat12.itemdiscovery.util.ModUtils;
import net.plat12.itemdiscovery.util.packet.ClientPayloadHandler;

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
}
