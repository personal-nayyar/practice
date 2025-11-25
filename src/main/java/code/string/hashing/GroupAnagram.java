package code.string.hashing;

import java.util.*;

/**
 Given two strings. The task is to check whether given strings are anagrams of each other or not.
 Input: str1 = “listen”  str2 = “silent”
 Output: “Anagram”
 Explanation: All characters of “listen” and “silent” are the same.

 Input: str1 = “gram”  str2 = “arm”
 Output: “Not Anagram”
 * */
class AnagramValidator{
    static final int NO_OF_CHARACTER =  256;
    static boolean checkAnagrams(String str1, String str2){
        int freq[] = new int[NO_OF_CHARACTER];
        if (str1.length() != str2.length())
            return false;

        for (int i = 0; i < str1.length(); i++) {
            freq[str1.charAt(i)]++;
            freq[str2.charAt(i)]--;
        }

        for (int i = 0; i < NO_OF_CHARACTER; i++) {
            if(freq[i] >0)
                return false;
        }
        return true;
    }
}

/**
 Given an array of strings strs, group the anagrams together. You can return the description in any order.
 An Anagram is a word or phrase formed by rearranging the letters of a different word or phrase, typically using all the original letters exactly once.
 Input: strs = ["eat","tea","tan","ate","nat","bat"]
 Output: [["bat"],["nat","tan"],["ate","eat","tea"]]
 * */

// Input - ["eat","tea","tab","ate","nat","bat"]
//  - [10001..1., 10001..1, 1100..1, 10001..1, 100..1..1, 11000..1]
// Output - [["bat", "tab"],["nat"],["eat","ate", "tea"]]
public class GroupAnagram {
    public static void  solution(List<String> list) {
        int n = list.size();
        int[][] freq = new int[n][26];
        for(int i=0;i<n;i++){
            String str = list.get(i);
            for(int j=0;j<str.length();j++){
                freq[i][str.charAt(j) - 'a']++;
            }
        }
        Map<String, List<String>> map = new HashMap();
        for(int i=0;i<n;i++){
            String key = Arrays.toString(freq[i]);
            if(map.containsKey(key)){
                map.get(key).add(list.get(i));
            }else{
                map.put(key, new ArrayList<>(Arrays.asList(list.get(i))));
            }
        }
        List<List<String>> result = new ArrayList();
        map.entrySet().forEach(entry -> {
            System.out.println(entry.getValue());
        });
    }

    public static void main(String[] args) {
        solution(Arrays.asList("eat","tea","tab","ate","nat","bat"));
    }
}
