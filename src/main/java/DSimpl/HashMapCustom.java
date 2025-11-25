package DSimpl;

interface HashMapCustom<K,V> {
    V put(K key, V value);
    V get(K key);
    V remove(K key);
}

class HashMapCustomImpl<K,V> implements HashMapCustom<K,V> {

    static class Entry<K,V> {
        final K key;
        V value;
        Entry<K,V> next;

        Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private static final int DEFAULT_CAPACITY = 16;
    private final Entry<K,V>[] table;
    private final int capacity;
    private int size;

    public HashMapCustomImpl(int capacity){
        this.capacity = capacity;
        this.table = new Entry[capacity];
    }

    HashMapCustomImpl(){
        this(DEFAULT_CAPACITY);
    }

    private int bucketIndex(K key){
        return Math.abs(key.hashCode()) % capacity;
    }

    @Override
    public V put(K key, V value) {
        int bucketIndex = bucketIndex(key);
        Entry<K,V> entry = table[bucketIndex];
        while(entry != null){
            if(entry.key.equals(key)){
                V oldValue = entry.value;
                entry.value = value;
                return oldValue;
            }
            entry = entry.next;
        }
        entry = new Entry<>(key, value);
        table[bucketIndex] = entry;
        size++;
        return value;
    }

    @Override
    public V get(K key) {
        int bucketIndex = bucketIndex(key);
        Entry<K,V> entry = table[bucketIndex];
        while(entry != null){
            if(entry.key.equals(key)){
                return entry.value;
            }
            entry = entry.next;
        }
        return null;
    }

    @Override
    public V remove(K key) {
        int bucketIndex = bucketIndex(key);
        Entry<K,V> entry = table[bucketIndex];
        Entry<K,V> prev = null;
        while(entry != null){
            if(entry.key.equals(key)){
                if(prev == null){
                    table[bucketIndex] = entry.next;
                } else {
                    prev.next = entry.next;
                }
                size--;
                return entry.value;
            }
            prev = entry;
            entry = entry.next;
        }
        return null;
    }
}
