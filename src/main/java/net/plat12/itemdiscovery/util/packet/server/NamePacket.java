package net.plat12.itemdiscovery.util.packet.server;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public sealed interface NamePacket<T> extends CustomPacketPayload
        permits ItemNamePacket, EffectNamePacket {
    T element();

    String name();
}
