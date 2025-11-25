package A_interview_experiences.epam;

public class MaximumSumSubarray {
    public static void main(String[] args) {
        int[] arr =  new int[]{-2, -3, 4, -1, -2, 1, 5, -3};
        System.out.println(maxSumSubArray(arr));
    }

    public static int maxSumSubArray(int[] arr){
        int i=0, maxSum = Integer.MIN_VALUE, currSum = 0;
        while (i < arr.length){
            currSum += arr[i++];
            maxSum = Math.max(maxSum, currSum);
            if (currSum <  0)
                currSum = 0;
        }
        return maxSum;
    }
}
