package net.plat12.itemdiscovery.util.packet.client;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.Map;

public sealed interface NameMapPacket<T> extends CustomPacketPayload
        permits ItemNameMapPacket, EffectNameMapPacket {
    Map<T, String> map();
}
