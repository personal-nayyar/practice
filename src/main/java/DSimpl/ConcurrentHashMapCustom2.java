package DSimpl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

interface ConcurrentHashMapCustom2 {
    void put(Object key, Object value);
    Object get(Object key);
    void remove(Object key);
}

class ConcurrentHashMapCustom2Impl implements ConcurrentHashMapCustom2 {
    private static class Segment {
        private final ReentrantLock lock = new ReentrantLock();
        private final Map<Object, Object> map = new HashMap<>();

        public void put(Object key, Object value) {
            lock.lock();
            try {
                map.put(key, value);
            } finally {
                lock.unlock();
            }
        }

        public Object get(Object key) {
            lock.lock();
            try {
                return map.get(key);
            } finally {
                lock.unlock();
            }
        }

        public void remove(Object key) {
            lock.lock();
            try {
                map.remove(key);
            } finally {
                lock.unlock();
            }
        }
    }

    private static final int DEFAULT_CAPACITY = 16;
    private final Segment[] segments;

    ConcurrentHashMapCustom2Impl(){
        this(DEFAULT_CAPACITY);
    }

    public ConcurrentHashMapCustom2Impl(int capacity) {
        this.segments = new Segment[capacity];
        for (int i = 0; i < capacity; i++) {
            segments[i] = new Segment(); // initialise each bucket segment
        }
    }

    @Override
    public void put(Object key, Object value) {
        int segmentIndex = getIndexFor(key);
        segments[segmentIndex].put(key, value);
    }

    @Override
    public Object get(Object key) {
        int segmentIndex = getIndexFor(key);
        return segments[segmentIndex].get(key);
    }

    @Override
    public void remove(Object key) {
        int segmentIndex = getIndexFor(key);
        segments[segmentIndex].remove(key);
    }

    private int getIndexFor(Object key) {
        return Math.abs(key.hashCode()) % segments.length;
    }
}

