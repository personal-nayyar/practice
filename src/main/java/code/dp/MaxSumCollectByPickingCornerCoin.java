package code.dp;

/**
 You are given an array A of size N. The array contains integers and is of even length.
 The elements of the array represent N coin of values V1, V2, ....Vn.
 You play against an opponent in an alternating way.
 In each turn, a player selects either the first or last coin from the row, removes it from the row permanently, and receives the value of the coin.
 You need to determine the maximum possible amount of money you can win if you go first.
 Note: Both the players are playing optimally.

 N = 4
 A[] = {5,3,7,10}
        i      j
 Output: 15
 Explanation: The user collects maximum
 value as 15(10 + 5)
 N = 4
 A[] = {8,15,3,7}
        i      j
 Output: 22
 Explanation: The user collects maximum
 value as 22(7 + 15)
 * */

class MaxSumCollectByPickingCornerCoin{
    public static void main(String[] args) {
        System.out.println(maxSumCollectByPickingCornerCoin(new int[]{8,15,3,7}));
        System.out.println(maxSumCollectByPickingCornerCoin(new int[]{5,3,7,10}));

        System.out.println(maxCollectCornerCoins(new int[]{8,15,3,7}));
        System.out.println(maxCollectCornerCoins(new int[]{5,3,7,10}));
    }

    public static int maxSumCollectByPickingCornerCoin(int[] arr){
        int n = arr.length;
        int[][]  table =  new int[n][n];
        int gap, i, j, x, y, z;

        for (int k = 0; k < n; k++) { // let's say number of coin available [coin to consider]
            for (i = 0, j= k; j < n; i++, j++) {
                // if you pick ith index
                // opp can pick only i+1, j index
                x = (i+2) <= j ? table[i + 2][j]: 0; // if opp pick i+1 index
                y = (i+1) <= j-1 ? table[i + 1][j - 1]: 0; // if opp pick jth index

                // if you pick jth index
                // opp can pick only i, j-1 index
                y = (i+1) <= j-1 ? table[i + 1][j - 1]: 0; // if opp pick ith index
                z = i <= (j-2) ? table[i][j - 2]: 0; // if opp pick j-1 index

                // if both the player play optimally, you'll leave the opponent with minimum value
                table[i][j] = Math.max(
                        arr[i] + Math.min(x, y), // you pick ith index
                        arr[j] + Math.min(y, z) // you pick jth index
                );
            }
        }
        return table[0][n-1];
    }

    static int maxCollectCornerCoins(int[] arr){
        return maxCollectCornerCoinsUtil(arr, 0, arr.length-1);
    }

    static int maxCollectCornerCoinsUtil(int[] arr, int i, int j){ // O(n^2)
        if(i>j)  // no coin left
            return 0;
        if(i==j) // only one coin left
            return arr[i];
        if(j==i+1) // only two coin left
            return Math.max(arr[i], arr[j]);
        return Math.max(
                arr[i] // p1 chosen i
                +Math.min(
                        maxCollectCornerCoinsUtil(arr, i+2, j), // if opponent choose i+1
                        maxCollectCornerCoinsUtil(arr, i+1, j-1) // if opponent choosen j  // repeating
                ),
                arr[j] // p1 chosen j
                +Math.min(
                        maxCollectCornerCoinsUtil(arr, i+1, j-1) // if opp choosen i   // repeating
                        ,maxCollectCornerCoinsUtil(arr, i, j-2) // if opp chosen j-1
                )
        );
    }

    static int[][] dp;
    static int maxCollectCornerCoinsDp(int[] arr){
        int n = arr.length;
        dp = new int[n+1][n+1];
        return maxCollectCornerCoinsDpUtil(arr, 0, n-1);
    }

    static int maxCollectCornerCoinsDpUtil(int[] arr, int i, int j){ // O(n^2)
        if(i>j) // no coin left
            return 0;
        if(i==j) //
            return arr[i];
        if(dp[i][j]  != -1) // already computed
            return dp[i][j];
        dp[i][j] =  Math.max(
                arr[i] // p1 choosen i
                        +Math.min(
                        maxCollectCornerCoinsUtil(arr, i+2, j), // if opponent choose i+1
                        maxCollectCornerCoinsUtil(arr, i+1, j-1) // if opponent choosen j  // repeating
                ),
                arr[j]
                        +Math.min(
                        maxCollectCornerCoinsUtil(arr, i+1, j-1) // if opp choosen i   // repeating
                        ,maxCollectCornerCoinsUtil(arr, i, j-2) // if opp chosen j-1
                )
        );
        return dp[i][j];
    }
}