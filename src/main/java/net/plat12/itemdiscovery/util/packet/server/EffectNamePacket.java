package net.plat12.itemdiscovery.util.packet.server;

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

public record EffectNamePacket(MobEffect element, String name) implements NamePacket<MobEffect> {
    public static final Type<EffectNamePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(
            ItemDiscovery.MOD_ID, "effect_name_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, EffectNamePacket> STREAM_CODEC = StreamCodec.composite(
            MobEffect.STREAM_CODEC.map(
                    Holder::value,                           // Holder<MobEffect> -> MobEffect
                    BuiltInRegistries.MOB_EFFECT::wrapAsHolder  // MobEffect -> Holder<MobEffect>
            ),
            EffectNamePacket::element,
            ByteBufCodecs.STRING_UTF8,                       // String name
            EffectNamePacket::name,
            EffectNamePacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
