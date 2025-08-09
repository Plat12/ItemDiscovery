package net.plat12.itemdiscovery.util.savedata;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.Map;
import java.util.UUID;

public class EffectNamesSaveData extends NamesSaveData<MobEffect, EffectNamesSaveData> {
    public static final SavedDataType<EffectNamesSaveData> TYPE =
            new EffectNamesSaveData().createSavedDataType();
    public static final String ID = "effect_names";
    public static final Codec<MobEffect> CODEC = BuiltInRegistries.MOB_EFFECT.byNameCodec();


    public EffectNamesSaveData() {
        super(CODEC, ID);
    }

    public EffectNamesSaveData(Map<UUID, Map<MobEffect, String>> playerNames) {
        super(playerNames, CODEC, ID);
    }

    public static EffectNamesSaveData getOrCreate(ServerLevel level) {
        return getOrCreate(level, TYPE);
    }

    @Override
    protected EffectNamesSaveData createEmpty() {
        return new EffectNamesSaveData();
    }

    @Override
    protected EffectNamesSaveData createInstance(Map<UUID, Map<MobEffect, String>> playerNames) {
        return new EffectNamesSaveData(playerNames);
    }
}
