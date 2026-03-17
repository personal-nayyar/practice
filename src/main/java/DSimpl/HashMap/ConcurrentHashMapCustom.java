package DSimpl.HashMap;

import java.util.concurrent.locks.ReentrantLock;

public interface ConcurrentHashMapCustom {
    void put(String key, String values);
    String get(String key);
    void remove(String key);
}

class ConcurrentHashMapCustomImpl implements ConcurrentHashMapCustom{
    static class Node{
        String key;
        String value;
        Node next;

        Node(String key, String value, Node next){
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    private final int DEFAULT_CAPACITY = 16;
    private final Node[] buckets;
    private final ReentrantLock[] locks;

    ConcurrentHashMapCustomImpl(){
        buckets = new Node[DEFAULT_CAPACITY];
        locks = new ReentrantLock[DEFAULT_CAPACITY];
        for (int i = 0; i < DEFAULT_CAPACITY; i++) {
            locks[i] =  new ReentrantLock();
        }
    }

    private int bucketIndex(String key){
        return Math.abs(key.hashCode()) % DEFAULT_CAPACITY;
    }

    @Override
    public void put(String key, String value) {
        int bucketIndex  = bucketIndex(key);
        Node head =  buckets[bucketIndex];
        locks[bucketIndex].lock();
        Node curr = head;
        while(curr !=  null && !key.equalsIgnoreCase(curr.key))
            curr = curr.next;

        if (curr == null) {
            buckets[bucketIndex] = new Node(key, value, head);
        }
        else{
            curr.value = value;
        }
    }

    @Override
    public String get(String key) {
        int bucketIndex =  bucketIndex(key);
        Node curr = buckets[bucketIndex];
        while (curr != null && !key.equalsIgnoreCase(curr.key))
            curr = curr.next;
        return curr == null ? null: curr.value;
    }

    @Override
    public void remove(String key) {
        int bucketIndex  = bucketIndex(key);
        Node head =  buckets[bucketIndex];
        if (key.equalsIgnoreCase(head.key))
            buckets[bucketIndex] = head.next;

        Node prev = null, curr = head;
        while (curr != null && !key.equalsIgnoreCase(curr.key)){
            prev = curr;
            curr = curr.next;
        }

        if (curr == null)
            throw new IllegalStateException("Key does not exist");
        else
            prev.next = curr.next;
    }
}
