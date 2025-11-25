package utils;

import java.util.*;
public class DSAUtils {

    public static class Pair<T, U> {
        public T first;
        public U second;
        public Pair(T first, U second) {
            this.first = first;
            this.second = second;
        }
    }
    // 🔹 Swap two elements in an array
    public static void swap(int[] arr, int i, int j) {
        if (i >= 0 && j >= 0 && i < arr.length && j < arr.length) {
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }

    public static <T> void swap(T[] arr, int i, int j) {
        if (i >= 0 && j >= 0 && i < arr.length && j < arr.length) {
            T temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }

    public static <T> void swap(List<T> list, int i, int j){
        if (i >= 0 && j >= 0 && i < list.size() && j < list.size()) {
            T temp = list.get(i);
            list.set(i, list.get(j));
            list.set(j, temp);
        }
    }

    // 🔹 Print an integer array
    public static void printArray(int[] arr) {
        System.out.println(Arrays.toString(arr));
    }

    // 🔹 Print a string array
    public static void printArray(String[] arr) {
        System.out.println(Arrays.toString(arr));
    }

    // 🔹 Print a 2D integer array
    public static void print2DArray(int[][] matrix) {
        for (int[] row : matrix) {
            System.out.println(Arrays.toString(row));
        }
    }

    // 🔹 Print a 2D string array
    public static void print2DArray(String[][] matrix) {
        for (String[] row : matrix) {
            System.out.println(Arrays.toString(row));
        }
    }

    // 🔹 Print a List
    public static <T> void printList(List<T> list) {
        System.out.println(list);
    }

    // 🔹 Print a List of Lists
    public static <T> void printListOfLists(List<List<T>> listOfLists) {
        for (List<T> list : listOfLists) {
            System.out.println(list);
        }
    }

    // 🔹 Print a Set
    public static <T> void printSet(Set<T> set) {
        System.out.println(set);
    }

    // 🔹 Print a Map
    public static <K, V> void printMap(Map<K, V> map) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }

    // 🔹 Print a Queue
    public static <T> void printQueue(Queue<T> queue) {
        System.out.println(queue);
    }

    // 🔹 Print a Stack
    public static <T> void printStack(Stack<T> stack) {
        System.out.println(stack);
    }

    // 🔹 Convert array to List
    public static <T> List<T> arrayToList(T[] arr) {
        return Arrays.asList(arr);
    }

    // 🔹 Convert List to Array
    public static <T> Object[] listToArray(List<T> list) {
        return list.toArray();
    }

    // 🔹 Generate random array of integers
    public static int[] generateRandomArray(int size, int bound) {
        Random rand = new Random();
        int[] arr = new int[size];
        for (int i = 0; i < size; i++) {
            arr[i] = rand.nextInt(bound);
        }
        return arr;
    }

    // 🔹 Find max element in array
    public static int maxInArray(int[] arr) {
        return Arrays.stream(arr).max().orElse(Integer.MIN_VALUE);
    }

    // 🔹 Find min element in array
    public static int minInArray(int[] arr) {
        return Arrays.stream(arr).min().orElse(Integer.MAX_VALUE);
    }

    // 🔹 Sum of array elements
    public static int sumArray(int[] arr) {
        return Arrays.stream(arr).sum();
    }

    // 🔹 Reverse an array
    public static void reverseArray(int[] arr) {
        int i = 0, j = arr.length - 1;
        while (i < j) {
            swap(arr, i, j);
            i++;
            j--;
        }
    }

    // 🔹 Check if array is sorted
    public static boolean isSorted(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] < arr[i - 1]) {
                return false;
            }
        }
        return true;
    }

    // 🔹 Print Heap (priority queue)
    public static <T> void printHeap(PriorityQueue<T> heap) {
        System.out.println(heap);
    }

    // 🔹 Print frequency map of array
    public static void printFrequency(int[] arr) {
        Map<Integer, Integer> freq = new HashMap<>();
        for (int num : arr) {
            freq.put(num, freq.getOrDefault(num, 0) + 1);
        }
        printMap(freq);
    }

    // 🔹 Print frequency map of string
    public static void printFrequency(String str) {
        Map<Character, Integer> freq = new HashMap<>();
        for (char c : str.toCharArray()) {
            freq.put(c, freq.getOrDefault(c, 0) + 1);
        }
        printMap(freq);
    }
}
