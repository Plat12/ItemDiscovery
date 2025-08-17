package net.plat12.itemdiscovery.util.packet.server;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.plat12.itemdiscovery.util.ModUtils;

public class ServerPayloadHandler {


    public static void handleItemName(ItemNamePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player
                    && player.level() instanceof ServerLevel level) {
                ModUtils.setItemName(level, player, packet.element(), packet.name());
            }
        });
    }

    public static void handleEffectName(EffectNamePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player
                    && player.level() instanceof ServerLevel level) {
                ModUtils.setEffectName(level, player, packet.element(), packet.name());
            }
        });
    }

}
