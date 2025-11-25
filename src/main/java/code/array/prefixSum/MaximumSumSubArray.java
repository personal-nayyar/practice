package code.array.prefixSum;

import java.util.Arrays;

/**
 * Given an array arr[] of size N.
 * The task is to find the sum of the contiguous subarray within a arr[] with the largest sum.
 * Input: nums = [-2,1,-3,4,-1,2,1,-5,4]
 * Output: 6
 * Explanation: [4,-1,2,1] has the largest sum = 6.
 * */
public class MaximumSumSubArray {
    public static void main(String[] args) {
        int[] arr =  new int[]{-2, -3, 4, -1, -2, 1, 5, -3};
        System.out.println(maxSumSubArray(arr));
        System.out.println(maxSumSubArray(arr));
        arr  = new int[]{-2,-3,-2,-4-5};
        System.out.println(maxSumSubArray(arr));
    }

    public static int maxSumSubArray(int[] arr){
        int i=0, maxSum = Integer.MIN_VALUE, currSum = 0;
        while (i < arr.length){
            currSum += arr[i++];
            maxSum = Math.max(maxSum, currSum);
            if (currSum <  0)
                currSum = 0;
        }
        return maxSum;
    }
}

/**
 Given an array containing n integers. The problem is to find the sum of the elements of the contiguous subarray having the smallest(minimum) sum.
 Input : arr[] = {3, -4, 2, -3, -1, 7, -5}
 Output : -6
 Subarray is {-4, 2, -3, -1} = -6

 Input : arr = {2, 6, 8, 1, 4}
 Output : 1
 * */
class SmallestSumContiguousSubarray{
    public static void main(String[] args) {
        System.out.println(smallestSumContiguousSubarray(new int[]{3, -4, 2, -3, -1, 7, -5}));
        System.out.println(smallestSumContiguousSubarray(new int[]{2, 6, 8, 1, 4}));

        System.out.println(smallestSumContiguousSubarray(new int[]{3, -4, 2, -3, -1, 7, -5}));
        System.out.println(smallestSumContiguousSubarray(new int[]{2, 6, 8, 1, 4}));
    }
    static int smallestSumContiguousSubarray(int[] arr){
        int minVal = arr[0], minSoFar =  arr[0], currSum = 0;
        for (int i = 1; i < arr.length; i++) {
            currSum += arr[i];
            minSoFar =  Math.min(currSum, minSoFar);
            minVal  = Math.min(minVal, arr[i]);
            if(currSum > 0){
                currSum = 0;
            }
        }
        return Math.min(minVal, minSoFar);
    }
}

/**
 Given a circular array of size n, find the maximum subarray sum of the non-empty subarray.
 Input: arr[] = {8, -8, 9, -9, 10, -11, 12}
 Output: 22
 Explanation: Subarray 12, 8, -8, 9, -9, 10 gives the maximum sum, that is 22.

 Input: arr[] = {10, -3, -4, 7, 6, 5, -4, -1}
 Output:  23
 Explanation: Subarray 7, 6, 5, -4, -1, 10 gives the maximum sum, that is 23.

 Input: arr[] = {-1, 40, -14, 7, 6, 5, -4, -1}
 Output: 52
 Explanation: Subarray 7, 6, 5, -4, -1, -1, 40 gives the maximum sum, that is 52.
 * */
class MaximumSumCircularSubArray{
    public static void main(String[] args) {
        System.out.println(maxSumCircularArray(new int[]{8, -8, 9, -9, 10, -11, 12}));
        System.out.println(maxSumCircularArray(new int[]{10, -3, -4, 7, 6, 5, -4, -1}));
        System.out.println(maxSumCircularArray(new int[]{-1, 40, -14, 7, 6, 5, -4, -1}));
    }
    static int maxSumCircularArray(int[] arr){
        int maxSumCont = MaximumSumSubArray.maxSumSubArray(arr);
        int minSumCont = SmallestSumContiguousSubarray.smallestSumContiguousSubarray(arr); // max negative value adding to the sum
        int totalSum = Arrays.stream(arr).sum();
        return Math.max(maxSumCont, totalSum-minSumCont);
    }

    static int maxSumCircularSubArray(int[] arr){
        int n = arr.length;
        int maxSumSubArray =  MaximumSumSubArray.maxSumSubArray(arr);
        int minSumSubArray = SmallestSumContiguousSubarray.smallestSumContiguousSubarray(arr);
        int sum =  Arrays.stream(arr).sum();
        return Math.max(sum - minSumSubArray, maxSumSubArray);
    }
}
