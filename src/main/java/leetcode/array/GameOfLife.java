package leetcode.array;

import utils.DSAUtils;

public class GameOfLife {
    public static void main(String[] args) {
        int[][] board = new int[][]
                {
                        {0,1,0},
                        {0,0,1},
                        {1,1,1},
                        {0,0,0}
                };
//        lifeGame(board);
//        lifeGame2(board);
        board = new int[][]
                {
                        {1,1},
                        {1,0}
                };
//        lifeGame(board);
//        lifeGame2(board);

        board = new int[][]
                {
                        {0,1,0},
                        {0,0,1},
                        {1,1,1},
                        {0,0,0}
                };

//        lifeGame(board);
//        lifeGame2(board);

        board = new int[][]{{0,0,0,0,0,0},{0,0,0,0,0,0},{0,0,1,1,1,0},{0,1,1,1,0,0},{0,0,0,0,0,0},{0,0,0,0,0,0}};
        lifeGame2(board);
    }

    public static void lifeGame(int[][] board){
        int rows =  board.length;
        int cols = board[0].length;
        // deep copy 2d array
        int[][] cloned = new int[rows][];
        for (int i = 0; i < rows; i++) {
            cloned[i] = board[i].clone();
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                // check neighbours i-1,j, i+1,j, i,j-1, i,j+1, i-1,j-1 i-1,j+1 i+1,j-1 i+1,j+1
                if (board[i][j] == 1){ // live, check live neighbours
                    int liveCount = countLiveNeighbours(board, i, j, rows, cols);
                    if (liveCount < 2)
                        cloned[i][j] =  0;
                    else if (liveCount > 3)
                        cloned[i][j] = 0;
                } else if (board[i][j] == 0){// died, check for live neighbours
                    int liveCount = countLiveNeighbours(board, i, j, rows, cols);
                    if (liveCount == 3)
                        cloned[i][j] = 1;
                }
            }
        }
        board =  cloned;
        DSAUtils.print2DArray(board);
    }

    private static int countLiveNeighbours(int[][] board, int i, int j, int rows, int cols){
        int count = 0;
        count += checkBoundary(i-1, j, rows, cols) && board[i-1][j] == 1 ? 1: 0;
        count += checkBoundary(i+1, j, rows, cols) && board[i+1][j] == 1 ? 1: 0;
        count += checkBoundary(i, j-1, rows, cols) && board[i][j-1] == 1 ? 1: 0;
        count += checkBoundary(i, j+1, rows, cols) && board[i][j+1] == 1 ? 1: 0;
        count += checkBoundary(i-1, j-1, rows, cols) && board[i-1][j-1] == 1 ? 1: 0;
        count += checkBoundary(i-1, j+1, rows, cols) && board[i-1][j+1] == 1 ? 1: 0;
        count += checkBoundary(i+1, j-1, rows, cols) && board[i+1][j-1] == 1 ? 1: 0;
        count += checkBoundary(i+1, j+1, rows, cols) && board[i+1][j+1] == 1 ? 1: 0;
        return count;
    }


    public static void lifeGame2(int[][] board){
        int rows = board.length;
        int cols = board[0].length;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int liveNeighbours = countLiveNeighbours2(board, i, j, rows, cols);
                if (board[i][j] == 1 || board[i][j] == 2){ // live
                    if (liveNeighbours < 2 || liveNeighbours > 3)
                        board[i][j] = 2; // represent 1 -> 0 (live --> dead)
                } else if (board[i][j] ==  0 || board[i][j] == -2){
                    if (liveNeighbours == 3)
                        board[i][j] = -2; // represent 0 -> 1 (dead --> live)
                }
            }
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (board[i][j] == 2) // Marked dead (alive -> dead)
                        board[i][j] = 0;
                else if (board[i][j] == -2)
                        board[i][j] = 1;
            }
        }

        DSAUtils.print2DArray(board);
    }



    private static int countLiveNeighbours2(int[][] board, int i, int j, int rows, int cols){
        int count = 0;
        int[][] dirs = {{-1,-1}, {-1,0}, {-1,1}, {0,-1}, {0,1}, {1,-1}, {1,0}, {1,1}};
        for (int[] dir: dirs){
            int r = i+dir[0];
            int c = j+dir[1];
            if (checkBoundary(r,c, rows, cols) && (board[r][c] == 1 || board[r][c] == 2))
                count++;
        }
        return count;
    }

    private static boolean checkBoundary(int i, int j, int rows, int cols){
        return  (i >=0 && i < rows) && (j >=0 && j < cols);
    }
}
