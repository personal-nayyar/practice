package code.array.mix;

import utils.DSAUtils;

import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * Input: Array[] = {1,2,3,4,5,6,7}, K = 3.
 * Output:Array[] = {5,6,7,1,2,3,4}
 *           temp = {5,6,7,1,2,3,4}
 *
 *
 * */
public class RotateArrayBySizeK {
    public static void main(String[] args) {
        int k = 3;
        int[] arr = {1,2,3,4,5,6,7};
        System.out.println(Arrays.toString(rotateByK(arr, k)));
        System.out.println(Arrays.toString(rotateByKWithouySpace(IntStream.of(arr).boxed().toArray(Integer[]::new), k)));
    }
    public static int[] rotateByK(int[] arr, int k){
        int n = arr.length;
        k=k%n;
        int[] temp =  new int[n];
        int j = 0;
        for (int i = n-k; i <n;) {
            temp[j++] =  arr[i++];
        }

        for (int i = 0; i < n-k;) {
            temp[j++] = arr[i++];
        }
        return temp;
    }

    public static Integer[] rotateByKWithouySpace(Integer[] arr, int k){
        /*
        reverse the array 0 to N-1
        reverse 0 to k-1
        reverse k to n-1
        */
        int n = arr.length;
        reverseArray(arr, 0, n-1);
        reverseArray(arr, 0, k-1);
        reverseArray(arr, k, n-1);
        return arr;
    }

    public static Integer[] reverseArray(Integer[] arr, int low, int high){
        while(low<high){
            DSAUtils.swap(arr, low++, high--);
        }
        return arr;
    }


}
