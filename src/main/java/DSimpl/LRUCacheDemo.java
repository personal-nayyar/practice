package DSimpl;

import java.util.*;

// Entry of doubly linked list
class Node {
    int key, value;
    Node prev, next;

    Node(int key, int value) {
        this.key = key;
        this.value = value;
    }
}

class LRUCache {
    private final int capacity;
    private final Map<Integer, Node> map;   // Stores key → Entry for O(1) access
    private final Node head;
    private final Node tail;          // Pseudo head & tail for easy list management

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.map = new HashMap<>();

        // Create dummy head and tail to avoid null checks
        head = new Node(0, 0);
        tail = new Node(0, 0);
        head.next = tail;
        tail.prev = head;
    }

    // GET operation
    public int get(int key) {
        if (!map.containsKey(key)) {
            return -1; // Not found
        }

        Node node = map.get(key);
        moveToFront(node);
//        remove(node);        // Remove from current position
//        insertToHead(node);  // Move to front (MRU)

        return node.value;
    }

    // PUT operation
    public void put(int key, int value) {
        if (map.containsKey(key)) {
            // Update existing node
            Node node = map.get(key);
            node.value = value;
            remove(node);
            insertToHead(node);
        } else {
            // If cache full → evict LRU
            if (map.size() == capacity) {
                Node lru = tail.prev; // Entry before tail = LRU
                remove(lru);
                map.remove(lru.key);
            }

            // Insert new node
            Node newNode = new Node(key, value);
            map.put(key, newNode);
            insertToHead(newNode);
        }
    }

    private void moveToFront(Node node){
        remove(node);
        insertToHead(node);
    }

    // Remove node from list
    private void remove(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    // Insert node right after head (MRU position)
    private void insertToHead(Node node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }

    // For debugging → print cache state
    public void printCache() {
        Node curr = head.next;
        System.out.print("Cache: ");
        while (curr != tail) {
            System.out.print("(" + curr.key + ":" + curr.value + ") ");
            curr = curr.next;
        }
        System.out.println();
    }
}

// Demo class
public class LRUCacheDemo {
    public static void main(String[] args) {
        LRUCache cache = new LRUCache(3);

        cache.put(1, 10);
        cache.put(2, 20);
        cache.put(3, 30);
        cache.printCache(); // (3:30) (2:20) (1:10)

        cache.get(1);       // Access 1 → now MRU
        cache.printCache(); // (1:10) (3:30) (2:20)

        cache.put(4, 40);   // Evicts LRU (2)
        cache.printCache(); // (4:40) (1:10) (3:30)

        System.out.println("Get 2: " + cache.get(2)); // -1 (evicted)
        System.out.println("Get 3: " + cache.get(3)); // 30
        cache.printCache(); // (3:30) (4:40) (1:10)
    }
}