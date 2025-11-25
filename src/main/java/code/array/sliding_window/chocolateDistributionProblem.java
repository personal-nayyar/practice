package code.array.sliding_window;

import java.util.Arrays;

/**
 Given an array of N integers where each value represents the number of chocolates in a packet.
 Each packet can have a variable number of chocolates.
 There are m students, the task is to distribute chocolate packets such that:
 Each student gets one packet.
 The difference between the number of chocolates in the packet with maximum chocolates
 and the packet with minimum chocolates given to the students is minimum.
 Input : arr[] = {7, 3, 2, 4, 9, 12, 56} , m = 3
 Output: Minimum Difference is 2
 Explanation:
 We have seven packets of chocolates and we need to pick three packets for 3 students
 If we pick 2, 3 and 4, we get the minimum difference between maximum and minimum packet sizes.

 Input : arr[] = {3, 4, 1, 9, 56, 7, 9, 12} , m = 5
 Output: Minimum Difference is 6
 */
public class chocolateDistributionProblem {
    public static void main(String[] args) {
        System.out.println(minDiffDistribution(new int[]{3, 4, 1, 9, 56, 7, 9, 12}, 5));
        System.out.println(minDiff(new int[]{3, 4, 1, 9, 56, 7, 9, 12}, 5));
    }
    public static int minDiffDistribution(int[] arr, int m){
        Arrays.sort(arr);
        int minDiff = Integer.MAX_VALUE;
        for(int i=0;i<arr.length-m;i++){
            minDiff =  Math.min(minDiff, arr[i+m-1]- arr[i]);
        }
        return minDiff;
    }

    static int minDiff(int[] arr, int m){
        Arrays.sort(arr);
        int minDff  = Integer.MAX_VALUE;
        for (int i = 0, j = m-1; j < arr.length; i++,j++) {
            minDff = Math.min(minDff, arr[j]-arr[i]);
        }
        return minDff;
    }
}
