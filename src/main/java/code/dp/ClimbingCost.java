package code.dp;

import java.util.Arrays;

/**
 Given N non-negative integers which signifies the cost of the moving from each stair.
 Paying the cost at i-th step, you can either climb one or two steps.
 Given that one can start from the 0-the step or 1-the step,
 the task is to find the minimum cost to reach the top of the floor(N+1) by climbing N stairs.
 Input: cost[] = {16, 19, 10, 12, 18 }
 Output: 31
 Start from 19 and then move to 12.

 Input: a[] = {2, 5, 3, 1, 7, 3, 4}
 Output: 9
 2->3->1->3
 * */
public class ClimbingCost {
    public static void main(String[] args) {
        System.out.println(minClimbingCost(5, new int[]{16, 19, 10, 12, 18}));
        System.out.println(minClimbingCost(7, new int[]{2, 5, 3, 1, 7, 3, 4}));
        System.out.println(minCost(new int[]{16, 19, 10, 12, 18}));
        System.out.println(minCost(new int[]{2, 5, 3, 1, 7, 3, 4}));
    }
    static int minClimbingCost(int N, int[] cost){
        int[] expense = new int[N];
        expense[0] = cost[0];
        expense[1]= cost[1];
        for (int i = 2; i < N; i++) {
            expense[i] = Math.min(expense[i-2], expense[i-1])
                    +cost[i];
        }
        return Math.min(expense[N-1], expense[N-2]);
    }

    static int minCost(int[] cost){
        int n = cost.length;
        if (n ==0)
            return 0;
        if (n == 1)
            return cost[0];
        int[] minCost =  new int[n];
        minCost[0] =  cost[0];
        minCost[1]  = cost[1];
        for (int i = 2; i < n; i++) {
            minCost[i] = Math.min(minCost[i-1], minCost[i-2])+cost[i];
        }
        return Math.min(minCost[n-1], minCost[n-2]);
    }
}

/**
 Given N leaves numbered from 1 to N . A caterpillar at leaf 1, jumps from leaf to leaf in multiples of Aj (Aj, 2Aj, 3Aj).
 j is specific to the caterpillar. Whenever a caterpillar reaches a leaf, it eats it a little.
 You have to find out how many leaves, from 1 to N, are left uneaten after all K caterpillars have reached the end.
 Each caterpillar has its own jump factor denoted by Aj, and each caterpillar starts at leaf number 1.
 N=10 K=3
 arr[] = {2, 3, 5}
 Output: 2
 Explanation: The leaves eaten by the first
 caterpillar are (2, 4, 6, 8, 10). The leaves
 eaten by the second caterpillar are (3, 6, 9).
 The leaves eaten by the third caterpillar
 are (5, 10). Ultimately, the uneaten leaves are
 1, 7 and their number is 2.
 * */
class JumpingCaterpillar{
    public static void main(String[] args) {
        System.out.println(findUneatenLeafCnt(10, new int[]{2,3,5}));
        System.out.println(findUneatenLeafCntOptimised(10, new int[]{2,3,5}));
        System.out.println(unEatenLeaf(10, new int[]{2,3,5}));
    }
    static int findUneatenLeafCnt(int n, int[] arr){ // O(N*K)
        Boolean[] leafs =  new Boolean[n+1];
        for (int i = 0; i < arr.length; i++) {
            int f =  arr[i];
            for (int j = 1; j <= n; j++) {
                if (j%f == 0)
                    leafs[j] = true;
            }
        }
        return (int) Arrays.stream(leafs).filter(flag -> flag == null).count()-1;
    }

    static int findUneatenLeafCntOptimised(int n, int[] arr){ // O(k^2)
        int k = arr.length;
        int cnt = n; // Suppose all leaves are uneaten
        // reduce cnt which divide n
        for (int i = 0; i < k; i++) {
            cnt -= n/arr[i];
        }

        // increase cnt for cases where two factor insect, since it's reduced both the time
        for (int i = 0; i < k; i++) {
            for (int j = i+1; j < k; j++) {
                cnt += n/(arr[i]*arr[j]);
            }
        }
        return cnt;

    }

    static int unEatenLeaf(int n, int arr[]){
        int l =  arr.length;
        int cnt = n;
        for (int i = 0; i < l; i++) {
            cnt -= n/arr[i];
        }

        for (int i = 0; i < l; i++) {
            for (int j = i+1; j < l; j++) {
                cnt += n/(arr[i]*arr[j]);
            }
        }
        return cnt;
    }
}
