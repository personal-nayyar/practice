package leetcode.array;

import java.util.HashMap;
import java.util.Map;

/**
 * 4Sum II Problem - Multiple Approaches
 * Given four integer arrays nums1, nums2, nums3, and nums4 all of length n,
 * return the number of tuples (i, j, k, l) such that:
 * - 0 <= i, j, k, l < n
 * - nums1[i] + nums2[j] + nums3[k] + nums4[l] == 0
 * 
 * This class demonstrates different approaches from brute force to optimized solution
 * with detailed trade-off explanations.
 */
public class FourSumII {
    public static void main(String[] args) {
        FourSumII solution = new FourSumII();
        
        // Example 1
        int[] nums1 = {1, 2};
        int[] nums2 = {-2, -1};
        int[] nums3 = {-1, 2};
        int[] nums4 = {0, 2};
        
        System.out.println("=== Example 1 ===");
        System.out.println("Brute Force: " + solution.fourSumCountBruteForce(nums1, nums2, nums3, nums4)); // Output: 2
        System.out.println("Optimized: " + solution.fourSumCount(nums1, nums2, nums3, nums4)); // Output: 2
        
        // Example 2
        int[] nums1_2 = {0};
        int[] nums2_2 = {0};
        int[] nums3_2 = {0};
        int[] nums4_2 = {0};
        
        System.out.println("\n=== Example 2 ===");
        System.out.println("Brute Force: " + solution.fourSumCountBruteForce(nums1_2, nums2_2, nums3_2, nums4_2)); // Output: 1
        System.out.println("Optimized: " + solution.fourSumCount(nums1_2, nums2_2, nums3_2, nums4_2)); // Output: 1


        // Example 3
        int[] nums1_3 = {-1, -1};
        int[] nums2_3 = {-1, 1};
        int[] nums3_3 = {-1, 1};
        int[] nums4_3 = {1, -1};

        System.out.println("\n=== Example 2 ===");
        System.out.println("Brute Force: " + solution.fourSumCountBruteForce(nums1_3, nums2_3, nums3_3, nums4_3)); // Output: 1
        System.out.println("Optimized: " + solution.fourSumCount(nums1_3, nums2_3, nums3_3, nums4_3)); // Output: 1
    }

    // ============================================================================
    // APPROACH 1: BRUTE FORCE
    // ============================================================================
    
    /**
     * BRUTE FORCE APPROACH
     * 
     * Strategy: Check all possible combinations of (i, j, k, l)
     * - Use four nested loops to iterate through all indices
     * - For each combination, check if sum equals 0
     * 
     * Time Complexity: O(n^4)
     *   - Four nested loops, each iterating n times
     *   - For n=200: 200^4 = 1,600,000,000 operations (very slow!)
     * 
     * Space Complexity: O(1)
     *   - Only using a few variables (count, i, j, k, l)
     *   - No additional data structures needed
     * 
     * Trade-offs:
     *   ✅ PROS:
     *      - Simple and straightforward to understand
     *      - No extra memory required
     *      - Easy to implement and debug
     *   
     *   ❌ CONS:
     *      - Extremely slow for large inputs (n > 50)
     *      - Not scalable - time grows exponentially
     *      - Will timeout on competitive programming platforms
     * 
     * When to use:
     *   - Only for very small inputs (n < 20)
     *   - When memory is extremely constrained
     *   - For educational purposes to understand the problem
     * 
     * @param nums1 First array
     * @param nums2 Second array
     * @param nums3 Third array
     * @param nums4 Fourth array
     * @return Number of valid tuples
     */
    public int fourSumCountBruteForce(int[] nums1, int[] nums2, int[] nums3, int[] nums4) {
        int count = 0; // Count of valid tuples
        int n = nums1.length; // All arrays have same length
        
        // Four nested loops - check every possible combination
        for (int i = 0; i < n; i++) {           // Loop through nums1
            for (int j = 0; j < n; j++) {       // Loop through nums2
                for (int k = 0; k < n; k++) {   // Loop through nums3
                    for (int l = 0; l < n; l++) { // Loop through nums4
                        // Check if sum equals zero
                        if (nums1[i] + nums2[j] + nums3[k] + nums4[l] == 0) {
                            count++; // Found a valid tuple
                        }
                    }
                }
            }
        }
        
        return count;
    }

    // ============================================================================
    // APPROACH 2: OPTIMIZED USING HASHMAP (RECOMMENDED)
    // ============================================================================
    
    /**
     * OPTIMIZED APPROACH USING HASHMAP
     * 
     * Strategy: Divide and conquer using HashMap
     * 1. Calculate all pair sums from nums1 and nums2, store in HashMap with frequencies
     * 2. For each pair sum from nums3 and nums4, check if negative exists in HashMap
     * 
     * Key Insight: Instead of checking all 4 arrays together, we split into two groups:
     *   - Group 1: nums1 + nums2 (pre-compute and store)
     *   - Group 2: nums3 + nums4 (check against stored values)
     *   - If (nums1[i] + nums2[j]) + (nums3[k] + nums4[l]) == 0
     *     Then (nums1[i] + nums2[j]) == -(nums3[k] + nums4[l])
     * 
     * Time Complexity: O(n^2)
     *   - First loop: O(n^2) to build HashMap from nums1 & nums2 pairs
     *   - Second loop: O(n^2) to check nums3 & nums4 pairs against HashMap
     *   - HashMap operations (put, get, containsKey) are O(1) on average
     *   - For n=200: 200^2 = 40,000 operations (40,000x faster than brute force!)
     * 
     * Space Complexity: O(n^2)
     *   - HashMap stores at most n^2 unique sum values from nums1 & nums2 pairs
     *   - In worst case, all pair sums are unique
     *   - For n=200: at most 40,000 entries in HashMap
     * 
     * Trade-offs:
     *   ✅ PROS:
     *      - Much faster than brute force (O(n^2) vs O(n^4))
     *      - Scalable - handles n=200 efficiently
     *      - Optimal time complexity for this problem
     *      - Works well in competitive programming
     *   
     *   ❌ CONS:
     *      - Uses extra memory (O(n^2) space)
     *      - Slightly more complex to understand
     *      - Requires knowledge of HashMap data structure
     * 
     * When to use:
     *   - Production code (recommended approach)
     *   - Competitive programming
     *   - When n > 50
     *   - When time is more important than space
     * 
     * Performance Comparison (for n=200):
     *   - Brute Force: ~1.6 billion operations
     *   - Optimized: ~40,000 operations
     *   - Speedup: ~40,000x faster!
     * 
     * @param nums1 First array
     * @param nums2 Second array
     * @param nums3 Third array
     * @param nums4 Fourth array
     * @return Number of valid tuples
     */
    public int fourSumCount(int[] nums1, int[] nums2, int[] nums3, int[] nums4) {
        // Step 1: Pre-compute all pair sums from nums1 and nums2
        // Key: sum of nums1[i] + nums2[j]
        // Value: frequency (how many pairs produce this sum)
        Map<Integer, Integer> sumMap = new HashMap<>();
        
        // Build HashMap: O(n^2) time, O(n^2) space
        for (int i = 0; i < nums1.length; i++) {
            for (int j = 0; j < nums2.length; j++) {
                int sum = nums1[i] + nums2[j]; // Calculate pair sum
                // Store sum with frequency - increment if sum already exists
                // This handles cases where multiple pairs produce the same sum
                sumMap.put(sum, sumMap.getOrDefault(sum, 0) + 1);
            }
        }
        
        // Step 2: Check pairs from nums3 and nums4 against pre-computed sums
        int count = 0; // Total count of valid tuples
        
        // Check all pairs: O(n^2) time
        for (int k = 0; k < nums3.length; k++) {
            for (int l = 0; l < nums4.length; l++) {
                int pairSum = nums3[k] + nums4[l]; // Sum of current pair from nums3 and nums4
                int target = -pairSum; // We need this value to make total sum = 0
                
                // If target exists in map, we found valid combinations
                // The frequency tells us how many (i,j) pairs from nums1&nums2 produce this sum
                if (sumMap.containsKey(target)) {
                    count += sumMap.get(target); // Add all possible combinations
                    // Example: If sumMap has target=5 with frequency=3, and we found
                    // nums3[k]+nums4[l]=-5, then we have 3 valid tuples for this (k,l) pair
                }
            }
        }
        
        return count;
    }
    
    // ============================================================================
    // SUMMARY OF TRADE-OFFS
    // ============================================================================
    
    /**
     * DECISION MATRIX: Which approach to choose?
     * 
     * ┌─────────────────┬──────────────┬──────────────┬──────────────┬──────────────┐
     * │ Approach        │ Time         │ Space        │ Use Case     │ Scalability  │
     * ├─────────────────┼──────────────┼──────────────┼──────────────┼──────────────┤
     * │ Brute Force     │ O(n^4)       │ O(1)         │ n < 20       │ ❌ Poor      │
     * │ Optimized       │ O(n^2)       │ O(n^2)       │ n <= 200     │ ✅ Excellent │
     * └─────────────────┴──────────────┴──────────────┴──────────────┴──────────────┘
     * 
     * Real-world Performance (n=200):
     * - Brute Force: ~1.6 billion operations → ~16 seconds (estimated)
     * - Optimized: ~40,000 operations → ~0.0004 seconds
     * 
     * Memory Trade-off:
     * - Brute Force: ~16 bytes (just variables)
     * - Optimized: ~40,000 * 12 bytes ≈ 480 KB (HashMap entries)
     * 
     * Conclusion: The optimized approach is ALWAYS preferred for n > 20
     * The small memory overhead is negligible compared to the massive time savings.
     */

    public static int countFourSumII(int[] nums1, int[] nums2, int[] nums3, int[] nums4){
        int n = nums1.length;
        Map<Integer, Integer> sumMap = new HashMap<>();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int pairSum =  nums1[i] + nums2[j];
                sumMap.put(pairSum, sumMap.getOrDefault(pairSum, 0)+1);
            }
        }

        int count = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int pairSum = nums3[i] + nums4[j];
                int target =  -pairSum;
                if (sumMap.containsKey(target)){
                    count += sumMap.get(target);
                }
            }
        }
        return count;
    }
}
