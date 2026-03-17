package A_interview_experiences.adobe;

public class MaxWaterContainerArea {
    static class Solution {

        public int maxArea(int[] height) {

            int left = 0;                 // start pointer
            int right = height.length-1;  // end pointer

            int maxArea = 0;

            while (left < right) {

                // height limited by shorter wall
                int h = Math.min(height[left], height[right]);

                // width between lines
                int w = right - left;

                int area = h * w;

                maxArea = Math.max(maxArea, area);

                // move smaller height pointer
                if (height[left] < height[right]) {
                    left++;      // try bigger height on left
                } else {
                    right--;     // try bigger height on right
                }
            }

            return maxArea;
        }
    }
}
