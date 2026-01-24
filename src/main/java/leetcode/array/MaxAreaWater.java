package leetcode.array;

public class MaxAreaWater {
    public static void main(String[] args) {
        System.out.println(maxArea(new int[]{1,8,6,2,5,4,8,3,7}));
        System.out.println(maxArea(new int[]{1,1}));

        // edge cases
        System.out.println(maxArea(new int[]{1,2,3,4,5})); // monotonic increasing
        System.out.println(maxArea(new int[]{5,4,3,2,1})); // monotonic decreasing
        System.out.println(maxArea(new int[]{}));

        // invalid cases
        System.out.println(maxArea(new int[]{1,-2,-3,4,-5}));
    }

    public static int maxArea(int[] arr){
        int maxArea = 0, curr = 0;
        int left = 0, right = arr.length-1;
        while(left < right){
            curr = Math.min(arr[left], arr[right]) * (right-left);
            maxArea = Math.max(curr, maxArea);
            if (arr[left] < arr[right])
                left++;
            else
                right--;
        }
        return maxArea;
    }
}
