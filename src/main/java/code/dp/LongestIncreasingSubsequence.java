package code.dp;

import code.greedy.ActivitySelectionProblem;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 The Longest Increasing Subsequence (LIS) problem is to
 find the length of the longest subsequence of a given sequence
 such that all elements of the subsequence are sorted in increasing order.
 For example, the length of LIS for {10, 22, 9, 33, 21, 50, 41, 60, 80} is 6
 and LIS is {10, 22, 33, 50, 60, 80}.
 * */
public class LongestIncreasingSubsequence {
    public static void main(String[] args) {
        System.out.println(longestIncreasingSubsequence(new int[]{10, 22, 9, 33, 21, 50, 41, 60, 80}));
        System.out.println(longestIncreasingSubsequence(new int[]{10,9,2,5,3,7,101,18}));
        System.out.println(longestIncreasingSubsequence(new int[]{0,1,0,3,2,3}));
        System.out.println(longestIncreasingSubsequence(new int[]{7,7,7,7,7,7,7}));
//        System.out.println(lisUsingLCS(new int[]{10, 22, 9, 33, 21, 50, 41, 60, 80}));
    }
    static int longestIncreasingSubsequence(int[] arr){ // O(n^2)
        int n =  arr.length;
        int[] lis =  new int[n];
        Arrays.fill(lis, 1); // since every element is a subsequence of length 1
        for (int i = 1; i < n; i++) {
            for (int j = i-1; j >=0; j--) {
                if (arr[i] > arr[j])
                    lis[i] = Math.max(lis[i], lis[j]+1);
            }
        }
        return Arrays.stream(lis).max().getAsInt();
    }

    static int lisUsingLCS(int[] arr){ // O(nlogn)
        int[] copyArr = Arrays.stream(arr).distinct().toArray();
        Arrays.sort(copyArr); // sort the array as we need to find the longest increase subsequence i.e. in increasing order
        return LongestCommonSubsequence.lengthOfLongestCommonSubsequence(Arrays.toString(arr), Arrays.toString(copyArr));
    }
}

/**
 Given an array of n positive integers.
 Write a program to find the maximum sum subsequence of the given array
 such that the integers in the subsequence are sorted in increasing order.
 For example, if input is {1, 101, 2, 3, 100, 4, 5}, then output should be 106 (1 + 2 + 3 + 100),
 if the input array is {3, 4, 5, 10}, then output should be 22 (3 + 4 + 5 + 10)
 and if the input array is {10, 5, 4, 3}, then output should be 10

 This problem is a variation of the standard Longest Increasing Subsequence (LIS) problem.
 We need a slight change in the Dynamic Programming solution of LIS problem.
 All we need to change is to use sum as a criteria instead of a length of increasing subsequence.
 * */
class MaximumSumIncreasingSubsequence{
    public static void main(String[] args) {
        System.out.println(maxSumIncreasingSubsequence(new int[]{1,101,105,2,3,100}));
    }
    static int maxSumIncreasingSubsequence(int[] arr){
        int n =  arr.length;
        int[] mis =  Arrays.copyOf(arr, arr.length); // mis[i] = max sum of increasing subsequence ending at i, since every element will be sum of itself
        for (int i = 1; i < n; i++) {
            for (int j = i-1; j >=0; j--) {
                if (arr[i] > arr[j])
                    mis[i] = Math.max(mis[i], mis[j]+arr[i]);
            }
        }
        return Arrays.stream(mis).max().getAsInt();
    }
}

/**
 You are given n pairs of numbers. In every pair, the first number is always smaller than the second number.
 A pair (c, d) can follow another pair (a, b) if b < c. Chain of pairs can be formed in this fashion.
 Find the longest chain which can be formed from a given set of pairs
 Input: pairs = [[1,2],[2,3],[3,4]]
 Output: 2
 Explanation: The longest chain is [1,2] -> [3,4].

 Input: pairs = [[1,2],[7,8],[4,5]]
 Output: 3
 Explanation: The longest chain is [1,2] -> [4,5] -> [7,8].

 * */
class LongestChainOfPairs {

    static class Pair{
        int first;
        int second;
        Pair(int first, int second){
            this.first = first;
            this.second = second;
        }

        @Override
        public String toString() {
            return "Pair{" +
                    "first=" + first +
                    ", second=" + second +
                    '}';
        }
    }

    public static void main(String[] args) {
        int[][] arr = {{1,2},{7,8},{4,5}};
        LongestChainOfPairs.Pair[] pairs = Arrays.stream(arr).map(p-> new LongestChainOfPairs.Pair(p[0], p[1])).toArray(LongestChainOfPairs.Pair[]::new);
        System.out.println(maxLengthUsingLIS(pairs));
//        System.out.println(maxLengthUsingActivitySelectionProblem(pairs));
    }

    static int maxLengthUsingLIS(Pair[] pairs){
        int n = pairs.length;
        Arrays.sort(pairs, (p1, p2) -> {return p1.first < p2.first ? -1 : 1;}); // sort based on first element

        int[] lis = new int[n];
        Arrays.fill(lis, 1);
        for (int i = 1; i < n; i++) {
            for (int j = 0; j < i; j++) {
                if(pairs[i].first > pairs[j].second)
                    lis[i] = Math.max(lis[i], lis[j]+1);
            }
        }
        return Arrays.stream(lis).max().getAsInt();
    }

    static int maxLengthUsingActivitySelectionProblem(Pair[] pairs){ // O(nlogn)
        List<ActivitySelectionProblem.Activity> activityList =
                Arrays.stream(pairs).map(pair -> new ActivitySelectionProblem.Activity(pair.first, pair.second)).collect(Collectors.toList());
        return ActivitySelectionProblem.maxActivityPerformed(activityList).size();
    }

}


/**
 Given an array of N integers arr[] where each element represents the max length of the jump that can be made forward from that element.
 Find the minimum number of jumps to reach the end of the array (starting from the first element).
 If an element is 0, then you cannot move through that element.
 Note: Return -1 if you can't reach the end of the array.
 Input:
 N = 11
 arr[] = {1, 3, 5, 8, 9, 2, 6, 7, 6, 8, 9}
 Output: 3
 Explanation:
 First jump from 1st element to 2nd
 element with value 3. Now, from here
 we jump to 5th element with value 9,
 and from here we will jump to the last.

 Input :
 N = 6
 arr = {1, 4, 3, 2, 6, 7}
 Output: 2
 Explanation:
 First we jump from the 1st to 2nd element
 and then jump to the last element.
 * */
class MinNumberOfJumpsProblem{
    public static void main(String[] args) {
        System.out.println(minJumps(new int[]{1, 3, 5, 8, 9, 2, 6, 7, 6, 8, 9}));
        System.out.println(minJumps(new int[]{1, 4, 3, 2, 6, 7}));
        System.out.println(minJumps(new int[]{1,0,0,1,2}));
    }
    static int minJumps(int[] arr){ //O(N^2)
        int n =  arr.length;
        int[] minJumps =  new int[n];
        Arrays.fill(minJumps, Integer.MAX_VALUE);
        minJumps[0] = 0;
        for (int i = 1; i < n; i++) {
            for (int j = 0; j < i; j++) {
                if(j+arr[j] >= i){
                    minJumps[i] = Math.min(minJumps[i],1+minJumps[j]);
                }
            }
        }
        return Arrays.stream(minJumps).max().getAsInt();
    }

    static int minJumpOptimised(int[] arr){ // O(N)
        int n =  arr.length;
        if (n <= 1)
            return 0;
        if (arr[0] == 0)
            return -1;
        int cnt = 1, maxReach = arr[0], steps = arr[0];
        for (int i = 1; i < n; i++) {
            if (i == n-1)
                return cnt;
            maxReach = Math.max(maxReach, i+arr[i]);
            steps--; // consume a step
            if (steps == 0){
                cnt++;
                if (i >= maxReach)
                    return -1;
                steps = maxReach-i;
            }
        }
        return -1;
    }
}


/**
 You are given a set of N types of rectangular 3-D boxes, where the ith box has height h, width w and length l.
 Your task is to create a stack of boxes which is as tall as possible,
 but you can only stack a box on top of another box if the dimensions of the 2-D base of the lower box are each strictly larger than those of the 2-D base of the higher box.
 Of course, you can rotate a box so that any side functions as its base.It is also allowable to use multiple instances of the same type of box.

 n = 4
 height[] = {4,1,4,10}
 width[] = {6,2,5,12}
 length[] = {7,3,6,32}
 Output: 60
 Explanation: One way of placing the boxes is
 as follows in the bottom to top manner:
 (Denoting the boxes in (l, w, h) manner)
 (12, 32, 10) (10, 12, 32) (6, 7, 4)
 (5, 6, 4) (4, 5, 6) (2, 3, 1) (1, 2, 3)
 Hence, the total height of this stack is
 10 + 32 + 4 + 4 + 6 + 1 + 3 = 60.
 No other combination of boxes produces a
 height greater than this.
 * */
class BoxStackProblem{
    public static void main(String[] args) {
        System.out.println(maxHeightBoxStack(new int[][]{{50,45,20},{95,37,53},{45,23,12}}));
        System.out.println(maxHeightBoxStack(new int[][]{{38,25,45},{76,35,3}}));
    }
    static class Box implements Comparable<Box> {
        int l;
        int w;
        int h;
        // assume that length > width
        Box(int l, int w, int h){
            this.l = l;
            this.w = w;
            this.h = h;
        }

        Box(int[] box){
            validateBox();
            this.l = box[0];
            this.w = box[1];
            this.h = box[2];
        }

        void validateBox(){
            return;
        }

        // sort in decreasing order
        @Override
        public int compareTo(Box o) {
            return o.l*o.w -this.l*this.w;
        }

        @Override
        public String toString() {
            return "Box{" +
                    "l=" + l +
                    ", w=" + w +
                    ", h=" + h +
                    '}';
        }
    }

    static int maxHeightBoxStack(int[][] arr){
        int n = arr.length; // number of box provided

        // make possible boxes configuration n -> 3n
        Box[] boxes = new Box[3*n];
        for (int i = 0; i < n;i++) {
            Box box = new Box(arr[i]);
            // original
            boxes[3*i+0] = new Box(Math.max(box.l, box.w), Math.min(box.l, box.w), box.h); // no rotation
            boxes[3*i+1] = new Box(Math.max(box.l, box.h), Math.min(box.l, box.h), box.w); // first rotaion +90
            boxes[3*i+2] = new Box(Math.max(box.w, box.h), Math.min(box.w, box.h), box.l); // second rotaion -90
        }
        // sort the boxes acc to their base area
        Arrays.sort(boxes);


        //prepare 2 array heights[] --> LIS and result[]
        int cnt = 3*n;
        int lis[] = new int[cnt];
        for (int i = 0; i < cnt; i++) { // initialize lis with height of each box, with worst case no box can be stacked
            lis[i] = boxes[i].h;
        }
//         perfrom LIS on height arr based on area
        for (int i = 1; i < cnt; i++) {
            Box currBox = boxes[i];
            for (int j = 0; j < i; j++) {
                Box prevBox =  boxes[j];
                if(currBox.l < prevBox.l && currBox.w < prevBox.w){  // include height also if needed
                    lis[i] = Math.max(lis[i], lis[j]+currBox.h);
                }
            }
        }
        return Arrays.stream(lis).max().getAsInt();
    }

}