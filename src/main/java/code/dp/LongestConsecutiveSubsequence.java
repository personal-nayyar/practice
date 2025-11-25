package code.dp;

import java.util.Arrays;
import java.util.HashSet;

/**
 Given an array of integers, find the length of the longest sub-sequence such that elements in the subsequence are consecutive integers, the consecutive numbers can be in any order.
 Input: arr[] = {1, 9, 3, 10, 4, 20, 2}
 Output: 4
 Explanation: The subsequence[n, n+1, n+2, ....] 1, 3, 4, 2 is the longest subsequence of consecutive elements

 Input: arr[] = {36, 41, 56, 35, 44, 33, 34, 92, 43, 32, 42}
 Output: 5
 Explanation: The subsequence 36, 35, 33, 34, 32 is the longest subsequence of consecutive elements.
 * */
public class LongestConsecutiveSubsequence { // Hashing

    public static int longestConsecutiveSubsequence(int[] arr){ // O(nlogn)
        arr = Arrays.stream(arr).sorted().distinct().toArray();
        int cnt = 1, lcs = 1;
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] == arr[i-1]+1)
                cnt++;
            else
                cnt = 1;
            lcs = Math.max(lcs, cnt);
        }
        return lcs;
    }

    public static int longestConsecutiveSubsequenceHashing(int[] arr){ // O(n)
        HashSet<Integer> lookup = new HashSet<>();
        for (int el : arr) {
            lookup.add(el);
        }
        int lcs = 1;
        for (int el : arr) {
            if (!lookup.contains(el - 1)) { // if el-1 is not present, then el is the starting element of a subsequence
                int cnt = 1;
                while (lookup.contains(el + cnt))
                    cnt++;
                lcs = Math.max(lcs, cnt);
            }
        }
        return lcs;
    }
}
