package code.array.sliding_window;
/**
 https://leetcode.com/problems/minimum-size-subarray-sum/description/?envType=problem-list-v2&envId=binary-search
 * */
public class MinSubArrayLen {
    public static void main(String[] args) {
        System.out.println(minSubArrayLen(new int[]{2,3,1,2,4,3}, 7));
        System.out.println(minSubArrayLen(new int[]{1,4,4}, 4));
        System.out.println(minSubArrayLen(new int[]{1,1,1,1,1,1,1,1}, 11));
    }

    static int minSubArrayLen(int[] arr, int target){
        int sum = 0, i = 0, j=0, n = arr.length;
        int minLen = Integer.MAX_VALUE;
        // sliding window
        while (j < n){
            sum += arr[j++];
            while (sum > target &&  i <= j){
                sum -= arr[i++];
            }
            if (sum == target)
                minLen = Math.min(minLen, j-i);
        }
        return minLen;
    }
}
