package code;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class Java8 {
    public static void main(String[] args) {
        // Collections.reverseOrder();
        PriorityQueue<Integer> pq = new PriorityQueue<>(Collections.reverseOrder()); // implementation as max-heap
        PriorityQueue<Integer> maHeap =  new PriorityQueue<>((a,b) -> b-a); // implementation as max-heap
        PriorityQueue<Integer> miHeap =  new PriorityQueue<>((a,b) -> a-b); // implementation as min-heap
        // PriorityQueue<Integer> pq = new PriorityQueue<>(); // default implementation as min-heap

        // sort map by key/value using stream api
         Map<Integer, Integer> map = new HashMap<>();
         map.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(System.out::println);
         map.entrySet().stream().sorted(Map.Entry.comparingByValue()).forEach(System.out::println);

    }

}
