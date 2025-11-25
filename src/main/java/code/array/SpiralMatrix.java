package code.array;

public class SpiralMatrix {
    public static void main(String[] args) {
        printSpiral(new int[][]{{1,2,3,4},{5,6,7,8},{9,10,11,12}});
    }

    public static void printSpiral(int[][] mat){
        int m = mat.length, n = mat[0].length;

        int top =0, left =0, bottom =m-1, right =n-1;

        while (top <= bottom && left <= right){
            // upper row
            for (int j = left; j <= right; j++) {
                System.out.print(mat[top][j]+" ");
            }
            top++;

            // side righ
            for (int i = top; i <= bottom; i++) {
                System.out.print(mat[i][right]+" ");
            }
            right--;


            // bottom
            if (top <= bottom){
                for (int j = right; j >= left; j--) {
                    System.out.print(mat[bottom][j]+" ");
                }
                bottom--;
            }


            // left
            if (left <= right){
                for (int i = bottom; i >= top; i--) {
                    System.out.print(mat[i][left]+" ");
                }
                left++;
            }
        }
    }
}
