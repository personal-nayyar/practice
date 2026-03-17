package A_interview_experiences.adobe;

public class RobAlternateHouse {
    class Solution {

        public int rob(int[] nums) {

            Integer[] memo = new Integer[nums.length];

            return dfs(nums, 0, memo);
        }

        private int dfs(int[] nums, int i, Integer[] memo) {

            // base case
            if (i >= nums.length)
                return 0;

            // if already computed
            if (memo[i] != null)
                return memo[i];

            // rob current house
            int rob = nums[i] + dfs(nums, i + 2, memo);

            // skip current house
            int skip = dfs(nums, i + 1, memo);

            // store result
            memo[i] = Math.max(rob, skip);

            return memo[i];
        }
    }

    class Solution2 {

        public int rob(int[] nums) {

            int prev1 = 0; // dp[i-1]
            int prev2 = 0; // dp[i-2]

            for (int money : nums) {

                // compute best choice
                int curr = Math.max(prev1, prev2 + money);

                // shift DP values
                prev2 = prev1;
                prev1 = curr;
            }

            return prev1;
        }
    }
}
