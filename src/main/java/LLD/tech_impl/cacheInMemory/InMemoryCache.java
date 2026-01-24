package LLD.tech_impl.cacheInMemory;

import java.util.*;

public class InMemoryCache {

}

// Custom Exception
class CacheException extends RuntimeException {
    public CacheException(String message) {
        super(message);
    }
}

// CacheEntry.java (Base - Abstraction for polymorphism)
abstract class CacheEntry {
    protected final String key;
    protected final String value;
    protected final long createdAt;

    protected CacheEntry(String key, String value) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
        this.key = key.trim();
        this.value = value != null ? value : "";
        this.createdAt = System.currentTimeMillis();
    }

    public String getKey() { return key; }
    public String getValue() { return value; }
    public long getCreatedAt() { return createdAt; }

    // Abstract: Polymorphism for expiration check
    public abstract boolean isExpired();
}

// ExpirableCacheEntry.java (Decorator - Structural: Adds TTL without altering base)
class ExpirableCacheEntry extends CacheEntry {
    private final long expirationTime;

    public ExpirableCacheEntry(String key, String value, long ttlMillis) {
        super(key, value);
        if (ttlMillis < 0) {
            throw new IllegalArgumentException("TTL must be non-negative");
        }
        this.expirationTime = ttlMillis > 0 ? createdAt + ttlMillis : Long.MAX_VALUE;
    }

    @Override
    public boolean isExpired() {
        return System.currentTimeMillis() > expirationTime;
    }
}

// NonExpirableCacheEntry.java (Simple extension for no TTL)
class NonExpirableCacheEntry extends CacheEntry {
    public NonExpirableCacheEntry(String key, String value) {
        super(key, value);
    }

    @Override
    public boolean isExpired() {
        return false;
    }
}

// EntryFactory.java (Factory - Creational: Centralizes entry creation)
class EntryFactory {
    public static CacheEntry createEntry(String key, String value, long ttlMillis) {
        if (ttlMillis > 0) {
            return new ExpirableCacheEntry(key, value, ttlMillis);  // Decorator for TTL
        } else {
            return new NonExpirableCacheEntry(key, value);
        }
    }
}


// EvictionStrategy.java (Interface - Behavioral: Pluggable policies)
interface EvictionStrategy {
    void evictIfNeeded(Cache cache);  // Polymorphism: Different impls
}
// LRUEvictionStrategy.java (Strategy Impl - Uses LinkedHashMap for order)

class LRUEvictionStrategy implements EvictionStrategy {
    @Override
    public void evictIfNeeded(Cache cache) {
        if (cache.getSize() >= cache.getMaxSize()) {
            // Remove eldest (LRU) - LinkedHashMap in access-order mode
            Map.Entry<String, CacheEntry> eldest = cache.getStorage().entrySet().iterator().next();
            cache.getStorage().remove(eldest.getKey());
        }
    }
}
// NoOpEvictionStrategy.java (Fallback - No eviction)
class NoOpEvictionStrategy implements EvictionStrategy {
    @Override
    public void evictIfNeeded(Cache cache) {
        // Do nothing
    }
}

class TimeBasedEvictionStrategy implements EvictionStrategy {
    @Override
    public void evictIfNeeded(Cache cache) {
        // Remove expired entries
        cache.getStorage().entrySet().removeIf(entry -> entry.getValue().isExpired());
    }
}

// Cache.java (Singleton - Creational: Global instance; uses Strategy)
class Cache {
    private static Cache instance;
    private final Map<String, CacheEntry> storage;
    private final EvictionStrategy evictionStrategy;
    private final int maxSize;

    private Cache(int maxSize, EvictionStrategy strategy) {
        this.maxSize = maxSize > 0 ? maxSize : Integer.MAX_VALUE;
        this.evictionStrategy = strategy != null ? strategy : new NoOpEvictionStrategy();
        this.storage = new LinkedHashMap<>(maxSize, 0.75f, true);  // Access-order for LRU
        // tail -> most recently used, head -> least recently used
    }

    public static Cache getInstance(int maxSize, EvictionStrategy strategy) {
        if (instance == null) {
            instance = new Cache(maxSize, strategy);
        }
        return instance;
    }

    // Internal: Put entry (evicts if needed, removes expired)
    public void put(String key, CacheEntry entry) {
        evictionStrategy.evictIfNeeded(this);  // Strategy call
        storage.put(key, entry);
        // Move to recent (LinkedHashMap handles access-order)
    }

    public Optional<String> get(String key) {
        CacheEntry entry = storage.get(key);
        if (entry != null) {
            if (entry.isExpired()) {
                storage.remove(key);  // Lazy eviction
                return Optional.empty();
            }
            // Update access time (for LRU)
            storage.get(key);  // Triggers access-order
            return Optional.of(entry.getValue());
        }
        return Optional.empty();
    }

    public void remove(String key) {
        storage.remove(key);
    }

    // Getters (Encapsulation)
    public Map<String, CacheEntry> getStorage() { return new HashMap<>(storage); }  // Defensive copy
    public int getMaxSize() { return maxSize; }
    public int getSize() { return storage.size(); }

    // Iterator for keys (Behavioral)
    public Iterator<String> keys() {
        return storage.keySet().iterator();
    }
}

// CacheManager.java (Facade - Structural: Simplifies usage)
class CacheManager {
    private final Cache cache;

    public CacheManager(int maxSize, EvictionStrategy strategy) {
        this.cache = Cache.getInstance(maxSize, strategy);
    }

    // Facade: Set (uses Factory + Singleton)
    public void set(String key, String value, long ttlMillis) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        CacheEntry entry = EntryFactory.createEntry(key, value, ttlMillis);
        cache.put(key, entry);
    }

    // Facade: Get (handles expiration via core)
    public Optional<String> get(String key) {
        return cache.get(key);
    }

    // Facade: Delete
    public void delete(String key) {
        cache.remove(key);
    }

    // Facade: Size
    public int size() {
        return cache.getSize();
    }

    // Facade: Keys (uses Iterator)
    public Iterator<String> keys() {
        return cache.keys();
    }
}

class CacheDemo {
    public static void main(String[] args) {
        // Setup: Singleton via Facade, with LRU Strategy
        EvictionStrategy lru = new LRUEvictionStrategy();
        CacheManager cache = new CacheManager(3, lru);  // Max 3 entries

        // Crucial ops
        cache.set("key1", "value1", 0);  // No TTL
        cache.set("key2", "value2", 5000);  // 5s TTL
        System.out.println("Get key1: " + cache.get("key1"));  // Optional[value1]

        cache.set("key3", "value3", 0);
        cache.set("key4", "value4", 0);  // Triggers LRU eviction of key1

        System.out.println("Size: " + cache.size());  // 3
        System.out.println("Keys: ");
        Iterator<String> it = cache.keys();  // Iterator
        while (it.hasNext()) {
            System.out.println(it.next());  // key2, key3, key4 (LRU order)
        }

        // Expiration: Assume time passes >5s for key2
        // cache.get("key2") would return empty and evict

        cache.delete("key3");
        System.out.println("Get key1 after eviction: " + cache.get("key1"));  // empty
    }
}




