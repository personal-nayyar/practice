package A_interview_experiences.adobe;

public class NextGreaterNumber {
    class Solution {

        public int nextGreaterElement(int n) {

            char[] arr = String.valueOf(n).toCharArray();

            int i = arr.length - 2;

            // Step1: find first decreasing element from right
            while (i >= 0 && arr[i] >= arr[i + 1]) {
                i--;
            }

            if (i < 0) return -1; // already largest permutation

            int j = arr.length - 1;

            // Step2: find next greater element
            while (arr[j] <= arr[i]) {
                j--;
            }

            // Step3: swap
            char temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;

            // Step4: reverse remaining part
            reverse(arr, i + 1, arr.length - 1);

            long result = Long.parseLong(new String(arr));

            // handle integer overflow
            return result > Integer.MAX_VALUE ? -1 : (int) result;
        }

        private void reverse(char[] arr, int left, int right) {

            while (left < right) {

                char temp = arr[left];
                arr[left] = arr[right];
                arr[right] = temp;

                left++;
                right--;
            }
        }
    }
}
