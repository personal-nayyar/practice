package code.dp;

import java.util.Arrays;
import java.util.List;

/**
 Given a list piles, where piles[i] is a list of integers denoting the composition of the ith pile from top to bottom,
 and a positive integer k, return the maximum total value of coins you can have in your wallet if you choose exactly k coins optimally.
 Input: piles = [[1,100,3],[7,8,9]], k = 2
 Output: 101
 Explanation:
 The above diagram shows the different ways we can choose k coins.
 The maximum total we can obtain is 101.

 Input: piles = [[100],[100],[100],[100],[100],[100],[1,1,1,1,1,1,700]], k = 7
 Output: 706
 Explanation:
 The maximum total can be obtained if we choose all coins from the last pile

 * */
public class CoinPileMaxCollect {

    /**
     * Wrapper method to start the recursive calculation
     * @param piles List of piles where each pile is a list of coin values
     * @param k Maximum number of coins that can be collected
     * @return Maximum value that can be collected
     */
    static int maxCollection(List<List<Integer>> piles, int k) {
        return maxCollectionCompute(piles, 0, k);
    }

    /**
     * Recursive helper method to compute maximum coin collection
     * @param piles List of all coin piles
     * @param pileIndex Current pile being considered (0-based index)
     * @param k Remaining number of coins that can be collected
     * @return Maximum value that can be collected from remaining piles
     */
    static int maxCollectionCompute(List<List<Integer>> piles, int pileIndex, int k) {
        // Base case: if we've processed all piles or can't collect more coins
        if (pileIndex == piles.size() || k == 0)
            return 0;

        // Option 1: Skip the current pile and move to the next
        int bestCollect = maxCollectionCompute(piles, pileIndex + 1, k);

        // Option 2: Try taking 1 to min(k, pile size) coins from current pile
        int pickedAmount = 0;

        // Try taking 'i' coins from current pile (1 to min(remaining k, pile size))
        for (int i = 1; i <= Math.min(k, piles.get(pileIndex).size()); i++) {
            // Add the value of the i-th coin (0-based index, so i-1)
            pickedAmount += piles.get(pileIndex).get(i - 1);

            // Recursively find maximum from remaining piles with k-i coins left
            bestCollect = Math.max(
                    bestCollect,
                    pickedAmount + maxCollectionCompute(piles, pileIndex + 1, k - i)
            );
        }

        return bestCollect;
    }

    /**
     * Memoization-based solution to find maximum coin collection from piles
     * @param piles List of piles where each pile is a list of coin values
     * @param k Maximum number of coins that can be collected
     * @return Maximum value that can be collected using memoization
     */
    static int maxCollect(List<List<Integer>> piles, int k) {
        int n = piles.size();
        // dp[i][j] represents maximum value that can be collected from piles[i..n-1] with j coins
        int[][] dp = new int[n][k + 1];

        // Initialize dp table with -1 to indicate uncomputed states
        for (int[] row : dp) {
            Arrays.fill(row, -1);
        }

        // Start computation from the first pile with all k coins available
        return computeMax(0, piles, k, dp);
    }

    /**
     * Helper method that uses memoization to compute maximum coin collection
     * @param pileIndex Current pile being considered
     * @param piles List of all coin piles
     * @param k Remaining number of coins that can be collected
     * @param dp Memoization table storing computed results
     * @return Maximum value that can be collected from current state
     */
    static int computeMax(int pileIndex, List<List<Integer>> piles, int k, int[][] dp) {
        // Base case: no more piles or no more coins can be collected
        if (pileIndex == piles.size() || k == 0)
            return 0;

        // Return memoized result if already computed
        if (dp[pileIndex][k] != -1)
            return dp[pileIndex][k];

        // Option 1: Skip current pile and move to next
        int bestCollect = computeMax(pileIndex + 1, piles, k, dp);

        // Option 2: Try taking 1 to min(k, pile size) coins from current pile
        int pickAmount = 0;

        // Try taking (i) coins from current pile (1 to min(k, pile size))
        for (int i = 1; i <= Math.min(k, piles.get(pileIndex).size()); i++) {
            // Add value of current coin to the picked amount
            pickAmount += piles.get(pileIndex).get(i-1);

            // Update bestCollect with maximum value between:
            // 1. Current best (not taking from this pile)
            // 2. Taking (i+1) coins from current pile + best from remaining piles
            bestCollect = Math.max(
                    bestCollect,
                    pickAmount + computeMax(pileIndex + 1, piles, k - i, dp)
            );
        }

        // Store result in dp table before returning
        return dp[pileIndex][k] = bestCollect;
    }
}
