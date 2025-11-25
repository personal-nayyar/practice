package A_interview_experiences.epam;

import java.util.Arrays;

public class LongestCommonPrefix {
    public static void main(String[] args) {
        String[] strs = {"flower", "flow", "flight"};
        System.out.println(longestCommonPrefix(strs));
        System.out.println(longestCommonPrefixSorting(strs));
    }

    public static String longestCommonPrefix(String[] strs) {
        if (strs.length == 0) return "";
        String prefix = strs[0];
        for (int i = 1; i < strs.length; i++) {
            // find the prefix of the current string
            while (strs[i].indexOf(prefix) != 0) {
                // reduce the prefix length by 1
                prefix = prefix.substring(0, prefix.length() - 1);
            }
        }
        return prefix;
    }
//    •	O(N × M), where
//    N = number of strings,
//    M = length of shortest string.

    public static String longestCommonPrefixSorting(String[] strs) {
        if (strs == null || strs.length == 0) return "";

        Arrays.sort(strs);
        System.out.println(Arrays.toString(strs));
        String first = strs[0];
        String last = strs[strs.length - 1];

        int i = 0;
        while (i < first.length() && i < last.length() && first.charAt(i) == last.charAt(i)) {
            i++;
        }
        return first.substring(0, i);
    }
    // ✅ Time Complexity:
    //	•	O(N log N + M), because of sorting.

}
