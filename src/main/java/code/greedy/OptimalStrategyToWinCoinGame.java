package code.greedy;

class OptimalStrategyToWinCoinGame {
    public static void main(String[] args) {
        System.out.println(optimalStrategyToWinGame(new int[]{5,3,7,10}));
        System.out.println(optimalStrategyToWinGame(new int[]{8,15,3,7}));

    }
    static String optimalStrategyToWinGame(int[] arr){
        int n = arr.length;
        int evenSum =0, oddSum =0;
        for (int i = 0; i < n; i++) {
            if (i%2==0)
                evenSum += arr[i];
            else
                oddSum += arr[i];
        }
        return evenSum > oddSum ? "even_idx" : "odd_idx";
    }
}