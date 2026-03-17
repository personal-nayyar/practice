package A_interview_experiences.adobe;

public class RotateArrayKTimes {
    public void rotate(int[] nums, int k) {

        int n = nums.length;

        k = k % n; // handle k > n

        // Step1: reverse entire array
        reverse(nums, 0, n - 1);

        // Step2: reverse first k elements
        reverse(nums, 0, k - 1);

        // Step3: reverse remaining elements
        reverse(nums, k, n - 1);
    }

    // helper function to reverse part of array
    private void reverse(int[] nums, int left, int right) {

        while (left < right) {

            int temp = nums[left];  // swap elements
            nums[left] = nums[right];
            nums[right] = temp;

            left++;
            right--;
        }
    }
}