package code.array.divive_n_conquer;

import utils.DSAUtils;

import java.util.Arrays;
import java.util.PriorityQueue;

/**
 Given an array and a number K where K is smaller than the size of the array.
 Find the K’th smallest element in the given array. Given that all array elements are distinct.
 Examples:
 Input: arr[] = {7, 10, 4, 3, 20, 15}, K = 3
 Output: 7
 Input: arr[] = {7, 10, 4, 3, 20, 15}, K = 4
 Output: 10
 * */
public class KthLargest {
    public static void main(String[] args) {
        System.out.println(kthSmallest(new int[]{7, 10, 4, 3, 20, 15}, 3));
    }
    static int kthSmallest(int arr[], int k){
        return kthSmallestUtil(arr, 0, arr.length-1, k);
    }
    static int kthSmallestUtil(int[] arr, int low, int high, int k){ // O(nlogn)
        if(k > 0 && k <= high-low+1){ // k should be in range
            int pi =  QuickSort.partition(arr, low, high);
            if (pi == k-1)
                return arr[pi];
            else if(pi > k-1) // if pi is greater than k-1, then kth smallest is in left subarray
                return kthSmallestUtil(arr, low, pi-1, k);
            else
                return kthSmallestUtil(arr, pi+1, high, k);
        }
        return Integer.MAX_VALUE;
    }

    static int kthSmallestUsingPriorityQueue(int[] arr, int k){ // O(nlogk)
        PriorityQueue<Integer> priorityQueue = new PriorityQueue<>((a,b) -> b-a); // implementation as max-heap
        for (int i = 0; i < k; i++) {
            priorityQueue.add(arr[i]); // add first k elements to priority queue
        }

        for (int i = k; i < arr.length; i++) {
            priorityQueue.add(arr[i]); // O(logk)
            priorityQueue.poll(); // O(logk)
        }
        return priorityQueue.peek();
    }

    static void buildHeap(int[] arr){
        int n = arr.length;
        // build max-heap, max element always at root
        for (int i = n/2-1; i >=0 ; i--) {
            heapify(arr, i, n);
        }

        for (int i = n-1; i >=0; i--) {
            DSAUtils.swap(arr, 0, i); // swap root with last element
            heapify(arr, 0, i); // heapify remaining elements
        }
    }

    static void heapify(int[] arr, int i, int n){
        int largest = i;
        int l = 2*i+1;
        int r = 2*i+2;
        if (l < n && arr[l] > arr[largest]) largest = l;
        if (r < n && arr[r] > arr[largest]) largest = r;
        if (i != largest){
            DSAUtils.swap(arr, i, largest);
            heapify(arr, largest, n);
        }
    }
}

class QuickSort{
    public static void quickSort(int arr[]){
        quickSortUtil(arr, 0, arr.length-1);
    }
    public static void quickSortUtil(int[] arr, int low, int high){
        if(low < high){
            int pi = partition(arr, low, high); // pi is now at its correct position
            quickSortUtil(arr, low, pi-1); // sort left
            quickSortUtil(arr, pi+1, high);
        }
    }
    public static int partition(int[] arr, int low, int high){
        int pivot = arr[high];
        int i=low-1;
        int j=low;
        for (;j < high; j++) {
            if (arr[j] < pivot) {
                DSAUtils.swap(arr, ++i, j);
            }
        }
        i++;
        DSAUtils.swap(arr, i, high);
        return i;
    }
}

class QuickSortAlgo{
    public static void main(String[] args) {
       int[] arr =  new int[]{2,3,1,4,7,5};
       quickSort(arr);
       System.out.println(Arrays.toString(arr));
    }
    static void quickSort(int[] arr){
        quickSortUtil(arr, 0, arr.length-1);
    }
    static void quickSortUtil(int[] arr, int l, int r){
        if(l>=r)
            return;
        int piIndex =  partition(arr, l,r);
        quickSortUtil(arr, l, piIndex-1);
        quickSortUtil(arr, piIndex+1, r);
    }
    static int partition(int[] arr, int l, int r){
        int pi = r, i=l, j=l;
        while(j < r){
            if (arr[j] < arr[pi])
                DSAUtils.swap(arr, i++,j++);
            else
                j++;
        }
        DSAUtils.swap(arr, pi, i);
        return i;
    }
}
