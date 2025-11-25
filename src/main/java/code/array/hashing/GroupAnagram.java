package code.array.hashing;

import java.util.*;

/**
 * Given an array of strings strs, group the anagrams together. You can return the description in any order.
 * Example 1:
 * Input: strs = ["eat","tea","tan","ate","nat","bat"]
 * output: [["bat"],["nat","tan"],["ate","eat","tea"]]
 *
 * Example 2:
 * Input: strs = [""]
 * Output: [[""]]
 */
public class GroupAnagram {
    public static void main(String[] args) {
        List<String> input = new ArrayList(){{
            add("Eat");
            add("Tea");
            add("Tan");
            add("ate");
            add("nat");
            add("bat");
        }};
        List<List<String>> result =  groupAnagram(input);
        System.out.println(result);
    }
    static List<List<String>> groupAnagram(List<String> list){
        Map<String, List<String>> map = new HashMap<>();
        for (String word : list) {
            int[] count = new int[26];
            for (char c : word.toCharArray()) {
                count[c - 'a']++;
            }
            // Build unique key from frequency
            StringBuilder sb = new StringBuilder();
            for (int num : count) {
                sb.append("#").append(num); // avoid ambiguity
            }
            String key = sb.toString();

            map.computeIfAbsent(key, k -> new ArrayList<>()).add(word);
        }

        return new ArrayList<>(map.values());
    }
}
