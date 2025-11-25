package code.backtracking_dp;

import java.util.Arrays;

/**
 Given a Knapsack/Bag with W weight capacity and a list of N items with given vi value and wi weight.
 Put these items in the knapsack in order to maximise the value of all the placed items without exceeding the limit of the Knapsack.
 The problem remains the same but one cannot break the items you can either select it fully ( 1) or don’t select it (0 ).
 * */
public class O1KnapsackProblem{
    public static void main(String args[])
    {
        System.out.println(maxValueUsingMinWeight(new int[]{ 60, 100, 120 }, new int[]{ 10, 20, 30 },50));
        System.out.println(maxValueUsingMinWeight(new int[]{4,5,1}, new int[]{1,2,3}, 4));
    }
    static int maxValueUsingMinWeight(int[] values, int[] weights, int capacity){
        int n = weights.length;
        int[][] dp =  new int[n+1][capacity+1];

        // for capacity 0
        for (int i = 0; i<=n; i++) {
            dp[i][0] = 0;
        }

        // for no weight/value provided
        for (int j = 0; j<=n; j++) {
            dp[0][j] = 0;
        }

        for (int i = 1; i <=n; i++) { // for each item
            for (int j = 1; j <= capacity; j++) { // for each capacity/target
                if (j < weights[i-1]){
                    dp[i][j] = dp[i-1][j]; // can not pick this weight
                }else{
                    dp[i][j] = Math.max(
                            dp[i-1][j],  // not picking up this item
                            values[i-1]+dp[i-1][j-weights[i-1]] // picking up this item
                    );
                }
            }
        }
        return dp[n][capacity];
    }
}



/**
 You are given an integer array coins representing coins of different denominations and an integer amount representing a total amount of money.
 Return the fewest number of coins that you need to make up that amount. If that amount of money cannot be made up by any combination of the coins, return -1.
 You may assume that you have an infinite number of each kind of coin.
 Input: coins = [1,2,5], amount = 11
 Output: 3
 Explanation: 11 = 5 + 5 + 1

 Input: coins = [1], amount = 0
 Output: 0

 * */
class CoinChangeProblem {
    public static void main(String[] args) {
        System.out.println(coinChange(new int[]{9, 6, 5, 1}, 11));
        System.out.println(coinChange(new int[]{2}, 3));
    }
    public static int coinChange(int[] coins, int target){
        Arrays.sort(coins);
        int n = coins.length;
        int[][] mcc = new int[n+1][target+1];

        // if target is 0
        for (int i = 0; i <= n; i++) {
            mcc[i][0] = 0;
        }

        // if no coins given
        for (int j = 0; j<= target; j++) {
            mcc[0][j] = Integer.MAX_VALUE;
        }

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= target; j++) {
                if(j >= coins[i-1]){
                    mcc[i][j] = Math.min(mcc[i-1][j], // don't include current coin
                            1+mcc[i][j-coins[i-1]]); // include current coin, 1(cnt) used for current coin
                }else{
                    mcc[i][j] = mcc[i-1][j]; // can't include current coin
                }
            }
        }
        return mcc[n][target];
    }
}

/**
 Given a rod of length ‘N’ units. The rod can be cut into different sizes and each size has a cost associated with it.
 Determine the maximum cost obtained by cutting the rod and selling its pieces.
 Input:
 N = 8
 Price[] = {1, 5, 8, 9, 10, 17, 17, 20}
 Output:
 22
 Explanation:
 The maximum obtainable value is 22 by
 cutting in two pieces of lengths 2 and
 6, i.e., 5+17=22.
 * */
class RodCuttingMaximizeProfit{  // similar to 0/1 knapsack
    static int maxProfitCuttingRod(int[] price, int[] lengths, int l){
        int n = price.length;
        int[][] dp =  new int[n+1][l+1];

        // for length 0
        for (int i = 0; i<=n; i++) {
            dp[i][0] = 0;
        }

        // for no price/length provided
        for (int j = 0; j<=n; j++) {
            dp[0][j] = 0;
        }

        for (int i = 1; i <=n; i++) { // for each item
            for (int j = 1; j <= l; j++) { // for each length/target
                if (j < lengths[i-1]){
                    dp[i][j] = dp[i-1][j]; // can not pick this weight
                }else{
                    dp[i][j] = Math.max(
                            dp[i-1][j],  // not picking up this item
                            price[i-1]+dp[i][j-lengths[i-1]]
                    );
                }
            }
        }
        return dp[n][l];
    }
}



/**
 Partition problem is to determine whether a given set can be partitioned into two subsets such that the sum of elements in both subsets is the same.
 Input: arr[] = {1, 5, 11, 5}
 Output: true
 The array can be partitioned as {1, 5, 5} and {11}

 Input: arr[] = {1, 5, 3}
 Output: false
 The array cannot be partitioned into equal sum sets.

 This problem is essentially let us find whether there are several numbers in a set which are able to sum to a specific value (in this problem, the value is sum/2).
 Actually, this is a 0/1 knapsack problem, for each number, we can pick it or not. Let us assume dp[i][j] means whether the specific sum j can be gotten from the first i numbers. If we can pick such a series of numbers from 0-i whose sum is j, dp[i][j] is true, otherwise it is false.
 * */
class EqualPartitionSumProblem{ // equivalent to 0/1 knapsack problem, weather to pick elem for current set or not

    public static void main(String[] args) {
        System.out.println(equalPartitionPossible(new int[]{1,5,11,5}));
        System.out.println(equalPartitionPossible(new int[]{1,5,3}));
        System.out.println(equalPartitionPossible(new int[]{1,5,3,5}));
    }

    static boolean equalPartitionPossible(int[] arr){
        int sum = Arrays.stream(arr).sum();
        if (sum%2 !=0)
            return false;
        int target =  sum/2;
        int n = arr.length;
        boolean[][] dp =  equalPartitionPossible(arr, n, target);
        return dp[n][target];
    }

    static boolean[][] equalPartitionPossible(int[] arr, int n, int target){
        boolean[][] dp =  new boolean[n+1][target+1];
        // for capacity 0
        for (int i = 0; i<=n; i++) {
            dp[i][0] = true;
        }

        // for no weight/elem provided
        for (int j = 0; j<=n; j++) {
            dp[0][j] = false;
        }

        for (int i = 1; i <=n; i++) { // for each item
            for (int j = 1; j <= target; j++) { // for each capacity/target
                if (j < arr[i-1]){
                    dp[i][j] = dp[i-1][j]; // can't include this item into set
                }else{
                    dp[i][j] = dp[i-1][j] // don't include this item into set
                            || dp[i-1][j-arr[i-1]]; // include this item into set
                }
            }
        }
        return dp;
    }
}

/**
 Given an array arr of size n containing non-negative integers,
 the task is to divide it into two sets S1 and S2 such that the absolute difference between their sums is minimum
 and find the minimum difference
 Input: N = 4, arr[] = {1, 6, 11, 5}
 Output: 1
 Explanation:
 Subset1 = {1, 5, 6}, sum of Subset1 = 12
 Subset2 = {11}, sum of Subset2 = 11

 Input: N = 2, arr[] = {1, 4}
 Output: 3
 Explanation:
 Subset1 = {1}, sum of Subset1 = 1
 Subset2 = {4}, sum of Subset2 = 4

 * */
class MinSumDiffPartitionProblem{
    static int midDiffPartition(int[] arr){
        int n =  arr.length;
        int target =  Arrays.stream(arr).sum()/2;
        boolean[][] t =  EqualPartitionSumProblem.equalPartitionPossible(arr, n, target);
        int best =  Integer.MAX_VALUE;
        // Find the best possible subset sum <= half
        // such that the difference between the two subsets is minimized
        for (int j = target; j >=0; j--) {
            if(t[n][j]){
                best = target -j;
                break;
            }
        }
        int otherSum = target*2 - best;
        return Math.abs(otherSum-best);
    }
}