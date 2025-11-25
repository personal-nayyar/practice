package code.array.index_manipulation;

import java.util.Arrays;

/**
 Given an unsorted array Arr of size N of positive integers.
 One number 'A' from set {1, 2,....,N} is missing and one number 'B' occurs twice in array.
 Find these two numbers.
 * */
public class MissingRepeatingNumber {

    public static void main(String[] args) {
        System.out.println(Arrays.toString(findMissingRepeating(new int[]{1,3,3})));
    }
    public static int[] findMissingRepeating(int[] arr){
        int mis = 0 , rep = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] < 0)
                rep = -arr[i];
            else
                arr[arr[i]-1] = arr[arr[i]-1]*-1;
        }

        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > 0)
                mis = i+1;
        }
        return new int[]{mis, rep};
    }
}
