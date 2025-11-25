package code.backtracking_dp;

import java.util.ArrayList;
import java.util.List;

/**
 * Given an array of distinct integers nums, return all the possible subsets (the power set).
 * The solution set must not contain duplicate subsets. Return the solution in any order.
 * Example 1:
 * Input: nums = [1,2,3]
 * result: [[],[1],[2],[1,2],[3],[1,3],[2,3],[1,2,3]]
 */
class PowerSet{

    public static void main(String[] args) {
        List<Integer> input = new ArrayList(){{
            add(1);
            add(2);
            add(3);
        }};
        List<List<Integer>> result =  powerSetRecursive(input);
        System.out.println(result);
        List<List<Integer>> result1 =  powerSetRecursive2(input);
        System.out.println(result1);
    }

    static List<List<Integer>> powerSetRecursive(List<Integer> list){ // O(2^n)
        List<List<Integer>> result =  new ArrayList<>();
        result.add(new ArrayList<>()); // empty set
        powerSetRecursiveUtil(list, 0, list.size(), new ArrayList<>(), result);
        return result;
    }

    static void powerSetRecursiveUtil(List<Integer> list, int start, int n, List<Integer> subset, List<List<Integer>> result){
        if(start >= n){
            return;
        }
        for (int i = start; i < list.size(); i++) {
            subset.add(list.get(i));
            result.add(new ArrayList<>(subset));
            powerSetRecursiveUtil(list, i+1,n,subset, result);
            subset.remove(subset.size()-1); //backtrace
        }
    }

    //The idea is to consider two cases for every character.
    //(i) Consider current character as part of current subset
    //(ii) Do not consider current character as part of the current subset.

    static List<List<Integer>> powerSetRecursive2(List<Integer> list){ // O(2^n)
        List<List<Integer>> result =  new ArrayList<>();
        powerSetRecursiveUtil2(list, 0, list.size(), new ArrayList<>(), result);
        return result;
    }

    static void powerSetRecursiveUtil2(List<Integer> list, int start, int n, List<Integer> subset, List<List<Integer>> result){
        if(start == n){
            result.add(new ArrayList<>(subset));
            return;
        }
        List<Integer> newSet =  new ArrayList<>(subset);
        newSet.add(list.get(start));
        powerSetRecursiveUtil2(list, start+1, n, newSet, result); //(i) Consider current character as part of current subset
        powerSetRecursiveUtil2(list, start+1, n, subset, result); //(ii) Do not consider current character as part of the current subset.
    }
}
