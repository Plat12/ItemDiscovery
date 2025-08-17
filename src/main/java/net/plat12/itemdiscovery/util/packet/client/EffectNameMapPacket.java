package net.plat12.itemdiscovery.util.packet.client;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.plat12.itemdiscovery.ItemDiscovery;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public record EffectNameMapPacket(Map<MobEffect, String> map) implements NameMapPacket<MobEffect> {
    public static final Type<EffectNameMapPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(
            ItemDiscovery.MOD_ID, "effect_name_map_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, EffectNameMapPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(
                    HashMap::new,
                    MobEffect.STREAM_CODEC.map(
                            Holder::value,           // Holder<MobEffect> -> MobEffect
                            BuiltInRegistries.MOB_EFFECT::wrapAsHolder  // MobEffect -> Holder<MobEffect>
                    ),
                    ByteBufCodecs.STRING_UTF8                // Value: String
            ),
            EffectNameMapPacket::map,  // Fixed: was ItemNameMapPacket::map
            EffectNameMapPacket::new   // Fixed: was ItemNameMapPacket::new
    );

    public static EffectNameMapPacket single(MobEffect effect, String name) {
        return new EffectNameMapPacket(Map.of(effect, name));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
