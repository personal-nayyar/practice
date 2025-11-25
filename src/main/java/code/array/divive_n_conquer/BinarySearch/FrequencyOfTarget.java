package code.array.divive_n_conquer.BinarySearch;

import java.util.PriorityQueue;

/**
 * https://leetcode.com/problems/find-first-and-last-position-of-element-in-sorted-array/description/
 * Given an array of integers nums sorted in non-decreasing order, find the starting and ending position of a given target value.
 *
 * If target is not found in the array, return [-1, -1].
 *
 * You must write an algorithm with O(log n) runtime complexity.
* */
public class FrequencyOfTarget {
    public static void main(String[] args) {
        int[] nums = {5,7,7,8,8,10};
        int target = 8;
        int first = firstOccurrence(nums, target);
        int last = lastOccurrence(nums, target);
        System.out.println(last-first);
    }

    private static int firstOccurrence(int[] nums, int target) {
        int low = 0, high = nums.length - 1, res = -1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (nums[mid] == target) {
                res = mid;
                high = mid - 1;  // continue searching left
            } else if (nums[mid] < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return res;
    }

    private static int lastOccurrence(int[] nums, int target) {
        int low = 0, high = nums.length - 1, res = -1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (nums[mid] == target) {
                res = mid;
                low = mid + 1;   // continue searching right
            } else if (nums[mid] < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return res;
    }
}

/**
 https://leetcode.com/problems/kth-smallest-element-in-a-sorted-matrix/description/
 Given an n x n matrix where each of the rows and columns is sorted in ascending order, return the kth smallest element in the matrix.
 Note that it is the kth smallest element in the sorted order, not the kth distinct element.
 You must find a solution with a memory complexity better than O(n2).

 Example 1:
 Input: matrix = [[1,5,9],[10,11,13],[12,13,15]], k = 8
 Output: 13
 Explanation: The elements in the matrix are [1,5,9,10,11,12,13,13,15], and the 8th smallest number is 13
 Example 2:

 Input: matrix = [[-5]], k = 1
 Output: -5
 * */

class MatrixSearch3{
    public static void main(String[] args) {
//        int[][] matrix = {{1,5,9},{10,11,13},{12,13,15}};
//        int k = 8;
//        System.out.println(kthSmallest(matrix, k));
//        System.out.println(kthSmallestPriorityQueue(matrix, k));

//        int[][] matrix = {{-5}};
//        int k = 1;
//        System.out.println(kthSmallest(matrix, k));
//        System.out.println(kthSmallestPriorityQueue(matrix, k));

        int[][] matrix = {{10, 20, 30, 40},{15, 25, 35, 45},{27, 29, 37, 48},{32, 33, 39, 50}};
        int k = 11;
        System.out.println(kthSmallest(matrix, k));
        System.out.println(kthSmallestPriorityQueue(matrix, k));

        int[][] matrix2 = {{-10, -5, 0, 5},{-8, -3, 2, 7},{-7, 1, 3, 9}};
        int k2 = 7;
        System.out.println(kthSmallest(matrix2, k2));
        System.out.println(kthSmallestPriorityQueue(matrix2, k2));
    }

    public static int kthSmallest(int[][] matrix, int k){
        int m = matrix.length, n = matrix[0].length;
        int low = matrix[0][0], high = matrix[m-1][n-1];
        while(low < high){
            int mid =  low + (high-low)/2;
            // count element less than mid
            int count = countLesser(matrix, mid);
            if (count < k){ // search right
                low = mid + 1;
            } else {
                high = mid;
            }
        }
        return low;
    }

    private static int countLesser(int[][] matrix, int target){
        int m = matrix.length, n = matrix[0].length;
        int count = 0;
        int row = m-1, col = 0;
        while (row >= 0 && col < n) {
            if (matrix[row][col] <= target) {
                // all elements in this row up to 'row' at this column are ≤ target
                count += (row + 1);
                col++;
            } else {
                // too large, move up
                row--;
            }
        }
        return count;
    }

    static int kthSmallestPriorityQueue(int[][] matrix, int k){
        int n = matrix.length;
        PriorityQueue<int[]> minHeap = new PriorityQueue<>((a,b) -> a[0] - b[0]);
        // add first element of each row to the min heap
        for (int i = 0; i < n; i++) {
            minHeap.offer(new int[]{matrix[i][0], i, 0});
        }

        // Step 2: Pop k-1 times
        for (int i = 0; i < k-1; i++) {
            int[] current = minHeap.poll();
            int row = current[1];
            int col = current[2];
            if (col == n-1){
                continue;
            }
            minHeap.offer(new int[]{matrix[row][col+1], row, col+1});
        }
        return minHeap.peek()[0];
    }

}
