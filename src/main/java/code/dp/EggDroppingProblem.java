package code.dp;

import java.util.Arrays;
import java.util.function.IntBinaryOperator;
import java.util.stream.IntStream;

/**
 * The Egg Dropping Problem is a classic dynamic programming problem that determines
 * the minimum number of attempts needed to find the critical floor in the worst case
 * with a given number of eggs and floors.
 * 
 * Problem Statement:
 * Given N eggs and K floors, find the minimum number of attempts needed to determine
 * the critical floor f (0 <= f <= K) such that:
 * - Any egg dropped from a floor higher than f will break
 * - Any egg dropped from floor f or below will not break
 * 
 * Key Rules:
 * 1. An egg that survives a fall can be used again
 * 2. A broken egg must be discarded
 * 3. The effect of a fall is the same for all eggs
 * 4. If an egg doesn't break at a certain floor, it won't break at any floor below
 * 5. If an egg breaks at a certain floor, it will break at any floor above
 *
 * Example:
 * Input: N = 2 eggs, K = 10 floors
 * Output: 4 (minimum attempts needed in worst case)
 *
 * Time Complexity:
 * - Recursive: O(2^K) - Exponential
 * - DP Solution: O(N*K^2) - Polynomial time with O(N*K) space
 */
public class EggDroppingProblem {
    public static void main(String[] args) {
        // Test cases for DP solution
        System.out.println("DP Solution:");
        System.out.println("Eggs: 2, Floors: 1 -> " + minTrailCnt(2, 1));  // Expected: 1
        System.out.println("Eggs: 2, Floors: 6 -> " + minTrailCnt(2, 6));  // Expected: 3
        System.out.println("Eggs: 2, Floors: 10 -> " + minTrailCnt(2, 10)); // Expected: 4

        // Test cases for recursive solution
        System.out.println("\nRecursive Solution:");
        System.out.println("Eggs: 2, Floors: 1 -> " + minTrailRecur(2, 1));  // Expected: 1
        System.out.println("Eggs: 2, Floors: 6 -> " + minTrailRecur(2, 6));  // Expected: 3
        System.out.println("Eggs: 2, Floors: 10 -> " + minTrailRecur(2, 10)); // Expected: 4
    }

    /**
     * Recursive solution to find minimum trials needed in worst case
     * @param eggs Number of eggs available
     * @param floor Number of floors to test
     * @return Minimum number of trials needed in worst case
     * 
     * Base Cases:
     * 1. If only 1 egg, we need to check each floor one by one
     * 2. If 0 or 1 floor, we need 0 or 1 trial respectively
     * 
     * For each floor k from 1 to floor, we consider both possibilities:
     * 1. Egg breaks: We need to check floors below with eggs-1
     * 2. Egg doesn't break: We need to check floors above with same eggs
     * We take the maximum of these two cases (worst case) and find the minimum over all floors
     */
    static int minTrail(int eggs, int floor) {
        // Base case 1: If only 1 egg, check each floor one by one
        if (eggs == 1)
            return floor;
            
        // Base case 2: If 0 or 1 floor, we need 0 or 1 trial
        if (floor == 0 || floor == 1)
            return floor;
            
        int minTrail = Integer.MAX_VALUE;
        
        // Try dropping from each floor from 1 to floor
        for (int k = 1; k <= floor; k++) {
            // 1 (current attempt) + max of two cases:
                // 1. Egg breaks: check k-1 floors below with eggs-1
                // 2. Egg doesn't break: check floor-k floors above with same eggs
            int trials = 1 + Math.max(
                minTrail(eggs - 1, k - 1),  // Egg breaks
                minTrail(eggs, floor - k)   // Egg doesn't break
            );
            // Keep track of minimum trials needed
            minTrail = Math.min(minTrail, trials);
        }
        return minTrail;
    }

    /**
     * Alternative recursive solution with different base case handling
     * @param n Number of eggs
     * @param f Number of floors
     * @return Minimum number of trials needed in worst case
     * 
     * This is similar to minTrail but with slightly different base cases
     * and loop starting from floor 2 instead of 1.
     */
    static int minTrailRecur(int n, int f) {
        // Base case 1: If only 1 egg, check each floor one by one
        if (n == 1)
            return f;
            
        // Base case 2: If 0 or 1 floor, we need 0 or 1 trial
        if (f == 0 || f == 1)
            return f;

        int minCnt = Integer.MAX_VALUE;
        
        // Try dropping from each floor from 2 to f
        for (int k = 2; k <= f; k++) {
            // 1 (current attempt) + max of two cases:
            int trials = 1 + Math.max(
                minTrailRecur(n - 1, k - 1),  // Egg breaks
                minTrailRecur(n, f - k)       // Egg doesn't break
            );
            // Keep track of minimum trials needed
            minCnt = Math.min(minCnt, trials);
        }
        return minCnt;
    }

    /**
     * Dynamic Programming solution to find minimum trials needed in worst case
     * @param n Number of eggs
     * @param f Number of floors
     * @return Minimum number of trials needed in worst case
     * 
     * DP State: t[i][j] represents the minimum number of trials needed for i eggs and j floors
     * 
     * Base Cases:
     * 1. If floors = 0, trials needed = 0
     * 2. If floors = 1, trials needed = 1
     * 3. If eggs = 1, trials needed = number of floors (linear search)
     * 
     * For each floor k from 1 to j, we consider both possibilities:
     * 1. Egg breaks: check k-1 floors below with i-1 eggs
     * 2. Egg doesn't break: check j-k floors above with i eggs
     * We take the maximum of these two cases (worst case) and find the minimum over all k
     */
    static int minTrailCnt(int n, int f) {
        // DP table where t[i][j] represents min trials for i eggs and j floors
        int[][] t = new int[n + 1][f + 1];

        // Base Case 1: If there's only 1 floor, we need 1 trial
        // If there are 0 floors, we need 0 trials
        for (int i = 1; i <= n; i++) {
            t[i][0] = 0;  // 0 floors -> 0 trials
            t[i][1] = 1;  // 1 floor -> 1 trial
        }

        // Base Case 2: If there's only 1 egg, we need to check each floor
        for (int j = 1; j <= f; j++) {
            t[1][j] = j;  // 1 egg and j floors -> j trials needed
        }

        // Fill the DP table in bottom-up manner
        for (int i = 2; i <= n; i++) {           // For each number of eggs
            for (int j = 2; j <= f; j++) {        // For each number of floors
                if (i > j) {
                    // If eggs > floors then result will be same as eggs-1 with same floors
                    t[i][j] = t[i - 1][j];
                } else {
                    t[i][j] = Integer.MAX_VALUE;
                    
                    // Try dropping from each floor k from 1 to j
                    for (int k = 1; k <= j; k++) {
                        // 1 (current attempt) + max of two cases:
                        int trials = 1 + Math.max(
                            t[i - 1][k - 1],  // Egg breaks
                            t[i][j - k]       // Egg doesn't break
                        );
                        // Keep the minimum trials needed
                        t[i][j] = Math.min(t[i][j], trials);
                    }
                }
            }
        }
        
        // Return the result for n eggs and f floors
        return t[n][f];
    }
}
