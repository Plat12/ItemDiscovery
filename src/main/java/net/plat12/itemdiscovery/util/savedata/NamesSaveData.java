package net.plat12.itemdiscovery.util.savedata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.plat12.itemdiscovery.ItemDiscovery;

import java.util.*;
import java.util.function.Supplier;

public abstract class NamesSaveData<T, S extends NamesSaveData<T, S>> extends SavedData {
    protected final Map<UUID, Map<T, String>> playerNames;
    private final Codec<T> elementCodec;
    private final String saveDataId;

    protected NamesSaveData(Codec<T> elementCodec, String saveDataId) {
        this(new HashMap<>(), elementCodec, saveDataId);
    }

    protected NamesSaveData(Map<UUID, Map<T, String>> playerNames, Codec<T> elementCodec, String saveDataId) {
        this.playerNames = new HashMap<>(playerNames);
        this.elementCodec = elementCodec;
        this.saveDataId = saveDataId;
    }

    protected static <S extends NamesSaveData<?, S>> SavedDataType<S> createType(String id, Supplier<S> constructor) {
        return new SavedDataType<>(
                ItemDiscovery.MOD_ID + "_" + id,
                ctx -> constructor.get(),
                ctx -> {
                    S instance = constructor.get();
                    return instance.createCodec();
                }
        );
    }

    protected static <S extends NamesSaveData<?, S>> S getOrCreate(ServerLevel level, SavedDataType<S> type) {
        return level.getServer().overworld().getDataStorage().computeIfAbsent(type);
    }

    // Now it returns exactly SavedDataType<S>
    protected final SavedDataType<S> createSavedDataType() {
        return createType(saveDataId, this::createEmpty);
    }

    protected abstract S createEmpty();

    protected abstract S createInstance(Map<UUID, Map<T, String>> playerNames);

    protected final Codec<S> createCodec() {
        Codec<NameEntry<T>> nameEntryCodec = RecordCodecBuilder.create(instance ->
                instance.group(
                        this.elementCodec.fieldOf("element").forGetter(NameEntry::element),
                        Codec.STRING.fieldOf("name").forGetter(NameEntry::name)
                ).apply(instance, NameEntry::new)
        );

        Codec<PlayerNamesEntry<T>> playerEntryCodec = RecordCodecBuilder.create(instance ->
                instance.group(
                        UUIDUtil.CODEC.fieldOf("playerId").forGetter(PlayerNamesEntry::playerId),
                        Codec.list(nameEntryCodec)
                                .xmap(
                                        list -> {
                                            Map<T, String> map = new HashMap<>();
                                            for (NameEntry<T> e : list) map.put(e.element(), e.name());
                                            return map;
                                        },
                                        map -> map.entrySet().stream()
                                                .map(en -> new NameEntry<>(en.getKey(), en.getValue()))
                                                .toList()
                                )
                                .fieldOf("elementNames")
                                .forGetter(PlayerNamesEntry::elementNames)
                ).apply(instance, PlayerNamesEntry::new)
        );

        return RecordCodecBuilder.create(instance -> instance.group(
                Codec.list(playerEntryCodec)
                        .xmap(
                                list -> {
                                    Map<UUID, Map<T, String>> map = new HashMap<>();
                                    for (PlayerNamesEntry<T> e : list) map.put(e.playerId(), e.elementNames());
                                    return map;
                                },
                                map -> map.entrySet().stream()
                                        .map(en -> new PlayerNamesEntry<>(en.getKey(), en.getValue()))
                                        .toList()
                        )
                        .fieldOf("playerElementNames")
                        .forGetter(data -> data.playerNames)
        ).apply(instance, this::createInstance));
    }

    public void put(UUID uuid, T key, String name) {
        this.playerNames.computeIfAbsent(uuid, k -> new HashMap<>()).put(key, name);
        setDirty();
    }

    public void put(Player player, T key, String name) {
        this.put(player.getUUID(), key, name);
    }

    public String getName(UUID uuid, T key) {
        return this.playerNames.getOrDefault(uuid, Map.of()).getOrDefault(key, null);
    }

    public String getName(Player player, T key) {
        return this.getName(player.getUUID(), key);
    }

    public boolean contains(UUID uuid, T key) {
        return this.playerNames.getOrDefault(uuid, Map.of()).containsKey(key);
    }

    public boolean contains(Player player, T key) {
        return this.contains(player.getUUID(), key);
    }

    public boolean remove(UUID uuid, T key) {
        Map<T, String> map = this.playerNames.get(uuid);
        if (map != null) {
            String removed = map.remove(key);
            if (map.isEmpty()) {
                this.playerNames.remove(uuid);
            }
            if (removed != null) {
                setDirty();
                return true;
            }
        }
        return false;
    }

    public boolean remove(Player player, T key) {
        return this.remove(player.getUUID(), key);
    }

    public void removePlayer(UUID uuid) {
        if (this.playerNames.remove(uuid) != null) {
            setDirty();
        }
    }

    public void removePlayer(Player player) {
        this.removePlayer(player.getUUID());
    }

    public Map<T, String> getPlayerNames(UUID uuid) {
        return new HashMap<>(this.playerNames.getOrDefault(uuid, Map.of()));
    }

    public Map<T, String> getPlayerNames(Player player) {
        return this.getPlayerNames(player.getUUID());
    }

    public Set<UUID> getPlayers() {
        return new HashSet<>(this.playerNames.keySet());
    }

    public boolean hasPlayer(UUID uuid) {
        return this.playerNames.containsKey(uuid);
    }

    public boolean hasPlayer(Player player) {
        return this.hasPlayer(player.getUUID());
    }

    public int getPlayerNameCount(UUID uuid) {
        return this.playerNames.getOrDefault(uuid, Map.of()).size();
    }

    public int getPlayerNameCount(Player player) {
        return this.getPlayerNameCount(player.getUUID());
    }

    public int getTotalNameCount() {
        return this.playerNames.values().stream()
                .mapToInt(Map::size)
                .sum();
    }

    public void clear() {
        if (!this.playerNames.isEmpty()) {
            this.playerNames.clear();
            setDirty();
        }
    }

    public boolean isEmpty() {
        return this.playerNames.isEmpty();
    }

    public Map<UUID, Map<T, String>> getAllData() {
        Map<UUID, Map<T, String>> copy = new HashMap<>();
        this.playerNames.forEach((uuid, items) -> copy.put(uuid, new HashMap<>(items)));
        return copy;
    }

    public void putAll(UUID uuid, Map<T, String> items) {
        if (items != null && !items.isEmpty()) {
            this.playerNames.computeIfAbsent(uuid, k -> new HashMap<>()).putAll(items);
            setDirty();
        }
    }

    public void putAll(Player player, Map<T, String> items) {
        this.putAll(player.getUUID(), items);
    }

    public boolean containsName(UUID uuid, String name) {
        return this.getPlayerNames(uuid).containsValue(name);
    }

    public boolean containsName(Player player, String name) {
        return this.containsName(player.getUUID(), name);
    }

    protected record NameEntry<T>(T element, String name) {
    }

    protected record PlayerNamesEntry<T>(UUID playerId, Map<T, String> elementNames) {
    }
}
