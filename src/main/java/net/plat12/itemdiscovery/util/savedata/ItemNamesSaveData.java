package net.plat12.itemdiscovery.util.savedata;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.Map;
import java.util.UUID;

public class ItemNamesSaveData extends NamesSaveData<Item, ItemNamesSaveData> {
    public static final SavedDataType<ItemNamesSaveData> TYPE =
            new ItemNamesSaveData().createSavedDataType();
    public static final String ID = "item_names";
    public static final Codec<Item> CODEC = BuiltInRegistries.ITEM.byNameCodec();


    public ItemNamesSaveData() {
        super(CODEC, ID);
    }

    public ItemNamesSaveData(Map<UUID, Map<Item, String>> playerNames) {
        super(playerNames, CODEC, ID);
    }

    public static ItemNamesSaveData getOrCreate(ServerLevel level) {
        return getOrCreate(level, TYPE);
    }

    @Override
    protected ItemNamesSaveData createEmpty() {
        return new ItemNamesSaveData();
    }

    @Override
    protected ItemNamesSaveData createInstance(Map<UUID, Map<Item, String>> playerNames) {
        return new ItemNamesSaveData(playerNames);
    }
}
