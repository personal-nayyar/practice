package code.dp;

import java.util.*;

public class RottenOranges {

    public static int orangesRotting(int[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;

        Queue<int[]> queue = new LinkedList<>();
        int fresh = 0;

        // Step 1: Add all rotten oranges to queue & count fresh oranges
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == 2) {
                    queue.offer(new int[]{r, c});
                } else if (grid[r][c] == 1) {
                    fresh++;
                }
            }
        }

        if (fresh == 0) return 0; // No fresh oranges to rot

        int minutes = 0;
        int[][] directions = {{-1,0},{1,0},{0,-1},{0,1}}; // up, down, left, right

        // Step 2: BFS
        while (!queue.isEmpty()) {
            int size = queue.size();
            boolean rottedThisMinute = false;

            for (int i = 0; i < size; i++) {
                int[] curr = queue.poll();
                int r = curr[0], c = curr[1];

                for (int[] dir : directions) {
                    int nr = r + dir[0], nc = c + dir[1];
                    if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && grid[nr][nc] == 1) {
                        grid[nr][nc] = 2; // rot the orange
                        queue.offer(new int[]{nr, nc});
                        fresh--;
                        rottedThisMinute = true;
                    }
                }
            }

            if (rottedThisMinute) minutes++;
        }

        return fresh == 0 ? minutes : -1;
    }

    public static void main(String[] args) {
        int[][] grid1 = {
            {2,1,1},
            {1,1,0},
            {0,1,1}
        };
        System.out.println(orangesRotting(grid1)); // Output: 4

        int[][] grid2 = {
            {2,1,1},
            {0,1,1},
            {1,0,1}
        };
        System.out.println(orangesRotting(grid2)); // Output: -1

        int[][] grid3 = {
            {0,2}
        };
        System.out.println(orangesRotting(grid3)); // Output: 0
    }
}