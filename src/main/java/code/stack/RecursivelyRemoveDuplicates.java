package code.stack;

import java.util.Stack;

/**
 * Given a string, recursively remove adjacent duplicate characters from the string.
 * The output string should not have any adjacent duplicates. See the following examples.
 * Input: azxxzy
 * Output: ay
 *
 * First “azxxzy” is reduced to “azzy”.
 * The string “azzy” contains duplicates,
 * so it is further reduced to “ay”.
 * Input: geeksforgeeg
 * Output: gksfor
 *
 * First “geeksforgeeg” is reduced to “gksforgg”.
 * The string “gksforgg” contains duplicates,
 * so it is further reduced to “gksfor”.
 * Input: caaabbbaacdddd
 * Output: Empty String
 *
 * Input: acaaabbbacdddd
 * Output: acac
 * */
public class RecursivelyRemoveDuplicates {
    private  static  char lastRemoved;
    public static void main(String[] args) {
        System.out.println(removeDuplicatesBrute("azxxxzy"));
        System.out.println(removeDuplicatesBrute("azxxxzy"));
        System.out.println(removeDuplicatesBrute("caaabbbaac"));
        System.out.println(removeDuplicatesBrute("caaabbbaac"));

        System.out.println(recurRemoveAdjDuplicateUsingStack("azxxxzy"));
        System.out.println(recurRemoveAdjDuplicateUsingStack("azxxxzy"));
        System.out.println(recurRemoveAdjDuplicateUsingStack("caaabbbaac"));
        System.out.println(recurRemoveAdjDuplicateUsingStack("caaabbbaac"));
    }

    // Brute Force recursive approach
    public static String removeDuplicatesBrute(String s) {
        if (s.length() <= 1) return s;

        StringBuilder sb = new StringBuilder();
        int i = 0;
        boolean allRemoved = true;

        while (i < s.length()) {
            int j = i + 1;
            // Find the extent of duplicates
            while (j < s.length() && s.charAt(i) == s.charAt(j)) {
                j++;
            }
            // If only one char, keep it
            if (j == i + 1) {
                sb.append(s.charAt(i));
            } else {
                allRemoved = false; // duplicates found
            }
            i = j;
        }

        // If removal happened, recurse again
        return !allRemoved ? removeDuplicatesBrute(sb.toString()) : sb.toString();
    }

    /*
    * caaaabbbaac
    *           i
    * st->c
    *
    * azxxxzy
    *      i
    * st->azz
    * */

    public static String recurRemoveAdjDuplicateUsingStack(String s) {
        Stack<Character> stack = new Stack<>();

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (!stack.isEmpty() && stack.peek() == c) {
                // pop all duplicates of 'c'
                while (i < s.length() && s.charAt(i) == c) {
                    i++; // skip all duplicates
                }
                stack.pop(); // remove previous occurrence in case of duplication
                i--; // adjust because for-loop will increment
            } else {
                stack.push(c);
            }
        }

        // rebuild string from stack
        StringBuilder result = new StringBuilder();
        for (char ch : stack) {
            result.append(ch);
        }

        return result.toString();
    }
}