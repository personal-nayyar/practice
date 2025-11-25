package code.array.prefixSum;

import java.util.*;

import static code.array.prefixSum.SubArrayWithGivenSum.sumArray;
import static utils.DSAUtils.Pair;

/**
 Given an array arr[] of non-negative integers and an integer sum, find a subarray that adds to a given sum.
 Input: arr[] = {1, 4, 20, 3, 10, 5}, sum = 33
 Output: Sum found between indexes 2 and 4
 Explanation: Sum of elements between indices 2 and 4 is 20 + 3 + 10 = 33

 Input: arr[] = {1, 4, 0, 0, 3, 10, 5}, sum = 7
 Output: Sum found between indexes 1 and 4
 Explanation: Sum of elements between indices 1 and 4 is 4 + 0 + 0 + 3 = 7

 Input: arr[] = {1, 4}, sum = 0
 Output: No subarray found
 Explanation: There is no subarray with 0 sum
 * */
// 	•	Prefix sum at index i = sum of elements from 0 to i.
//  •	Suffix sum at index i = sum of elements from i to n-1.
public class SubArrayWithGivenSum {
    public static void main(String[] args) {
        System.out.println(Arrays.toString(subArrayWithGivenSum(new int[]{1, 4, 20, 3, 10, 5}, 33)));
        System.out.println(Arrays.toString(subArrayWithGivenSum(new int[]{1, 4, 0, 0, 3, 10, 5}, 7)));
        System.out.println(Arrays.toString(subArrayWithGivenSum(new int[]{1, 4}, 0)));
        System.out.println(Arrays.toString(subArrayWithGivenSum(new int[]{10, 2, -2, -20}, -10)));

        System.out.println("=====================================");

        System.out.println(Arrays.toString(subArrayWithGivenSumOptimised(new int[]{1, 4, 20, 3, 10, 5}, 33)));
        System.out.println(Arrays.toString(subArrayWithGivenSumOptimised(new int[]{1, 4, 0, 0, 3, 10, 5}, 7)));
        System.out.println(Arrays.toString(subArrayWithGivenSumOptimised(new int[]{1, 4}, 0)));
        System.out.println(Arrays.toString(subArrayWithGivenSumOptimised(new int[]{10, 2, -2, -20, 10}, -10)));

        System.out.println("=====================================");

        System.out.println(Arrays.toString(subArrayWithGivenSumOptimised2(new int[]{1, 4, 20, 3, 10, 5}, 33)));
        System.out.println(Arrays.toString(subArrayWithGivenSumOptimised2(new int[]{1, 4, 0, 0, 3, 10, 5}, 7)));
        System.out.println(Arrays.toString(subArrayWithGivenSumOptimised2(new int[]{1, 4}, 0)));
        System.out.println(Arrays.toString(subArrayWithGivenSumOptimised2(new int[]{10, 2, -2, -20}, -10)));
    }

    static int[] subArrayWithGivenSum(int[] arr, int sum){
        int n = arr.length;
        for (int i = 0; i < n; i++) { // start
            for (int j = i; j < n; j++) { // end
                if(sumArray(arr, i, j) == sum)
                    return new int[]{i,j};
            }
        }
        return new int[]{-1,-1};
    }

    static int[] subArrayWithGivenSumOptimised(int[] arr, int sum){ // works only for non negative numbers
        int n= arr.length;
        int currSum = arr[0], start = 0;
        for (int i = 1; i <= n; i++) {
            while (currSum > sum && start < i-1){
                currSum -= arr[start];
                start++;
            }
            if(currSum == sum)
                return new int[]{start, i-1};

            if(i < n)
                currSum += arr[i];
        }
        return new int[]{-1,-1};
    }

    static int[] subArrayWithGivenSumOptimised2(int[] arr, int k){
        int n= arr.length;
        int x = 0;
        Map<Integer, Integer> prefixSum =  new HashMap<>();
        prefixSum.put(0, -1);
        for (int i = 0; i < n; i++) {
            x += arr[i];
            int prevReqSum =  x - k;
            if(prefixSum.containsKey(prevReqSum)) // there is an array with sum as x-k
                return new int[]{prefixSum.get(prevReqSum)+1, i};
            prefixSum.put(x, i);
        }
        return new int[]{-1,-1};
    }
    public static int sumArray(int[] arr, int start, int end){
        int sum = 0;
        for (int i = start; i <= end; i++) {
            sum += arr[i];
        }
        return sum;
    }
}

/**
 * Given an unsorted array of integers, find the number of subarrays having a sum exactly equal to a given number k.
 * nput : arr[] = {10, 2, -2, -20, 10}, k = -10
 * Output : 3
 * Explanation: Subarrays: arr[0…3], arr[1…4], arr[3..4] have a sum exactly equal to -10.
 *
 * Input : arr[] = {9, 4, 20, 3, 10, 5}, k = 33
 * Output : 2
 * Explanation: Subarrays : arr[0…2], arr[2…4] have a sum exactly equal to 33.
 * */
class NumberOfSubArrayWithGivenSum{
    public static void main(String[] args) {
        System.out.println(numberOfSubArrayWithGivenSum(new int[]{1,2,3,4,5}, 5));
        System.out.println(numberOfSubArrayWithGivenSum(new int[]{1, 4, 0, 0, 3, 10, 5}, 7));
        System.out.println(numberOfSubArrayWithGivenSum(new int[]{1, 4}, 0));
        System.out.println(numberOfSubArrayWithGivenSum(new int[]{10, 2, -2, -20, 10}, -10));
        System.out.println("=====================================");

        System.out.println(numberOfSubArrayWithGivenSumOptimised2(new int[]{1,2,3,4,5}, 5));
        System.out.println(numberOfSubArrayWithGivenSumOptimised2(new int[]{1, 4, 0, 0, 3, 10, 5}, 7));
        System.out.println(numberOfSubArrayWithGivenSumOptimised2(new int[]{1, 4}, 0));
        System.out.println(numberOfSubArrayWithGivenSumOptimised2(new int[]{10, 2, -2, -20, 10}, -10));
        System.out.println("=====================================");
    }

    static int numberOfSubArrayWithGivenSum(int[] arr, int sum){
        int n = arr.length;
        int count = 0;
        for (int i = 0; i < n; i++) { // start
            for (int j = i; j < n; j++) { // end
                if(sumArray(arr, i, j) == sum){
                    count++;
                }
            }
        }
        return count;
    }

    static int numberOfSubArrayWithGivenSumOptimised2(int[] arr, int sum){
        if (arr == null || arr.length == 0)
            return 0;
        int n =  arr.length;
        int count = 0, currSum = 0;
        Map<Integer, List<Integer>> prefixSum =  new HashMap<>();
        prefixSum.computeIfAbsent(0, k -> new ArrayList<>()).add(-1);
        for (int i = 0; i < n; i++) {
            currSum  +=  arr[i];
            int reqPref =  currSum - sum;
//            DSAUtils.printMap(prefixSum);
//            System.out.println("i: "+i+", currSum: "+currSum+ ", reqPref:"+reqPref+",count:"+count);
            if (prefixSum.containsKey(reqPref)){
                count += prefixSum.get(reqPref).size();
            }
            prefixSum.computeIfAbsent(currSum, k -> new ArrayList<>()).add(i);
        }
        return count;
    }
}

/**
 * Given an array of integers nums and an integer sum, return largest subarray sum equal to sum.
 * Input: nums = [1, -1, 5, -2, 3], sum = 2
 * Output: 3
 * Explanation: Subarray [5, -2, 3] sum is 2 and equal to 2.
 * input: nums = [1, -1, 5, -2, 3], sum = 4
 * Output: 3
 * Explanation: Subarray [1, -1, 5, -2, 3] sum is 3 and equal to 4.
 * input: nums = [1, -1, 5, -2, 3], sum = 10
 * Output: 0
 * Explanation: There is no subarray sum equal to 10.
 * input: nums = [1, -1, 5, -2, 3], sum = 0
 * Output: 0
 * Explanation: There is no subarray sum equal to 0.
 * input: nums = [1, -1, 5, -2, 3], sum = 6
 * Output: 6
 *
 * */
class LargestSubArrayWithGivenSum{
    public static void main(String[] args) {
        System.out.println(largestSubArrayWithGivenSum(new int[]{1, -1, 5, -2, 3}, 2));
        System.out.println(largestSubArrayWithGivenSum(new int[]{1, -1, 5, -2, 3}, 4));
        System.out.println(largestSubArrayWithGivenSum(new int[]{1, -1, 5, -2, 3}, 10));
        System.out.println("=====================================");

        System.out.println(largestSubArrayWithGivenSumOptimised(new int[]{1, -1, 5, -2, 3}, 2));
        System.out.println(largestSubArrayWithGivenSumOptimised(new int[]{1, -1, 5, -2, 3}, 4));
        System.out.println(largestSubArrayWithGivenSumOptimised(new int[]{1, -1, 5, -2, 3}, 10));
        System.out.println("=====================================");


        System.out.println(longestSubArrayWithGivenSumOptimised2(new int[]{1, -1, 5, -2, 3}, 2));
        System.out.println(longestSubArrayWithGivenSumOptimised2(new int[]{1, -1, 5, -2, 3}, 4));
        System.out.println(longestSubArrayWithGivenSumOptimised2(new int[]{1, -1, 5, -2, 3}, 10));
        System.out.println("=====================================");

    }

    static int largestSubArrayWithGivenSum(int[] arr, int sum){ // o(n^2)
        int n = arr.length;
        int maxLen = 0;
        for (int i = 0; i < n; i++) { // start
            for (int j = i; j < n; j++) { // end
                if(sumArray(arr, i, j) == sum)
                    maxLen = Math.max(maxLen, j-i+1);
            }
        }
        return maxLen;
    }

    static int largestSubArrayWithGivenSumOptimised(int[] arr, int sum){ // handle positive numbers only
        int n= arr.length;
        int currSum = arr[0], start = 0, maxLen = 0;
        for (int i = 1; i <= n; i++) {
            while (currSum > sum && start < i-1){
                currSum -= arr[start];
                start++;
            }
            if(currSum == sum)
                maxLen = Math.max(maxLen, i-start);
            if(i < n)
                currSum += arr[i];
        }
        return maxLen;
    }

    static int longestSubArrayWithGivenSumOptimised2(int[] arr, int sum){
        int n= arr.length;
        int currSum = 0, maxLen = 0;
        Map<Integer, Integer> prefixSum =  new HashMap<>();
        for (int i = 0; i < n; i++) {
            currSum += arr[i];
            int prevReqSum =  currSum - sum;
            if(prefixSum.containsKey(prevReqSum)) // there is an sub array previously with sum as x-k
                maxLen = Math.max(maxLen, i-prefixSum.get(prevReqSum));
            if (!prefixSum.containsKey(currSum))
                prefixSum.put(currSum, i);
        }
        return maxLen;
    }

}


/**
 Given an array arr[] of length N, find the length of the longest sub-array with a sum equal to 0.
 Input: arr[] = {15, -2, 2, -8, 1, 7, 10, 23}
 Output: 5
 Explanation: The longest sub-array with elements summing up-to 0 is {-2, 2, -8, 1, 7}

 Input: arr[] = {1, 2, 3}
 Output: 0
 Explanation: There is no subarray with 0 sum

 Input:  arr[] = {1, 0, 3}
 Output:  1
 Explanation: The longest sub-array with elements summing up-to 0 is {0}
 * */
class largestSubArrayWithSum0 {

    public static void main(String[] args) {
        System.out.println(largestSubarrayWithSum0(new int[]{15, -2, 2, -8, 1, 7, 10, 23}));
        System.out.println(largestSubarrayWithSum0(new int[]{1, 0, 3}));
        System.out.println(largestSubarrayWithSum0(new int[]{1, 2, 3}));
    }

    static int largestSubarrayWithSum0(int arr[]) {
        int x = 0, maxLen = 0;
        Map<Integer, Integer> prefixSum = new HashMap<>();
        for (int i = 0; i < arr.length; i++) {
            x += arr[i];
            if (prefixSum.containsKey(x)) // there is an sub array previously with sum as x-k = x as k=0
                maxLen = Math.max(maxLen, i - prefixSum.get(x));
            else {
                prefixSum.put(x, i);
            }
        }
        return maxLen;
    }

}


/**
 Given an array of both positive and negative numbers, the task is to find out the subarray whose sum is closest to 0.
 There can be multiple such subarrays, we need to output just 1 of them.
 Input : arr[] = {-1, 3, 2, -5, 4}
 Output : 1, 3
 Subarray from index 1 to 3 has sum closest to 0 i.e.
 3 + 2 + -5 = 0

 Input : {2, -5, 4, -6, 3}
 Output : 0, 2
 2 + -5 + 4 = 1 closest to 0

 arr[] = {-1, 3, 2, -5, 4}
 prefixSum
 -1 0
 2 1
 4 2
 -3 4
 1, 5
 {1,3}

 {2, -5, 4, -6, 3}
 prefixSum
 2 0
 -3 1
 1 2
 -5 3
 -2 4

 -5 3
 -3 1
 -2 4
 1 2
 2 0

 [1,4]
 [0,2]
 * */
class LargestSubarrayWithSumClosestToZero{
    public static void main(String[] args) {
        System.out.println(Arrays.toString(sumClosestToZeroArray(new int[]{-1, 3, 2, -5, 4})));
        System.out.println(Arrays.toString(sumClosestToZeroArray(new int[]{2, -5, 4, -6, 3})));
    }

    public static int[] sumClosestToZeroArray(int[] arr){
        Map<Integer, Integer> prefixSum =  new HashMap<>(); // Map<sum, index>
        int x = 0;
        for (int i = 0; i < arr.length; i++) {
            x += arr[i];
            if (prefixSum.containsKey(x)) // there is an array with sum = 0
                return new int[]{prefixSum.get(x)+1, i};
            prefixSum.put(x, i);
        }
        // if some zero is not present
        // If two prefix sums are very close to each other, their difference (i.e., a subarray sum) will be close to 0.
        int min = Integer.MAX_VALUE;
        Map<Integer, Integer> sortedMap  = new TreeMap<>(prefixSum); // sorted by key
        List<Integer> sums =  new ArrayList<>(sortedMap.keySet()); // sorted sums
        int[] resArr =  new int[2];
        for (int i = 1; i < sums.size(); i++) {
            if(min < sums.get(i)- sums.get(i-1)){ // get the min diff b/w two sums
                min  = sums.get(i)- sums.get(i-1);
                resArr = new int[]{sortedMap.get(sums.get(i)), sortedMap.get(sums.get(i-1))};
            }
            min = Math.min(min, sums.get(i)-sums.get(i-1));
        }
        return resArr;
    }

    public static int[] subarrayClosestToZero(int[] arr) {
        int n = arr.length;

        // Step 1: Create prefix sum array with indices
        @SuppressWarnings("unchecked")
        Pair<Integer, Integer>[] prefixSum = new Pair[n + 1];  // prefix[0] = 0 (sum of 0 elements)
        prefixSum[0] = new Pair<>(0, -1); // base case: sum=0 before array starts

        int runningSum = 0;
        for (int i = 0; i < n; i++) {
            runningSum += arr[i];
            prefixSum[i + 1] = new Pair<>(runningSum, i);
        }

        // Step 2: Sort prefix sums by value
        Arrays.sort(prefixSum, Comparator.comparingInt((Pair<Integer, Integer> a) -> a.first));

        // Step 3: Find two closest prefix sums
        int minDiff = Integer.MAX_VALUE;
        int start = 0, end = 0;

        for (int i = 1; i <= n; i++) {
            int diff = Math.abs(prefixSum[i].first - prefixSum[i - 1].first);
            if (diff < minDiff) {
                minDiff = diff;
                // indices of subarray = between these prefix sums
                start = Math.min(prefixSum[i].second, prefixSum[i - 1].second) + 1;
                end = Math.max(prefixSum[i].first, prefixSum[i - 1].second);
            }
        }

        return new int[]{start, end};
    }
}



/**
 Given an array arr[] of integers and an integer sum, find a subarray that adds to a given sum.
 Input: arr[] = {10, 2, -2, -20, 10}, sum = -10
 Output: Sum found between indexes 0 to 3
 Explanation: Sum of elements between indices
 0 and 3 is 10 + 2 – 2 – 20 = -10
 */
class SubArrayWithGivenSumHandleNegative {   // prefix sum hashing
    public static void main(String[] args) {
        System.out.println(Arrays.toString(subArrayWithGivenSumHandleNegative(new int[]{10, 2, -2, -20, 10}, -10)));
        System.out.println(Arrays.toString(subArrayWithGivenSumHandleNegative(new int[]{10, 2, -4, -5, 10}, -7)));
    }

    public static int[] subArrayWithGivenSumHandleNegative(int[] arr, int k){
        int x = 0;
        Map<Integer, Integer> prefixSum =  new HashMap<>();
        prefixSum.put(0, -1);
        for (int i = 0; i < arr.length; i++) {
            x += arr[i];   // represents current sum from beginning
            if (prefixSum.containsKey(x - k))  // there exist a sub array with sum = k because second sub array sum =  x-k and total sum till i =x
                return new int[]{prefixSum.get(x-k)+1, i};
            prefixSum.put(x, i);
        }
        return new int[]{-1,-1};
    }
}
