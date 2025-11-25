package code.mix;


import java.util.Deque;
import java.util.HashMap;
import java.util.Optional;

/**
 In practice, LRU cache is a kind of Queue — if an element is reaccessed, it goes to the end of the eviction order
 This queue will have a specific capacity as the cache has a limited size.
 Whenever a new element is brought in, it is added at the head of the queue.
 When eviction happens, it happens from the tail of the queue.
 Hitting/access data in the cache must be done in constant time, which isn't possible in Queue! But, it is possible with Java's HashMap data structure
 Removal of the least recently used element must be done in constant time,
    which means for the implementation of Queue, we'll use DoublyLinkedList instead of SingleLinkedList or an array.
 All operations should run in order of O(1)
 The cache has a limited size
 It's mandatory that all cache operations support concurrency
 If the cache is full, adding a new item must invoke the LRU strategy

 BP
 Build a cache data structure which implements the following eviction policy:
 1) evict an expired item if it exists
 2) otherwise find the items with the lowest priority and evict the Least Recently Used of these items

 C = new Cache(5)
 C.Set(key="A", value=5, priority=5, expiration=10001)
 C.Set(key="B", value=4, priority=1, expiration=40006)
 C.Set(key="C", value=3, priority=5, expiration=10001)
 C.Set(key="D", value=2, priority=9, expiration= 500)
 C.Set(key="E", value=1, priority=5, expiration=10004)
 C.Get("C")

 // Current Time = 900 system.get_current_time()
 C.Set(key="F", value=10, priority=5, expiration= 5001)
 C.Set(key="G", value=9, priority=5, expiration= 5004)
 C.Set(key="H", value=-1, priority=5, expiration= 5009)
 C.Set(key="I", value=1, priority=5, expiration= 5011)
 C.Set(key="C", value=1, priority=5, expiration= 5021)
 C.Get("D") // throw Error

 <C,5,5021>,<I,5,5001>,<H,5,5009>,<G,5,5001>,<F,5,5001>,<E,5,10004>,<B,1,40006>,<A,5,10001>

  <C,5,10001>,<E,5,10004> <F,5,5001>, <A,5,10001>, <B,1,40006> |  <G, 5,5004>



 (key="G", value=9, priority=5, expiration= 5004)

 HashMap<key,Payload> O(1) tells if element exist in cache or not
 A -> 5
 B -> 4
 C -> 3
 E -> 1
 F -> 10

 <Deque> cache_elem
 5-> (key="F", value=10, priority=5, expiration= 5001), (key="C", value=3, priority=5, expiration=10001), (key="A", value=5, priority=5, expiration=10001) (key="E", value=1, priority=5, expiration=10004)
 1-> (key="B", value=4, priority=1, expiration=40006)
 9->

 */

class Payload{
    String key;
    String value;
    int priority;
    long expiration;

    public Payload(String key, String value, int priority, long expiration) {
        this.key = key;
        this.value = value;
        this.priority = priority;
        this.expiration = expiration;
    }
}

interface CacheBP{
    int DEFAULT_CAPACITY = 5;
    int MAX_PRIORITY = 10;
    Optional<String> get(String key);
    void set(String key, String value, int priority, long expiration);
}

class CacheBPImpl implements CacheBP{
    HashMap<String, Payload> cache;
    Deque<Payload>[] cache_elem;
    CacheBPImpl(){
        cache =  new HashMap<>(DEFAULT_CAPACITY);
        cache_elem = new Deque[MAX_PRIORITY];
    }

    @Override
    public Optional<String> get(String key) {
        if(!cache.containsKey(key))
            return Optional.empty();
        else {
            Payload p =  cache.get(key);
            Deque<Payload> deque = cache_elem[p.priority];
            deque.remove(p);
            deque.addFirst(p);
            return Optional.of(p.value);
        }
    }

    @Override
    public void set(String key, String value, int priority, long expiration) {
        Payload p =  new Payload(key,value, priority, expiration);
        if(!cache.containsKey(key)){
            if(cache.size() == DEFAULT_CAPACITY){ // need to remove the lowest priority and LRU elem
                for (int i = 0; i < MAX_PRIORITY; i++) {
                    if(cache_elem[i] != null){
                        Payload lruWithMinPriority = cache_elem[i].removeLast();
                        cache.remove(lruWithMinPriority.key);
                    }
                }
            }
            cache.put(key, p);
            cache_elem[p.priority].addFirst(p);
        }else{
            Deque<Payload> deque = cache_elem[p.priority];
            deque.remove(p);
            deque.addFirst(p);
        }
    }

    class CleanerThread extends Thread{
        @Override
        public void run() {
            cache.entrySet().forEach(entry->{
                String key = entry.getKey();
                Payload payload =  entry.getValue();
                if(payload.expiration < System.currentTimeMillis()){
                    cache.remove(key);
                    cache_elem[payload.priority].remove(payload);
                }
            });
        }
    }
}





