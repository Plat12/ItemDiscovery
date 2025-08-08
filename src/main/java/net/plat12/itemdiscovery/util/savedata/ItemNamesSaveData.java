package net.plat12.itemdiscovery.util.savedata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.plat12.itemdiscovery.ItemDiscovery;

import java.util.*;

public class ItemNamesSaveData extends SavedData {

    public static final SavedDataType<ItemNamesSaveData> TYPE = new SavedDataType<>(
            ItemDiscovery.MOD_ID + "_item_names",
            ctx -> new ItemNamesSaveData(),
            ctx -> {

                Codec<ItemNameEntry> itemEntryCodec = RecordCodecBuilder.create(instance ->
                        instance.group(
                                BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(ItemNameEntry::item),
                                Codec.STRING.fieldOf("name").forGetter(ItemNameEntry::name)
                        ).apply(instance, ItemNameEntry::new)
                );


                Codec<PlayerItemNamesEntry> playerEntryCodec = RecordCodecBuilder.create(instance ->
                        instance.group(
                                UUIDUtil.CODEC.fieldOf("playerId").forGetter(PlayerItemNamesEntry::playerId),
                                Codec.list(itemEntryCodec)
                                        .xmap(
                                                list -> {
                                                    Map<Item, String> map = new HashMap<>();
                                                    for (ItemNameEntry entry : list) {
                                                        map.put(entry.item(), entry.name());
                                                    }
                                                    return map;
                                                },
                                                map -> {
                                                    List<ItemNameEntry> list = new ArrayList<>();
                                                    for (Map.Entry<Item, String> entry : map.entrySet()) {
                                                        list.add(new ItemNameEntry(entry.getKey(), entry.getValue()));
                                                    }
                                                    return list;
                                                }
                                        )
                                        .fieldOf("itemNames")
                                        .forGetter(PlayerItemNamesEntry::itemNames)
                        ).apply(instance, PlayerItemNamesEntry::new)
                );

                return RecordCodecBuilder.create(instance -> instance.group(
                        Codec.list(playerEntryCodec)
                                .xmap(
                                        list -> {
                                            Map<UUID, Map<Item, String>> map = new HashMap<>();
                                            for (PlayerItemNamesEntry entry : list) {
                                                map.put(entry.playerId(), entry.itemNames());
                                            }
                                            return map;
                                        },
                                        map -> {
                                            List<PlayerItemNamesEntry> list = new ArrayList<>();
                                            for (Map.Entry<UUID, Map<Item, String>> entry : map.entrySet()) {
                                                list.add(new PlayerItemNamesEntry(entry.getKey(), entry.getValue()));
                                            }
                                            return list;
                                        }
                                )
                                .fieldOf("playerItemNames")
                                .forGetter(data -> data.playerItemNames)
                ).apply(instance, ItemNamesSaveData::new));
            }
    );
    private final Map<UUID, Map<Item, String>> playerItemNames;


    private ItemNamesSaveData() {
        this(new HashMap<>());
    }


    private ItemNamesSaveData(Map<UUID, Map<Item, String>> playerItemNames) {
        this.playerItemNames = new HashMap<>(playerItemNames);
    }

    public static ItemNamesSaveData getOrCreate(ServerLevel level) {
        return level.getServer().overworld().getDataStorage().computeIfAbsent(TYPE);
    }

    public void put(UUID uuid, Item item, String name) {
        this.playerItemNames.computeIfAbsent(uuid, k -> new HashMap<>()).put(item, name);
        setDirty();
    }

    public void put(Player player, Item item, String name) {
        this.put(player.getUUID(), item, name);
    }

    public String getName(UUID uuid, Item item) {
        return this.playerItemNames.getOrDefault(uuid, Map.of()).getOrDefault(item, null);
    }

    public String getName(Player player, Item item) {
        return this.getName(player.getUUID(), item);
    }

    public String getName(Player player, ItemStack stack) {
        return this.getName(player, stack.getItem());
    }

    public boolean contains(UUID uuid, Item item) {
        return this.playerItemNames.getOrDefault(uuid, Map.of()).containsKey(item);
    }

    public boolean contains(Player player, Item item) {
        return this.contains(player.getUUID(), item);
    }

    public boolean remove(UUID uuid, Item item) {
        Map<Item, String> playerItems = this.playerItemNames.get(uuid);
        if (playerItems != null) {
            String removed = playerItems.remove(item);
            // Clean up empty player entries
            if (playerItems.isEmpty()) {
                this.playerItemNames.remove(uuid);
            }
            if (removed != null) {
                setDirty();
                return true;
            }
        }
        return false;
    }

    public boolean remove(Player player, Item item) {
        return this.remove(player.getUUID(), item);
    }

    public void removePlayer(UUID uuid) {
        if (this.playerItemNames.remove(uuid) != null) {
            setDirty();
        }
    }

    public void removePlayer(Player player) {
        this.removePlayer(player.getUUID());
    }

    public Map<Item, String> getPlayerItems(UUID uuid) {
        return new HashMap<>(this.playerItemNames.getOrDefault(uuid, Map.of()));
    }

    public Map<Item, String> getPlayerItems(Player player) {
        return this.getPlayerItems(player.getUUID());
    }

    public Set<UUID> getPlayers() {
        return new HashSet<>(this.playerItemNames.keySet());
    }

    public boolean hasPlayer(UUID uuid) {
        return this.playerItemNames.containsKey(uuid);
    }

    public boolean hasPlayer(Player player) {
        return this.hasPlayer(player.getUUID());
    }

    public int getPlayerItemCount(UUID uuid) {
        return this.playerItemNames.getOrDefault(uuid, Map.of()).size();
    }

    public int getPlayerItemCount(Player player) {
        return this.getPlayerItemCount(player.getUUID());
    }

    public int getTotalItemCount() {
        return this.playerItemNames.values().stream()
                .mapToInt(Map::size)
                .sum();
    }

    public void clear() {
        if (!this.playerItemNames.isEmpty()) {
            this.playerItemNames.clear();
            setDirty();
        }
    }

    public boolean isEmpty() {
        return this.playerItemNames.isEmpty();
    }

    public Map<UUID, Map<Item, String>> getAllData() {
        // defensive copy
        Map<UUID, Map<Item, String>> copy = new HashMap<>();
        this.playerItemNames.forEach((uuid, items) ->
                copy.put(uuid, new HashMap<>(items))
        );
        return copy;
    }

    public void putAll(UUID uuid, Map<Item, String> items) {
        if (items != null && !items.isEmpty()) {
            this.playerItemNames.computeIfAbsent(uuid, k -> new HashMap<>()).putAll(items);
            setDirty();
        }
    }

    public void putAll(Player player, Map<Item, String> items) {
        this.putAll(player.getUUID(), items);
    }

    public boolean containsName(UUID uuid, String name) {
        return this.getPlayerItems(uuid).containsValue(name);
    }

    public boolean containsName(Player player, String name) {
        return this.containsName(player.getUUID(), name);
    }

    // helper records for serialization
    private record ItemNameEntry(Item item, String name) {
    }

    private record PlayerItemNamesEntry(UUID playerId, Map<Item, String> itemNames) {
    }

}
