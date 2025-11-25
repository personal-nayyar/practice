package code.backtracking_dp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 Given an array of distinct integers candidates and a target integer target, return a list of all unique combinations of candidates where the chosen numbers sum to target. You may return the combinations in any order.

 The same number may be chosen from candidates an unlimited number of times. Two combinations are unique if the frequency of at least one of the chosen numbers is different.

 The test cases are generated such that the number of unique combinations that sum up to target is less than 150 combinations for the given input.



 Example 1:

 Input: candidates = [2,3,6,7], target = 7
 Output: [[2,2,3],[7]]
 Explanation:
 2 and 3 are candidates, and 2 + 2 + 3 = 7. Note that 2 can be used multiple times.
 7 is a candidate, and 7 = 7.
 These are the only two combinations.
 Example 2:

 Input: candidates = [2,3,5], target = 8
 Output: [[2,2,2,2],[2,3,3],[3,5]]
 Example 3:

 Input: candidates = [2], target = 1
 Output: []
 */
public class CombinationSum {

    public static void main(String[] args) {
        Integer[] arr = {2,3,6,7};
        System.out.println(combinations(arr, 7));
    }

    static List<List<Integer>> combinations(Integer[] arr, int target){
        List<List<Integer>> result = new ArrayList<>();
        combinationsUtil(arr, target, 0, new ArrayList<>(), result);
        return result;
    }

    static void combinationsUtil(Integer[] arr, int target, int index, List<Integer> temp, List<List<Integer>> result){
        if (target < 0 || index >= arr.length)
            return;
        if (target == 0){
            result.add(new ArrayList<>(temp));
            return;
        }
        // include the current element
        temp.add(arr[index]);
        combinationsUtil(arr, target-arr[index], index, temp, result); // index pass as it is because we can use the same element multiple times

        // exclude the current element, remove the last element added for backtracking
        temp.remove(temp.size()-1);
        combinationsUtil(arr, target, index+1, temp, result);
    }
}
