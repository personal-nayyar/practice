package code.heap;

import java.util.Collections;
import java.util.PriorityQueue;

/**
 Given are N ropes of different lengths, the task is to connect these ropes into one rope with minimum cost,
 such that the cost to connect two ropes is equal to the sum of their lengths.
 Input: arr[] = {4,3,2,6} , N = 4
 Output: 29
 Explanation:

 First, connect ropes of lengths 2 and 3. Now we have three ropes of lengths 4, 6, and 5.
 Now connect ropes of lengths 4 and 5. Now we have two ropes of lengths 6 and 9.
 Finally connect the two ropes and all ropes have connected.
 * */

public class MinCostToConnectNRopes {

    public static int minCost(int[] arr){ // O(nlogn)
        int minCost = 0;
        MinHeap minHeap =  new MinHeap(arr);
        while (minHeap.getSize() >1){
            int min =  minHeap.extractMin();
            int secMin = minHeap.extractMin();
            int cost =  min+secMin;
            minCost += cost;
            minHeap.insert(cost);
        }
        return minCost;
    }

    public static int minCostPriorityQueue(int[] arr){
        PriorityQueue<Integer> pq =  new PriorityQueue<>(); // default implementation as min-heap
        PriorityQueue<Integer> pqMax =  new PriorityQueue<>(Collections.reverseOrder()); // implementation as max-code.heap
        for (int i = 0; i < arr.length; i++) {
            pq.add(arr[i]);
        }
        int min = 0, secMin = 0, currCost = 0, minCost=0;
        while (pq.size() >1){
            min = pq.poll();
            secMin = pq.poll();
            currCost = min+secMin;
            minCost += currCost;
            pq.add(currCost);
        }
        return minCost;
    }
}

class MinHeap {
    private int[] heap;   // Array to store heap elements
    private int size;     // Current number of elements
    private int capacity; // Maximum capacity

    // Constructor
    public MinHeap(int capacity) {
        this.capacity = capacity;
        this.size = 0;
        this.heap = new int[capacity];
    }

    // Constructor
    public MinHeap(int[] arr) {
        this.capacity = arr.length;
        this.size = 0;
        this.heap = new int[capacity];
        for (int i = capacity/2-1; i >= 0; i--) {
            heapifyDown(i);
        }
    }

    // ✅ Get parent index
    private int getParent(int index) {
        return (index - 1) / 2;
    }

    // ✅ Get left child index
    private int getLeftChild(int index) {
        return 2 * index + 1;
    }

    // ✅ Get right child index
    private int getRightChild(int index) {
        return 2 * index + 2;
    }

    // ✅ Insert element
    public void insert(int value) {
        if (size == capacity) {
            System.out.println("Heap is full. Cannot insert " + value);
            return;
        }

        // Step 1: Place at end
        heap[size] = value;
        int current = size;
        size++;

        System.out.println("\nInserted " + value + " at index " + current);

        // Step 2: Heapify Up
        heapifyUp(current);

        printHeap();
    }

    // ✅ Extract Min (Root element)
    public int extractMin() {
        if (size == 0) {
            throw new IllegalStateException("Heap is empty!");
        }

        int min = heap[0]; // Root is min

        // Step 1: Move last element to root
        heap[0] = heap[size - 1];
        size--;

        // Step 2: Heapify Down
        heapifyDown(0);

        System.out.println("\nExtracted Min: " + min);
        printHeap();
        return min;
    }

    // ✅ Heapify Up (bubble up)
    private void heapifyUp(int index) {
        while (index > 0 && heap[index] < heap[getParent(index)]) {
            System.out.println("Swap " + heap[index] + " with parent " + heap[getParent(index)]);

            // Swap with parent
            int temp = heap[index];
            heap[index] = heap[getParent(index)];
            heap[getParent(index)] = temp;

            index = getParent(index); // Move up
        }
    }

    // ✅ Heapify Down (sink down)
    private void heapifyDown(int index) {
        int smallest = index;

        int left = getLeftChild(index);
        int right = getRightChild(index);

        if (left < size && heap[left] < heap[smallest]) {
            smallest = left;
        }

        if (right < size && heap[right] < heap[smallest]) {
            smallest = right;
        }

        if (smallest != index) {
            System.out.println("Swap " + heap[index] + " with child " + heap[smallest]);

            // Swap
            int temp = heap[index];
            heap[index] = heap[smallest];
            heap[smallest] = temp;

            heapifyDown(smallest);
        }
    }

    public int getSize(){
        return size;
    }

    // ✅ Print heap
    public void printHeap() {
        System.out.print("Current Heap (Array): ");
        for (int i = 0; i < size; i++) {
            System.out.print(heap[i] + " ");
        }
        System.out.println();
    }

    // ✅ Main function
    public static void main(String[] args) {
        MinHeap minHeap = new MinHeap(10);

        // Insert elements
        minHeap.insert(10);
        minHeap.insert(20);
        minHeap.insert(15);
        minHeap.insert(30);
        minHeap.insert(40);
        minHeap.insert(5);

        // Extract elements
        minHeap.extractMin();
        minHeap.extractMin();
    }
}



