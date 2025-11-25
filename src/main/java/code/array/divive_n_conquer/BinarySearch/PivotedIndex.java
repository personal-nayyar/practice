package code.array.divive_n_conquer.BinarySearch;

import static java.util.Arrays.binarySearch;

/**
 Given a sorted and rotated array A of N distinct elements which is rotated at some point, and given an element K. The task is to find the index of the given element K in the array A.
 Given a sorted and rotated array A of N distinct elements which is rotated at some point, find the minimum element in the array or pivoted index.
 */
public class PivotedIndex {
    public static void main(String[] args) {
        int[] arr = {5, 6, 7, 8, 9, 10, 1, 2, 3};
        System.out.println(pivotedIndex(arr));
    }

    static int pivotedIndex(int[] arr){
        return pivotedIndexUtil(arr, 0, arr.length - 1);
    }
    static int pivotedIndexUtil(int[] arr, int low, int high){ // O(logn)
        if (arr[low] <= arr[high]) // not rotated
            return low;
        int mid = (low + high) / 2;
        if (arr[mid] > arr[mid + 1])
            return mid + 1;
        if (arr[mid] < arr[mid - 1])
            return mid;
        if (arr[mid] < arr[high]) // right side is sorted
            return pivotedIndexUtil(arr, low, mid - 1);
        else
            return pivotedIndexUtil(arr, mid + 1, high);
    }
}

class PivotedSearch{
    public static void main(String[] args) {
        int[] arr = {5, 6, 7, 8, 9, 10, 1, 2, 3};
        int key = 3;
        System.out.println(pivotedSearch(arr, key));
        arr =  new int[]{3, 4, 5, 1, 2};
        key = 1;
        System.out.println(pivotedSearch(arr, key));
        arr = new int[]{5,6,7,8,9,1,2,3,4};
        key = 2;
        System.out.println(pivotedSearch(arr, key));
    }

    static int pivotedSearch(int[] arr, int key){
        return pivotedSearchUtil(arr, 0, arr.length - 1, key);
    }

    static int pivotedSearchUtil2(int[] arr, int low, int high, int k){
        int pivot = PivotedIndex.pivotedIndex(arr);
        if (pivot == 0) // not rotated
            return binarySearch(arr, low, high, k);
        if (k >= arr[low] && k <= arr[pivot - 1])  // k is in left side
            return binarySearch(arr, low, pivot - 1, k);
        else
            return binarySearch(arr, pivot, high, k);
    }

    static int pivotedSearchUtil(int[] arr, int low, int high, int k){
        if (low > high)
            return -1;
        int mid = (low + high) / 2;
        if (arr[mid] == k)
            return mid;
        if (k >= arr[low] && k <= arr[mid]) // k is in left side
                return pivotedSearchUtil(arr, low, mid - 1, k);
        else
            return pivotedSearchUtil(arr, mid + 1, high, k);
    }
}
