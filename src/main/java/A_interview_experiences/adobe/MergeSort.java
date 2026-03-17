package A_interview_experiences.adobe;

import java.util.Arrays;

public class MergeSort {
    public static void mergeSort(int[] arr){
        // merge sort algorithm
        // divide and conquer approach
        // divide the array into two halves
        // sort the two halves
        // merge the two sorted halves
        mergeSortHelper(arr, 0, arr.length-1);
    }

    private static void mergeSortHelper(int[] arr, int l, int h){
        if(l<h){
            int m = l + (h-l)/2; // m is the middle index
            mergeSortHelper(arr, l, m); // sort the left half
            mergeSortHelper(arr, m+1, h); // sort the right half
            merge(arr, l, m, h); // merge the two sorted halves
        }
    }

    private static void merge(int[] arr, int l, int m, int h){
        int n1 = m - l + 1;
        int n2 = h - m;
        int[] left = new int[n1];
        int[] right = new int[n2];

        // copy data to left and right arrays
        for(int i=0; i<n1; i++){
            left[i] = arr[l+i];
        }
        for(int j=0; j<n2; j++){
            right[j] = arr[m+1+j];
        }

        // merge the two sorted halves
        int i=0, j=0, k=l;
        while(i<n1 && j<n2){
            if(left[i] <= right[j]){
                arr[k] = left[i];
                i++;
            }
            else{
                arr[k] = right[j];
                j++;
            }
            k++;
        }
        while(i<n1){
            arr[k] = left[i];
            i++;
            k++;
        }
        while(j<n2){
            arr[k] = right[j];
            j++;
            k++;
        }
    }
}
