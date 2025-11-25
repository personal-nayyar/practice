package code.greedy;

import java.util.Arrays;
import java.util.Collections;

/**
 Sean invented a game involving a 2n*2n  matrix where each cell of the matrix contains an integer.
 He can reverse any of its rows or columns any number of times.
 The goal of the game is to maximize the sum of the elements in the n*n sub-matrix located in the upper-left quadrant of the matrix.
 Given the initial configurations for q matrices,help Sean reverse the rows and columns of each matrix in the best possible way so that the sum of the elements in the matrix's upper-left quadrant is maximal.
 * matrix:  [
 *              [112, 42, 83, 119]
 *              [56,  125, 56,  49]
 *              [15,  78, 101, 43]
 *              [62, 98, 114, 108]
 *  Reverse column  2
 *  Reverse row 0
 *  maxSum =  414
 * */
public class FlippingTheMatrixForMaxSum {
    public static void main(String[] args) {
        int[][] mat =  {
                {112, 42, 83, 119},
                {56,  125, 56,  49},
                {15,  78, 101, 43},
                {62, 98, 114, 108}};
        System.out.println(flipMatrixMaxSum(mat));
        System.out.println(maxSumFlipMat(mat));
        int[][] mat2 = {
                {112,42},
                {56,125},
        };
        System.out.println(flipMatrixMaxSum(mat2));
        System.out.println(maxSumFlipMat(mat));
    }
    public static int flipMatrixMaxSum(int[][] mat){
        int n = mat.length-1;
        int sum =0;
        for (int i = 0; i <= n/2; i++) {
            for (int j = 0; j <= n/2; j++) {
                sum += Collections.max(Arrays.asList(
                        mat[i][j],
                        mat[i][n-j], // row swap
                        mat[n-i][j], // col swap
                        mat[n-i][n-j] // row swap, col swap
                ));
            }
        }
        return sum;
    }
    static int maxSumFlipMat(int[][] mat){
        int n = mat.length/2, maxVal = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                 maxVal += Collections.max(Arrays.asList(mat[i][j], mat[i][n-1-j], mat[n-1-i][j], mat[n-1-i][n-1-i]));
            }
        }
        return maxVal;
    }
}
