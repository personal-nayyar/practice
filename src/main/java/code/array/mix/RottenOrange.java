package code.array.mix;

import java.util.LinkedList;
import java.util.Queue;

/**
 0: Empty cell
 1: Cells have fresh oranges
 2: Cells have rotten oranges
 Input:  arr[][C] =
 { {2, 1, 0, 2, 1},
 {1, 0, 1, 2, 1},
 {1, 0, 0, 2, 1}};

 Output:
 All oranges can become rotten in 2-time frames.
 * */
public class RottenOrange {

    public static void main(String[] args) {
        int[][] arr =  { {2, 1, 0, 2, 1}, {1, 0, 1, 2, 1}, {1, 0, 0, 2, 1}};
        System.out.println(minIterationToRot(arr));
        System.out.println(minIterationToRotOptimised(arr));
    }

    static class Cell{
        int i;
        int j;
        Cell(int i, int j){
            this.i = i;
            this.j = j;
        }

        public boolean isDelim(){
            return i == -1 && j== -1;
        }
    }

    public static int minIterationToRot(int[][] arr){
        int row = arr.length;
        int col = row > 0 ? arr[0].length : 0;
        int infected = 2;
        boolean changed = false;
        while(changed == false){
            for(int i=0;i<row;i++){
                for(int j=0;j<col;j++){
                    if(arr[i][j] == infected){
                        // upper-row
                        // System.out.println("i:"+i+", j:"+j);
                        if(isSafe(i-1, j, row, col) && (arr[i-1][j] == 1)){
                            arr[i-1][j] = infected+1;
                            changed = true;
                        }


                        // bottom row
                        if(isSafe(i+1, j, row, col) && (arr[i+1][j] == 1)){
                            arr[i+1][j] = infected+1;
                            changed = true;
                        }


                        // left-col
                        if(isSafe(i, j-1, row, col)&& (arr[i][j-1] == 1)){
                            arr[i][j-1] = infected+1;
                            changed = true;

                        }

                        // right-col
                        if(isSafe(i, j+1, row, col) && (arr[i][j+1] == 1)){
                            arr[i][j+1] = infected+1;
                            changed = true;

                        }

                    }
                }
            }
            // System.out.println("infected:"+infected);
            if(changed == false) // no newly infected cell
                break;
            infected++;
            changed = false;
        }

        for(int i=0;i<row;i++){
            for(int j=0;j<col;j++){
                if(arr[i][j] == 1)
                    return -1;
            }
        }
        return infected-2;
    }

    public static boolean isSafe(int i, int j, int row, int col){
        if(i< 0 || i >= row || j <0 || j>= col )
            return false;
        return true;
    }


    public static int minIterationToRotOptimised(int[][] arr){
        int ans = 0;
        Queue<Cell> queue =  new LinkedList<>();
        int row =  arr.length, col = row > 0 ? arr[0].length : 0;
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if(arr[i][j] == 2) // rotten orange
                    queue.add(new Cell(i,j));
            }
        }
        queue.add(new Cell(-1,-1)); // delimiter

        while (!queue.isEmpty()){
            boolean changed =  false;
            while (!queue.peek().isDelim()){
                Cell cell =  queue.poll();
                int i = cell.i, j = cell.j;
                // upper row
                if(isSafe(i-1, j, row, col) && (arr[i-1][j] == 1)){
                    arr[i-1][j] = 2;
                    queue.add(new Cell(i-1,j));
                    changed = true;
                }


                // bottom row
                if(isSafe(i+1, j, row, col) && (arr[i+1][j] == 1)){
                    arr[i+1][j] = 2;
                    queue.add(new Cell(i+1,j));
                    changed = true;
                }


                // left-col
                if(isSafe(i, j-1, row, col)&& (arr[i][j-1] == 1)){
                    arr[i][j-1] = 2;
                    queue.add(new Cell(i,j-1));
                    changed = true;

                }

                // right-col
                if(isSafe(i, j+1, row, col) && (arr[i][j+1] == 1)){
                    arr[i][j+1] = 2;
                    queue.add(new Cell(i,j+1));
                    changed = true;

                }
            }
            if (changed){
                ans++;
                queue.add(new Cell(-1,-1));
            }
            queue.poll(); // remove this delimiter
        }
        return ans;
    }
}

/**
 Given a boolean matrix of size RxC where each cell contains either 0 or 1,
 modify it such that if a matrix cell matrix[i][j] is 1 then all the cells in its ith row and jth column will become 1.

 Input:
 {{1, 0, 0, 1},
 {0, 0, 1, 0},
 {0, 0, 0, 0}}
 Output:
 {{1, 1, 1, 1},
 {1, 1, 1, 1},
 {1, 0, 1, 1}}

 Expected Time Complexity: O(R * C)
 Expected Auxiliary Space: O(R + C)
 * */
class BooleanMatrixProblem{
    public static void modifyMatrix(boolean[][] mat){
        int row = mat.length;
        int col = row > 0 ? mat[0].length : 0;
        boolean[][] visited = new boolean[row][col];

        for (int i = 0; i < row; i++) {
            for (int j = 0; j <col; j++) {
                if(mat[i][j] && !visited[i][j]){
                    // row
                    for (int k = 0; k < row; k++) {
                        if (!mat[k][j]){
                            visited[k][i] = true;
                            mat[k][i] = true;
                        }
                    }
                    // col
                    for (int k = 0; k < col; k++) {
                        if (!mat[i][k]){
                            visited[i][k] = true;
                            mat[i][k] = true;
                        }
                    }
                }
            }
        }
    }

    public static void modifyMatrixOptimised(boolean[][] mat){
        int row = mat.length;
        int col = row > 0 ? mat[0].length :0;
        boolean[] rows = new boolean[row];
        boolean[] cols = new boolean[col];

        for (int i = 0; i <row; i++) {
            for (int j = 0; j < col; j++) {
                if(mat[i][j]){
                    rows[i] = true;
                    cols[j] = true;
                }
            }
        }

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if(rows[i] || cols[j])
                    mat[i][j] = true;
            }
        }
    }
}
