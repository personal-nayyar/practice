package leetcode.array;

public class MinimumWindowSubstring {
    public static void main(String[] args) {
        System.out.println(minWindow("ADOBECODEBANC", "ABC"));
        System.out.println(minWindow("a", "a"));
        System.out.println(minWindow("a", "aa"));
    }

    public static String minWindow(String s, String t) {
        // Edge case: If t is longer than s, no window is possible
        if (s.length() < t.length()) return "";

        // Frequency array for characters in t
        // need[c] > 0 means we still require 'c'
        int[] need = new int[128];

        // Fill the need array with frequency of characters required from t
        for (char c : t.toCharArray()) {
            need[c]++;
        }

        // Total number of characters we still need to satisfy t
        int required = t.length();

        // Sliding window pointers
        int left = 0;
        int start = 0;  // start index of minimum window
        int minLen = Integer.MAX_VALUE;

        // Expand the window with right pointer
        for (int right = 0; right < s.length(); right++) {
            char cRight = s.charAt(right);

            // If this character was needed (need > 0),
            // then including it in window reduces the "required" count
            if (need[cRight] > 0) {
                required--;  // we satisfied one needed character
            }

            // Decrease frequency (we used this character in the window)
            need[cRight]--;

            // When required == 0 → window has all characters of t
            while (required == 0) {

                // Check if this window is the smallest so far
                if (right - left + 1 < minLen) {
                    minLen = right - left + 1;
                    start = left;
                }

                // Try shrinking from the left side
                char cLeft = s.charAt(left);

                // Since we are removing this char from the window,
                // we add it back to the need array
                need[cLeft]++;

                // If after incrementing, need[cLeft] > 0,
                // it means removing this character makes the window invalid
                // (we now need this character again)
                if (need[cLeft] > 0) {
                    required++; // window is no longer valid
                }

                // Shrink the window
                left++;
            }
        }

        // If no window was found, return empty string
        if (minLen == Integer.MAX_VALUE) return "";

        // Return the smallest valid window
        return s.substring(start, start + minLen);
    }
}
