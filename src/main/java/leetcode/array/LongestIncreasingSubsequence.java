package leetcode.array;

import java.util.*;

public class LongestIncreasingSubsequence {
    public static void main(String[] args) {
        System.out.println(lis(new int[]{10, 9, 2, 5, 3, 7, 101, 18}));
    }

    static int lis(int[] arr){
        int n =  arr.length;
        int[] lis = new int[n];
        Arrays.fill(lis, 1);
        for (int i = 1; i < n; i++) {
            for (int j = i-1; j >=0 ; j--) {
                if (arr[i] > arr[j])
                    lis[i] = Math.max(lis[i], lis[j]+1);
            }
        }
        return Arrays.stream(lis).max().getAsInt();
    }
}

class LongestConsecutiveSubsequence{
    public static void main(String[] args) {
        System.out.println(lcs(new int[]{100,4,200,1,3,2}));
        System.out.println(lcs(new int[]{0,3,7,2,5,8,4,6,0,1}));
        System.out.println(lcs(new int[]{1,0,1,2}));
    }

    static int lcs(int[] arr){
        Set<Integer> set = new HashSet<>();
        for (int num: arr) set.add(num);

        int maxLen = 0;
        for (int curr:  arr){
            // check if this num can be start of the seq
            if (!set.contains(curr-1)){
                int lcs = 1;
                while (set.contains(curr+1)){
                    lcs++;
                    curr++;
                }
                maxLen = Math.max(maxLen, lcs);
            }
        }
        return maxLen;
    }
}
