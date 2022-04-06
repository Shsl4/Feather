package dev.sl4sh.feather;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Lifecycle;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.*;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

public class FeatherRegistry<T> extends MutableRegistry<T> {

    class Data{

        private final int rawId;
        private final RegistryKey<T> key;
        private final RegistryEntry<T> entry;
        private final T value;

        Data(int rawId, RegistryKey<T> key, RegistryEntry<T> entry, T value) {
            this.rawId = rawId;
            this.key = key;
            this.entry = entry;
            this.value = value;
        }

    }

    private final List<Data> dataList = new ArrayList<>();
    private int nextId = 0;

    public static <S> FeatherRegistry<S> from(DynamicRegistryManager manager, RegistryKey<Registry<S>> key){

        FeatherRegistry<S> registry = new FeatherRegistry<>(key, Lifecycle.stable());

        List<RegistryEntry.Reference<S>> refs =
                manager.get(key).streamEntries().toList();

        for(RegistryEntry.Reference<S> ref : refs){

            if(ref.getKey().isEmpty()) continue;
            registry.add(ref.getKey().get(), ref.value(), Lifecycle.stable());

        }

        return registry;

    }

    public FeatherRegistry(RegistryKey<? extends Registry<T>> registryKey, Lifecycle lifecycle) {
        super(registryKey, lifecycle);
    }

    @Override
    public RegistryEntry<T> set(int rawId, RegistryKey<T> key, T value, Lifecycle lifecycle) {

        if (containsRawId(rawId)){
            throw new IllegalStateException("The registry already contains an element with the id" + rawId);
        }

        if (contains(key)){
            Feather.getLogger().error("The registry already contains this key.");
            return null;
        }

        RegistryEntry<T> entry = RegistryEntry.of(value);
        entry.setRegistry(this);

        dataList.add(new Data(rawId, key, entry, value));

        if (this.nextId <= rawId) {
            this.nextId = rawId + 1;
        }

        return entry;

    }

    public boolean removeEntry(RegistryKey<T> key){
        return dataList.removeIf(data -> data.key == key);
    }

    public boolean containsRawId(int id) {

        for (Data data : dataList){
            if (data.rawId == id){
                return true;
            }
        }

        return false;

    }

    @Override
    public RegistryEntry<T> add(RegistryKey<T> key, T entry, Lifecycle lifecycle) {
        return this.set(this.nextId, key, entry, lifecycle);
    }

    @Override
    public RegistryEntry<T> replace(OptionalInt rawId, RegistryKey<T> key, T newEntry, Lifecycle lifecycle) {
        return null;
    }

    @Override
    public boolean isEmpty() {
        return dataList.isEmpty();
    }

    @Nullable
    @Override
    public Identifier getId(T value) {

        for (Data data : dataList){

            if (data.value == value){
                return data.key.getValue();
            }

        }

        return null;
    }

    @Override
    public Optional<RegistryKey<T>> getKey(T entry) {

        for (Data data : dataList){

            if (data.entry == entry){
                return Optional.of(data.key);
            }

        }

        return Optional.empty();
    }

    @Override
    public int getRawId(@Nullable T value) {

        if (value == null) { return -1; }

        for (Data data : dataList){

            if (data.entry == value){
                return data.rawId;
            }

        }

        return -1;
    }

    @Nullable
    @Override
    public T get(int index) {

        if (index < 0 || index > dataList.size())
        {
            return null;
        }

        return dataList.get(index).value;

    }

    @Override
    public int size() {
        return dataList.size();
    }

    @Nullable
    @Override
    public T get(@Nullable RegistryKey<T> key) {

        if (key == null) { return null; }

        for (Data data : dataList){
            if (data.key.getValue().equals(key.getValue())){
                return data.value;
            }
        }

        return null;
    }

    @Nullable
    @Override
    public T get(@Nullable Identifier id) {

        if (id == null) { return null; }

        for (Data data : dataList){
            if (data.key.getValue().equals(id)){
                return data.value;
            }
        }

        return null;
    }

    @Override
    public Lifecycle getEntryLifecycle(T entry) {
        return Lifecycle.stable();
    }

    @Override
    public Lifecycle getLifecycle() {
        return Lifecycle.stable();
    }

    @Override
    public Set<Identifier> getIds() {

        Set<Identifier> ids = new HashSet<>();

        for (Data data : dataList){
            ids.add(data.key.getValue());
        }

        return ids;

    }

    @Override
    public Set<Map.Entry<RegistryKey<T>, T>> getEntrySet() {

        Map<RegistryKey<T>, T> map = new HashMap<>();

        for (Data data : dataList){
            map.put(data.key, data.value);
        }

        return map.entrySet();

    }

    @Override
    public Optional<RegistryEntry<T>> getRandom(Random random) {
        return Optional.empty();
    }

    @Override
    public boolean containsId(Identifier id) {
        for (Data data : dataList){
            if (data.key.getValue() == id){
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean contains(RegistryKey<T> key) {
        for (Data data : dataList){
            if (data.key.getValue() == key.getValue()){
                return true;
            }
        }

        return false;
    }

    @Override
    public Registry<T> freeze() {
        return null;
    }

    @Override
    public RegistryEntry<T> getOrCreateEntry(RegistryKey<T> key) {

        return getEntry(key).get();

    }

    @Override
    public RegistryEntry.Reference<T> createEntry(T value) {
        throw new IllegalStateException("Unsupported. Use add instead.");
    }

    @Override
    public Optional<RegistryEntry<T>> getEntry(int rawId) {
        return Optional.empty();
    }

    @Override
    public Optional<RegistryEntry<T>> getEntry(RegistryKey<T> key) {

        for (Data data : dataList){

            if (data.key == key){
                return Optional.of(data.entry);
            }

        }

        return Optional.empty();
    }

    @Override
    public Stream<RegistryEntry.Reference<T>> streamEntries() {

        List<RegistryEntry.Reference<T>> refs = new ArrayList<>();

        for (Data data : dataList){

            refs.add(RegistryEntry.Reference.standAlone(this, data.key));

        }

        return refs.stream();
    }

    @Override
    public Optional<RegistryEntryList.Named<T>> getEntryList(TagKey<T> tag) {
        return Optional.empty();
    }

    @Override
    public RegistryEntryList.Named<T> getOrCreateEntryList(TagKey<T> tag) {
        return null;
    }

    @Override
    public Stream<Pair<TagKey<T>, RegistryEntryList.Named<T>>> streamTagsAndEntries() {
        return null;
    }

    @Override
    public Stream<TagKey<T>> streamTags() {
        return null;
    }

    @Override
    public boolean containsTag(TagKey<T> tag) {
        return false;
    }

    @Override
    public void clearTags() {

    }

    @Override
    public void populateTags(Map<TagKey<T>, List<RegistryEntry<T>>> tagEntries) {

    }

    @NotNull
    @Override
    public Iterator<T> iterator() {

        List<T> elements = new ArrayList<>();

        for (Data data : dataList){
            elements.add(data.value);
        }

        return elements.iterator();
    }
}
