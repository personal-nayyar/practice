package code.array.index_manipulation;


import utils.DSAUtils;

/**
 Given an unsorted array arr[] with both positive and negative elements,
 the task is to find the smallest positive number missing from the array.
 Note: You can modify the original array.

 Input:  arr[] = {2, 3, 7, 6, 8, -1, -10, 15}
 Output: 1

 Input:  arr[] = { 2, 3, -7, 6, 8, 1, -10, 15 }
 Output: 4

 Input: arr[] = {1, 1, 0, -1, -2}
 Output: 2
 */
public class SmallestPositiveMissing {

    static int findSmallestPositiveMissing(int[] arr){
        int n = arr.length;
        boolean[] present =  new boolean[n];
        for (int i = 0; i < n; i++) {
            if(arr[i] >0)
                present[arr[i-1]] = true;
        }

        for (int i = 0; i < n; i++) {
            if (!present[i])
                return i+1;
        }
        return n+1;
    }

    // segregate positive and negative numbers than apply index manipulation on positive part
    static int findSmallestPositiveMissingSpaceOptimised(Integer[] arr){
        int shift = segregate(arr);
        // shift positive to first
        for (int i = 0; i < shift; i++) {
            arr[i] = arr[shift++];
        }
        // use elem as index
        for (int i = 0; i < shift-1; i++) {
            arr[arr[i]-1] = arr[arr[i]-1]*-1;
        }
        // find first positive missing
        for (int i = 0; i < shift; i++) {
            if (arr[i] >0)
                return i+1;
        }
        return shift+1;
    }

    static int segregate(Integer[] arr){
        int j=0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i]<0)
                DSAUtils.swap(arr, i, j++);
        }
        return j;
    }
}
