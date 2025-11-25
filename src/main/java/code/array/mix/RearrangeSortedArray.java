package code.array.mix;

import java.util.Arrays;

/**
 Given a sorted array of positive integers,
 rearrange the array alternately i.e first element should be the maximum value,
 second minimum value, third-second max, fourth-second min and so on.
 Input: arr[] = {1, 2, 3, 4, 5, 6, 7}
 Output: arr[] = {7, 1, 6, 2, 5, 3, 4}
 Input: arr[] = {1, 2, 3, 4, 5, 6}
 Output: arr[] = {6, 1, 5, 2, 4, 3}
 * */
public class RearrangeSortedArray {
    public static void main(String[] args) {
        rearrangeSortedArray(new int[]{1,2,3,4,5,6});
    }
    public static int[]  rearrangeSortedArray(int arr[]){
        int minIndex = 0, maxIndex=arr.length-1;
        int maxElem = arr[maxIndex]+1;
        for (int i = 0; i < arr.length; i++) {
            // arr[i] =  arr[i]+(arr[minIndex/maxIndex]%maxElem)*maxElem;
            // store two elem at same index, arr[maxIndex] is stored as multiplier and “arr[i]” is stored as remainder
            if(i%2 ==0){ // even index
                arr[i] =  arr[i]+ (arr[maxIndex]%maxElem)*maxElem;
                maxIndex--;
            }else {
                arr[i] = arr[i]+ (arr[minIndex]%maxElem)*maxElem;
                minIndex++;
            }
        }
        System.out.println(Arrays.toString(arr));

        for (int i = 0; i < arr.length; i++) {
            // arr[i] =  arr[i]+(arr[minIndex/maxIndex]%maxElem)*maxElem;
            // store two elem at same index, arr[maxIndex] is stored as multiplier and “arr[i]” is stored as remainder
            arr[i] =  arr[i]/maxElem;
        }
        System.out.println(Arrays.toString(arr));
        return arr;
    }
}
