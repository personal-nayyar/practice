package code.array.sliding_window;

/**
 Given an array arr[] and an integer K, the task is to find to maximize the sum of K elements in the Array by taking only corner elements.
 Input: arr[] = {8, 4, 4, 8, 12, 3, 2, 9}, K = 3
 Output: 21
 Explanation:
 The optimal strategy is to pick the elements form the array is, two indexes from the beginning and one index from the end.
 All other possible choice will yield lesser sum. Hence, arr[0] + arr[1] + arr[7] = 21.
 * */
public class maxCollectKCornerElement {
    public static void main(String[] args) {
        System.out.println(collectMax(new int[]{2, 1, 14, 6, 4, 3}, 3));
        System.out.println(collectMax(new int[]{8,4,4,8,12,3,2,9}, 3));

        System.out.println(maxCollect(new int[]{2, 1, 14, 6, 4, 3}, 3));
        System.out.println(maxCollect(new int[]{8,4,4,8,12,3,2,9}, 3));
    }
    static int collectMax(int[] arr, int k){
        int currSum = 0, maxSum = 0;
        // pick first k element to window
        for (int i = 0; i < k; i++) {
            currSum +=  arr[i];
        }

        maxSum =  currSum;
        // now remove right most element from window and pick from right corner one by one
        int n = arr.length-1; // right most
        for (int i = k-1; i >=0 ; i--) {
            int j =  k-i-1; // right corner element
            currSum = currSum
                    -arr[i] // exclude left corner element (right most) from window
                    +arr[n-j]; // include right element
            maxSum =  Math.max(currSum, maxSum);
        }
        return maxSum;
    }

    static int maxCollect(int[] arr, int k){
        int n = arr.length;
        int sum = 0;
        for (int i = 0; i < k; i++) {
            sum += arr[i];
        }
        int maxSum = sum;
        for (int i = 0; i < k; i++) {
            sum = sum +arr[n-1-i]-arr[k-1];
            maxSum = Math.max(sum, maxSum);
        }
        return maxSum;
    }
}