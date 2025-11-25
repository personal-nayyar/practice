package code.array.divive_n_conquer.BinarySearch;

import java.util.Arrays;

/**
 https://leetcode.com/problems/search-a-2d-matrix/description/?envType=problem-list-v2&envId=binary-search
 You are given an m x n integer matrix matrix with the following two properties:

 Each row is sorted in non-decreasing order.
 The first integer of each row is greater than the last integer of the previous row.
 Given an integer target, return true if target is in matrix or false otherwise.

 You must write a solution in O(log(m * n)) time complexity.
 Input: matrix = [[1,3,5,7],[10,11,16,20],[23,30,34,60]], target = 3
 Output: true
 * */
public class MatrixSearch {
    public static void main(String[] args) {
        int[][] matrix = new int[][]{
                {1,3,5,7},
                {10,11,16,20},
                {23,30,34,60}
        };
        System.out.println(searchMatrix(matrix, 3));
        System.out.println(searchMatrix(matrix, 13));
    }

    static boolean searchMatrix(int[][] matrix, int target) {
        int row = matrix.length, col = matrix[0].length;
        return searchMatrixUtil(matrix, 0, row-1, target);
    }

    static boolean searchMatrixUtil(int[][] matrix, int lowRow, int highRow, int target){
        if(lowRow <= highRow){
            int mid = highRow- (lowRow+highRow)/2;
            if (matrix[mid][0] < target && target < matrix[mid][matrix[0].length-1]) {
                return Arrays.binarySearch(matrix[mid], target) >=0;
            } else if (target < matrix[mid][0]){
                return searchMatrixUtil(matrix, lowRow, mid-1, target);
            }
            else{
                return searchMatrixUtil(matrix, mid+1, highRow, target);
            }
        }
        return false;
    }
}

/**
 https://leetcode.com/problems/search-a-2d-matrix-ii/solutions/2324616/javac-full-visuall-explanation-by-hi-mal-701h/
 Write an efficient algorithm that searches for a value target in an m x n integer matrix matrix. This matrix has the following properties:

 Integers in each row are sorted in ascending from left to right.
 Integers in each column are sorted in ascending from top to bottom.

 * */

class MatrixSearch2{
    public static void main(String[] args) {

    }

    static boolean searchMatrix(int[][] matrix, int target){
        int row =  matrix.length, col = matrix[0].length;
        boolean found = false;
        for (int i = 0; i < row; i++) {
            if (matrix[i][0] > target && target < matrix[i][col-1]){
                found =  Arrays.binarySearch(matrix[i], target) ==  -1;
                if (found)
                    return found;
            }
        }
        return false;
    }

    static boolean searchMatrixUtil(int[][] matrix, int target){
        int m = matrix.length;
        int n = matrix[0].length;

        int row = 0;        // start from top-right
        int col = n - 1;

        while (row < m && col >= 0) {
            int value = matrix[row][col];

            if (value == target) {
                return true;     // found target
            } else if (value > target) {
                col--;           // move left
            } else {
                row++;           // move down
            }
        }
        return false; // not found
    }
}