package code.dp;

/**
 A frog jumps either 1, 2, or 3 steps to go to the top.
 In how many ways can it reach the top. As the description will be large find the description modulo 1000000007.
 or Given a distance ‘dist’, count total number of ways to cover the distance with 1, 2 and 3 steps.
 Input: n = 3
 Output: 4
 Explanation:
 Below are the four ways
 1 step + 1 step + 1 step
 1 step + 2 step
 2 step + 1 step
 3 step

 Input: n = 4
 Output: 7
 Explanation:
 Below are the four ways
 1 step + 1 step + 1 step + 1 step
 1 step + 2 step + 1 step
 2 step + 1 step + 1 step
 1 step + 1 step + 2 step
 2 step + 2 step
 3 step + 1 step
 1 step + 3 step
 * */
public class NumberOfWayToCoverADistance {
    public static void main(String[] args) {
        System.out.println(minWaysCnt(3));
        System.out.println(minWaysCnt(4));
        System.out.println(minWaysCnt(5));
        System.out.println(minWaysCnt(6));
        System.out.println(minWaysCnt(7));

    }
    static int minWaysCnt(int n){ // similar to fibonacci
        int[] ways = new int[n+1]; // ways[i] represent number of ways to reach ith step
        ways[0] = 1;
        ways[1] = 1;
        ways[2] = 2;
        for (int i = 3; i <=n; i++) {
            ways[i] = ways[i-1]+ways[i-2]+ways[i-3]; // formula
        }
        return ways[n];
    }
}


/**
 Given an array of n non-negative numbers,
 the task is to find the minimum sum of elements (picked from the array)
 such that at least one element is picked out of every 3 consecutive elements in the array.
 Input : arr[] = {1, 2, 3, 6, 7, 1}
 Output : 4
 We pick 3 and 1  (3 + 1 = 4)
 Note that there are following subarrays
 of three consecutive elements
 {1, 2, 3}, {2, 3, 6}, {3, 6, 7} and {6, 7, 1}
 We have picked one element from every subarray.
 * */
class MinSumOneEvery3Consecutive {
    static int minSum(int[] arr){
        int n = arr.length;
        int[] sum =new int[n]; // sum[i] represent min sum till ith index

        // Base cases (process first three elements) when n <= 3
        sum[0] = arr[0];
        sum[1] = arr[1];
        sum[2] = arr[2];

        // Iterate through all other elements
        for (int i = 3; i < n; i++)
            sum[i] = arr[i] + minimum(
                        sum[i - 3],
                        sum[i - 2],
                        sum[i - 1]);

        return minimum(sum[n - 1], sum[n - 2], sum[n - 3]);
    }

    static int minimum(int a, int b, int c)
    {
        return Math. min(Math.min(a, b), c);
    }
}


/**
 Given a number N. Find the minimum number of operations required to reach N starting from 0.
 You have 2 operations available:
 Double the number
 Add one to the number
 Input:
 N = 8
 Output: 4
 Explanation: 0 + 1 = 1, 1 + 1 = 2,
 2 * 2 = 4, 4 * 2 = 8
 * */
class MinOperation0toN {
    public static void main(String[] args) {
        System.out.println(minOperation(7));
        System.out.println(minOperation(8));
        System.out.println(minOperation(9));
        System.out.println(minOperation(10));
    }
    // This function calculates minimum number of operations required to reach N starting from 0.
    // It uses dynamic programming to solve this problem.
    // minOp[i] represents minimum number of operations required to reach i.
    // minOp[0] = 0 (no operations required to reach 0)
    // minOp[1] = 1 (1 operation required to reach 1: 0 + 1)
    // minOp[2] = 2 (2 operations required to reach 2: 0 + 1 + 1 or 1 + 1)
    // For each i > 2, it calculates minOp[i] based on the minimum number of operations
    // required to reach i - 1, i / 2, and i - 1:
    //   if i is odd, minOp[i] = minOp[i - 1] + 1 (1 operation is required to reach i: i - 1 + 1)
    //   if i is even, minOp[i] = minOp[i / 2] + 1 (1 operation is required to double i: i / 2 + 1)
    static int minOperation(int N){
        int minOp[] =  new int[N+1];
        minOp[0] = 0;
        minOp[1] = 1;
        minOp[2] = 2;

        for (int i = 3; i <= N; i++) {
            minOp[i] =  i%2 !=0
                    ? minOp[i-1]+1
                    : minOp[i/2]+1;
        }
        return minOp[N];
    }
}
