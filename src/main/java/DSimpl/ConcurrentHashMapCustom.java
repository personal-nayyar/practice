package DSimpl;

import java.util.concurrent.atomic.AtomicReferenceArray;

interface ConcurrentHashMapCustom<K, V> {
    void put(K key, V value);
    V get(K key);
    void remove(K key);
}

class ConcurrentHashMapCustomImpl<K,V> implements ConcurrentHashMapCustom<K,V> {
    private static class Entry<K, V>{
        K key;
        V value;
        Entry<K, V> next;

        Entry(K key, V value){
            this.key = key;
            this.value = value;
        }
    }


    private static final int DEFAULT_CAPACITY = 16;
    private int size;
    private AtomicReferenceArray<Entry<K, V>> table; // AtomicRefenceArray for thread-safety

    ConcurrentHashMapCustomImpl(){
        table = new AtomicReferenceArray<>(DEFAULT_CAPACITY);
        size = 0;
    }

    private int getIndexFor(K key){
        return Math.abs(key.hashCode()) % DEFAULT_CAPACITY;
    }

    @Override
    public void put(K key, V value) {
        int bucketIndex = getIndexFor(key);
        Entry<K,V> first = table.get(bucketIndex);
        // CAS based insertion for first node
        if(first == null){
            Entry<K,V> newEntry = new Entry<>(key, value);
            if(table.compareAndSet(bucketIndex, null, newEntry)){
                size++;
                return;
            }
        }
        // collision, lock only the bucket for this key
        synchronized (first){
            Entry<K,V> current = first;
            while(current.next != null){
                if(current.key.equals(key)){
                    current.value = value;
                    return;
                }
                current = current.next;
            }
            current.next = new Entry<>(key, value);
            size++;
        }
    }

    @Override
    public V get(K key) {
        // No locking required for get operation, lock free read
        int bucketIndex = getIndexFor(key);
        Entry<K,V> entry = table.get(bucketIndex);
        while(entry != null){
            if(entry.key.equals(key)){
                return entry.value;
            }
            entry = entry.next;
        }
        return null;
    }

    @Override
    public void remove(K key) {
        int bucketIndex = getIndexFor(key);
        Entry<K,V> first = table.get(bucketIndex);
        // CAS based removal for first node
        if(first == null){
            return;
        }
        if(first.key.equals(key)){
            if(table.compareAndSet(bucketIndex, first, first.next)){
                size--;
                return;
            }
        }
        // collision, lock only the bucket for this key and remove node;
        Entry<K,V> prev = first;
        synchronized (first){
            Entry<K,V> entry = first.next;
            while(entry != null){
                if(entry.key.equals(key)){
                    prev.next = entry.next;
                    size--;
                    return;
                }
                prev = entry;
                entry = entry.next;
            }
        }
    }

}
