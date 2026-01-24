package leetcode.array;

import java.util.ArrayList;
import java.util.List;

public class SpiralMatrix {
        public List<Integer> spiralOrder(int[][] matrix) {
        // Edge case: empty matrix - return empty list
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            return new ArrayList<>();
        }

        int m = matrix.length;  // Number of rows
        int n = matrix[0].length; // Number of columns
        
        // Result list to store spiral order elements
        List<Integer> result = new ArrayList<>(m * n); // Pre-allocate capacity for efficiency

        // Boundary pointers: track the current spiral layer
        int top = 0;      // Topmost row of current layer
        int left = 0;    // Leftmost column of current layer
        int bottom = m - 1; // Bottommost row of current layer
        int right = n - 1;  // Rightmost column of current layer

        // Continue until boundaries cross (all elements processed)
        while (top <= bottom && left <= right) {
            // Step 1: Traverse top row from left to right
            for (int j = left; j <= right; j++) {
                result.add(matrix[top][j]); // Add element to result list
            }
            top++; // Move top boundary down (this row is done)

            // Step 2: Traverse right column from top to bottom
            for (int i = top; i <= bottom; i++) {
                result.add(matrix[i][right]); // Add element to result list
            }
            right--; // Move right boundary left (this column is done)

            // Step 3: Traverse bottom row from right to left
            // Check if top <= bottom to avoid duplicate processing when only one row remains
            if (top <= bottom) {
                for (int j = right; j >= left; j--) {
                    result.add(matrix[bottom][j]); // Add element to result list
                }
                bottom--; // Move bottom boundary up (this row is done)
            }

            // Step 4: Traverse left column from bottom to top
            // Check if left <= right to avoid duplicate processing when only one column remains
            if (left <= right) {
                for (int i = bottom; i >= top; i--) {
                    result.add(matrix[i][left]); // Add element to result list
                }
                left++; // Move left boundary right (this column is done)
            }
        }
        
        return result; // Return the spiral order list
    }
    
}
