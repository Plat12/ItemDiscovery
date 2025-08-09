package net.plat12.itemdiscovery.util.savedata;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.Map;
import java.util.UUID;

public class EnchantmentNamesSaveData extends NamesSaveData<Enchantment, EnchantmentNamesSaveData> {
    public static final SavedDataType<EnchantmentNamesSaveData> TYPE =
            new EnchantmentNamesSaveData().createSavedDataType();
    public static final String ID = "enchantment_names";
    public static final Codec<Enchantment> CODEC = Enchantment.DIRECT_CODEC;


    public EnchantmentNamesSaveData() {
        super(CODEC, ID);
    }

    public EnchantmentNamesSaveData(Map<UUID, Map<Enchantment, String>> playerNames) {
        super(playerNames, CODEC, ID);
    }

    public static EnchantmentNamesSaveData getOrCreate(ServerLevel level) {
        return getOrCreate(level, TYPE);
    }

    @Override
    protected EnchantmentNamesSaveData createEmpty() {
        return new EnchantmentNamesSaveData();
    }

    @Override
    protected EnchantmentNamesSaveData createInstance(Map<UUID, Map<Enchantment, String>> playerNames) {
        return new EnchantmentNamesSaveData(playerNames);
    }
}
