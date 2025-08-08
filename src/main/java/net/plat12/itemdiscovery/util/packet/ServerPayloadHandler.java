package net.plat12.itemdiscovery.util.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.plat12.itemdiscovery.ItemDiscovery;
import net.plat12.itemdiscovery.util.ModUtils;
import org.jetbrains.annotations.NotNull;

public class ServerPayloadHandler {


    public static void handleItemName(ItemNamePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player
                    && player.level() instanceof ServerLevel level) {
                ModUtils.setItemName(level, player, packet.item(), packet.name());
            }
        });
    }

    public record ItemNamePacket(Item item, String name) implements CustomPacketPayload {
        public static final Type<ItemNamePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath
                (ItemDiscovery.MOD_ID, "item_name_packet"));

        public static final StreamCodec<ByteBuf, ItemNamePacket> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT.map( // Item
                        BuiltInRegistries.ITEM::byId,    // int ID -> Item
                        BuiltInRegistries.ITEM::getId    // Item -> int ID
                ),
                ItemNamePacket::item,
                ByteBufCodecs.STRING_UTF8,               // String name
                ItemNamePacket::name,
                ItemNamePacket::new
        );

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

}
