package code.greedy;

/**
 You are given an array prices where prices[i] is the price of a given stock on the ith day.
 You want to maximize your profit by choosing a single day to buy one stock and choosing a different day in the future to sell that stock.
 Return the maximum profit you can achieve from this transaction. If you cannot achieve any profit, return 0.

 prices[] = {100, 180, 260, 310, 40, 535, 695}
 (i) maxProfit if allowed make single txn
 (ii) maxProfit if allowed to make any number of txn
 (iii) maxProfit if allowed to make at most 2 number of txn
 (iv) maxProfit if allowed to make at most K number of txn
 */
public class MaxProfitStockNSell {

    public static void main(String[] args) {
        System.out.println(maxProfitSingleSell(new int[]{100, 180, 260, 310, 40, 535, 695}));
        System.out.println(maxProfitSingleSell(new int[]{7,1,5,3,6,4}));
        System.out.println(maxProfitSingleSell(new int[]{7,6,4,3,1}));
        System.out.println("=====================================");

        System.out.println(maxProfitSingleSellOptimised(new int[]{100, 180, 260, 310, 40, 535, 695}));
        System.out.println(maxProfitSingleSellOptimised(new int[]{7,1,5,3,6,4}));
        System.out.println(maxProfitSingleSellOptimised(new int[]{7,6,4,3,1}));
        System.out.println("=====================================");

        System.out.println(maxProfitMultipleSell(new int[]{100, 180, 260, 310, 40, 535, 695}));
        System.out.println(maxProfitMultipleSell(new int[]{7,1,5,3,6,4}));
        System.out.println(maxProfitMultipleSell(new int[]{7,6,4,3,1}));
        System.out.println("=====================================");

    }
    static int maxProfitSingleSell(int[] stock){
        int n = stock.length;
        int maxProfit = 0;
        for (int i=0;i<n;i++){
            for (int j = i+1; j < n; j++) {
                maxProfit = Math.max(maxProfit, stock[j] - stock[i]);
            }
        }
        return maxProfit;
    }

    static int maxProfitSingleSellOptimised(int[] stock){
        int n = stock.length;
        int maxProfit = 0;
        int minPrice = Integer.MAX_VALUE;
        for (int i=0;i<n;i++){
            minPrice = Math.min(minPrice, stock[i]);
            maxProfit = Math.max(maxProfit, stock[i] - minPrice);
        }
        return maxProfit;
    }

    static int maxProfitMultipleSell(int[] stock){
        int n = stock.length;
        int maxProfit = 0;
        for(int i=0;i<n;i++){
            int j = i;
            while (j < n-1 && stock[j] < stock[j+1]){
                j++;
            }
            maxProfit += stock[j] - stock[i];
            i= j;
        }
        return maxProfit;
    }
}


/**
 * Max difference arr[j] - arr[i] s.t. j > i
 * Max difference j-i such that arr[j] > arr[i]
 * */
class MaximumIndexJMinusI {
    public static void main(String[] args) {
        int[] arr = new int[]{34, 8, 10, 3, 2, 80, 5, 30, 33, 1};
        System.out.println(maxIndex(arr));
        System.out.println(maxIndexOptimised(arr));
        System.out.println();
    }

    static int maxIndex(int[] arr){
        int n = arr.length;
        int maxLen = 0;
        for (int i = 0; i < n; i++) {
            for (int j = i+1; j < n; j++) {
                if (arr[j] > arr[i]){
                     maxLen =  Math.max(maxLen,j-i);
                }
            }
        }
        return maxLen;
    }

    static int maxIndexOptimised(int[] arr){
        int n = arr.length;
        int maxLen =  0;
        int[] leftMin = new int[n];
        int[] rightMax = new int[n];
        leftMin[0] = arr[0];
        for (int i=1;i<n;i++){
            leftMin[i] =  Math.min(leftMin[i-1], arr[i]);
        }
        rightMax[n-1] = arr[n-1];
        for (int i = n-2; i >=0 ; i--) {
            rightMax[i]= Math.max(rightMax[i+1], arr[i]);
        }
        int i=0,j=0;
        while(i<n && j<n){
            if(leftMin[i] < rightMax[j]){
                maxLen = Math.max(maxLen, j-i);
                j++;
            }else{
                i++;
            }
        }
        return maxLen;

        /*
        *            {34,  8, 10,  3,  2, 80, 5, 30, 33, 1}
        * leftMin =  {34,  8,  8,  3,  2,  2, 2,  2,  2,  1}
        * rightMax = {80, 80, 80, 80, 80, 80, 33, 33, 33, 1}
        * */
    }
}
