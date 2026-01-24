package code.string.sliding_window;

/**
 Given two strings, string and pattern, the task is to find the smallest substring in string containing all characters of pattern.

 Input: string = “this is a test string”, pattern = “tist”
 Output: “t stri”
 Explanation: “t stri” contains all the characters of pattern.

 Input: string = “geeksforgeeks”, pattern = “ork”
 Output: “ksfor”

 * */
import java.util.*;

public class MinimumWindowSubstring {

    public static String findMinWindow(String str, String pattern) {
        if (str == null || pattern == null || str.length() < pattern.length()) {
            return "";
        }

        // Step 1: Frequency map of characters in pattern
        Map<Character, Integer> patternFreq = new HashMap<>();
        for (char c : pattern.toCharArray()) {
            patternFreq.put(c, patternFreq.getOrDefault(c, 0) + 1);
        }

        int required = patternFreq.size(); // unique characters to match
        int formed = 0; // how many unique chars matched with required frequency

        Map<Character, Integer> windowFreq = new HashMap<>();

        // (start index, end index) of best window found
        int minLen = Integer.MAX_VALUE;
        int minLeft = 0;

        int left = 0;
        for (int right = 0; right < str.length(); right++) {
            char c = str.charAt(right);

            // Add current char to window
            windowFreq.put(c, windowFreq.getOrDefault(c, 0) + 1);

            // If char frequency matches with pattern, increase 'formed'
            if (patternFreq.containsKey(c) &&
                    windowFreq.get(c) == patternFreq.get(c)) {
                formed++;
            }

            // Try to shrink window from left while it's valid
            while (left <= right && formed == required) {
                // Update min window if smaller found
                if (right - left + 1 < minLen) {
                    minLen = right - left + 1;
                    minLeft = left;
                }

                // Remove leftmost char
                char leftChar = str.charAt(left);
                windowFreq.put(leftChar, windowFreq.get(leftChar) - 1);

                // If frequency falls below requirement, window becomes invalid
                if (patternFreq.containsKey(leftChar) &&
                        windowFreq.get(leftChar) < patternFreq.get(leftChar)) {
                    formed--;
                }

                left++; // shrink window
            }
        }

        // If no window found, return empty string
        return minLen == Integer.MAX_VALUE ? "" : str.substring(minLeft, minLeft + minLen);
    }

    public static void main(String[] args) {
        String str1 = "this is a test string";
        String pattern1 = "tist";
        System.out.println(findMinWindow(str1, pattern1)); // "t stri"

        String str2 = "geeksforgeeks";
        String pattern2 = "ork";
        System.out.println(findMinWindow(str2, pattern2)); // "ksfor"
    }
}
