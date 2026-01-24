package leetcode.array;

import utils.DSAUtils;

public class SmallestPositiveMissing {
    public static void main(String[] args) {
        System.out.println(smallestPositiveMissing2(new int[]{1,2,0}));
        System.out.println(smallestPositiveMissing2(new int[]{3,4,-1,1}));
        System.out.println(smallestPositiveMissing2(new int[]{7,8,9,11,12}));
    }

    public static int smallestPositiveMissing(int[] arr){
        int n = arr.length;
        for (int num = 0; num < n+1; num++) {
            boolean found =  false;
            for (int i = 0; i < n; i++) {  // can use HashMap here (n^2 -> n)
                if (arr[i] == num)
                    found = true;
            }
            if (found == false)
                return num;
        }
        return n+1;
    }


    public static int smallestPositiveMissing2(int[] nums) {
        int n = nums.length;
        // cyclic sort -> put the number at its correct position
        // num -> [1.. n]
        // num not at it's correct position
        for (int i = 0; i < n; i++) {
            while (nums[i] >= 1 && nums[i] <= n && nums[i] != nums[nums[i]-1]){
                int correctIndex = nums[i]-1;
                DSAUtils.swap(nums, i, correctIndex);
            }
        }

        // check for the first element not at it's correct position
        for (int num = 1; num <= n; num++) {
            if (num != nums[num-1])
                return num;
        }
        return n+1;
    }
}

class DuplicateNumber{
    public static void main(String[] args) {
        System.out.println(findDuplicate(new int[]{1,3,4,2,2}));
        System.out.println(findDuplicate(new int[]{3,1,3,4,2}));
        System.out.println(findDuplicate(new int[]{3,3,3,3,3}));

        // edge case
        System.out.println(findDuplicate(new int[]{1,2,3}));
        System.out.println(findDuplicate(new int[]{}));

    }

    static int findDuplicate(int[] nums){
        for (int i = 0; i < nums.length; i++) {
            int index =  Math.abs(nums[i])-1;
            if (nums[index] < 0)
                return index+1;
            nums[index] *= -1;
        }
        return -1;
    }
}
