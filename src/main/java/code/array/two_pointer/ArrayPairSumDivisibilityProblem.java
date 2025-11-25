package code.array.two_pointer;

import java.util.*;
/**
 Given an array of integers and a number k, write a function that returns true
 if given array can be divided into pairs such that sum of every pair is divisible by k.

 Input : arr = [9, 5, 7, 3], k = 6
 Output: True
 Explanation: {(9, 3), (5, 7)} is a
 possible solution. 9 + 3 = 12 is divisible
 by 6 and 7 + 5 = 12 is also divisible by 6.

 Input: arr[] = [92, 75, 65, 48, 45, 35], k = 10
 Output: True
 We can divide the array into (92, 48), (75, 65) and (45, 35). The sum of all these pairs are multiples of 10.

 Input : arr = [2, 4, 1, 3], k = 4
 Output: False
 Explanation: There is no possible solution.

 * */
public class ArrayPairSumDivisibilityProblem {

    public static boolean isDivisible(int[] arr, int k){
        int n = arr.length;
        if (n%2 != 0)
            return false;
        if (k == 0)
            return false;
        for (int i = 0; i < n; i++)
            arr[i] = arr[i]%k;
        Arrays.sort(arr);
        for (int i = 0, j = n-1; i < j; i++, j--) {
            if ((arr[i]+arr[j])%k != 0)
                return false;
        }
        return true;
    }

    public static boolean isDivisibleOptimise2(int[] arr, int k) {
        int n = arr.length;
        if (n % 2 != 0)
            return false;
            
        // Create a frequency map of remainders when divided by k
        Map<Integer, Integer> freqMap = new HashMap<>();
        for (int num : arr) {
            int rem = (num % k + k) % k;  // Handle negative numbers
            freqMap.put(rem, freqMap.getOrDefault(rem, 0) + 1);
        }

        for (Map.Entry<Integer, Integer> entry : freqMap.entrySet()) {
            int rem = entry.getKey();
            
            // If remainder is 0, frequency must be even
            if (rem == 0) {
                if (entry.getValue() % 2 != 0) {
                    return false;
                }
            } 
            // For k even, check if k/2 has even frequency
            else if (k % 2 == 0 && rem == k / 2) {
                if (entry.getValue() % 2 != 0) {
                    return false;
                }
            }
            // For other remainders, check if count of 'rem' equals count of 'k-rem'
            else {
                int other = k - rem;
                if (freqMap.getOrDefault(other, 0) != entry.getValue()) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isDivisibleOptimise(int[] arr, int k) {
        int n = arr.length;
        if ( n%2 != 0)
            return false;
        if (k == 0)
            return false;
        int[] freq = new int[k];
        for (int el: arr){
            int rem = (el%k+k)%k;
            int comp = k - rem;
            if (freq[comp%k] != 0)
                freq[comp%k]--;
            else
                freq[rem]++;
        }
        for (int i = 0; i < k; i++) {
            if (freq[i] != 0)
                return false;
        }
        return true;
    }
    public static void main(String[] args) {
        // Test Case 1: Simple case with negative numbers
        System.out.println("Test 1: " + isDivisibleOptimise(new int[]{-1, -1}, 2) + " (Expected: true)");

        // Test Case 2: Example from problem statement
        System.out.println("Test 2: " + isDivisibleOptimise(new int[]{9, 5, 7, 3}, 6) + " (Expected: true)");

        // Test Case 3: Another example from problem statement
        System.out.println("Test 3: " + isDivisibleOptimise(new int[]{92, 75, 65, 48, 45, 35}, 10) + " (Expected: true)");

        // Test Case 4: Example that should return false
        System.out.println("Test 4: " + isDivisibleOptimise(new int[]{2, 4, 1, 3}, 4) + " (Expected: false)");

        // Test Case 5: Odd length array
        System.out.println("Test 5: " + isDivisibleOptimise(new int[]{1, 2, 3}, 5) + " (Expected: false)");

//         Test Case 6: All zeros
        System.out.println("Test 6: " + isDivisibleOptimise(new int[]{0, 0, 0, 0}, 5) + " (Expected: true)");

        // Test Case 7: Mixed positive and negative numbers
        System.out.println("Test 7: " + isDivisibleOptimise(new int[]{-1, 1, -2, 2}, 3) + " (Expected: true)");

        // Test Case 8: k = 1 (all pairs divisible by 1)
        System.out.println("Test 8: " + isDivisibleOptimise(new int[]{1, 2, 3, 4}, 1) + " (Expected: true)");

        // Test Case 9: k = 2 (checking even-odd pairing)
        System.out.println("Test 9: " + isDivisibleOptimise(new int[]{1, 2, 3, 4}, 2) + " (Expected: true)");

        // Test Case 10: Large k value (no pairs sum to multiple of k)
        System.out.println("Test 10: " + isDivisibleOptimise(new int[]{1, 2, 3, 4}, 10) + " (Expected: false)");

        // Test Case 11: k equals sum of min and max elements
        System.out.println("Test 11: " + isDivisibleOptimise(new int[]{1, 2, 3, 4}, 5) + " (Expected: true)");

        // Test Case 12: Duplicate elements
        System.out.println("Test 12: " + isDivisibleOptimise(new int[]{5, 5, 5, 5}, 10) + " (Expected: true)");

        // Test Case 13: All elements same and k is multiple of element sum
        System.out.println("Test 13: " + isDivisibleOptimise(new int[]{3, 3, 3, 3}, 6) + " (Expected: true)");

        // Test Case 14: All elements same but k is not multiple of element sum
        System.out.println("Test 14: " + isDivisibleOptimise(new int[]{3, 3, 3, 3}, 5) + " (Expected: false)");

        // Test Case 15: All negative numbers with valid pairs
        System.out.println("Test 15: " + isDivisibleOptimise(new int[]{-5, -3, -2, -6, -1, -4}, 8) + " (Expected: true)");

        // Test Case 16: Multiple pairs with same sum
        System.out.println("Test 16: " + isDivisibleOptimise(new int[]{1, 1, 2, 2, 3, 3}, 4) + " (Expected: true)");

//         Test Case 17: No possible pairs
        System.out.println("Test 17: " + isDivisibleOptimise(new int[]{1, 2, 3, 4, 5, 6}, 20) + " (Expected: false)");

        // Test Case 18: k is 0 (invalid input)
        System.out.println("Test 18: " + isDivisibleOptimise(new int[]{1, 2, 3, 4}, 0) + " (Expected: false)");

        // Test Case 19: Single element array with k=1
        System.out.println("Test 19: " + isDivisibleOptimise(new int[]{5}, 1) + " (Expected: false)");

        // Test Case 20: Empty array
        System.out.println("Test 20: " + isDivisibleOptimise(new int[]{}, 5) + " (Expected: true)");
    }
}
