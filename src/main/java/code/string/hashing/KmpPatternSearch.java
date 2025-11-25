package code.string.hashing;

/**
 https://www.geeksforgeeks.org/dsa/check-string-substring-another/
 Given two strings txt and pat, the task is to find if pat is a substring of txt. If yes, return the index of the first occurrence, else return -1.
 Input: txt = "geeksforgeeks", pat = "eks"
 Output: 2
 Explanation: String "eks" is present at index 2 and 9, so 2 is the smallest index.

 Input: txt = "geeksforgeeks", pat = "xyz"
 Output: -1.
 Explanation: There is no occurrence of "xyz" in "geeksforgeeks"

 https://leetcode.com/problems/find-the-index-of-the-first-occurrence-in-a-string/
 Given two strings needle and haystack, return the index of the first occurrence of needle in haystack, or -1 if needle is not part of haystack.
 Example 1:
 Input: haystack = "sadbutsad", needle = "sad"
 Output: 0
 Explanation: "sad" occurs at index 0 and 6.
 The first occurrence is at index 0, so we return 0.

 Example 2:
 Input: haystack = "leetcode", needle = "leeto"
 Output: -1
 Explanation: "leeto" did not occur in "leetcode", so we return -1.
 * */
public class KmpPatternSearch {
    public static void main(String[] args) {
        System.out.println("Test 1: " + kmpSerch("abcdabcdef", "abc") + " (Expected: 0)");
        System.out.println("Test 2: " + kmpSerch("abcdabcdef", "cde") + " (Expected: 5)");
        System.out.println("Test 3: " + kmpSerch("abcdabcdef", "abcd") + " (Expected: 0)");
        System.out.println("Test 4: " + kmpSerch("abcdabcdef", "f") + " (Expected: 9)");
        System.out.println("Test 5: " + kmpSerch("abcdabcdef", "g") + " (Expected: -1)");
        System.out.println("Test 6: " + kmpSerch("abababab", "abab") + " (Expected: 0)");
        System.out.println("Test 7: " + kmpSerch("abababab", "ababab") + " (Expected: 0)");
        System.out.println("Test 8: " + kmpSerch("abababab", "bab") + " (Expected: 1)");
        System.out.println("Test 9: " + kmpSerch("abababab", "aba") + " (Expected: 0)");
        System.out.println("Test 10: " + kmpSerch("abababab", "ab") + " (Expected: 0)");
    }

    static int kmpSerch(String str, String pattern){
        int[] lps = longestPrefixSuffix(pattern);
        int n = str.length();
        int m = pattern.length();
        int i=0, j = 0;
        while(i < n && j < m) {
            if (str.charAt(i) == pattern.charAt(j))
            {
                i++;j++;
            } else {
                if (j != 0)
                    j = lps[j-1];
                else
                    i++;
            }
        }
        return j == m ? i-m : -1;
    }

    public static int[] longestPrefixSuffix(String pattern){
        int n = pattern.length();
        int[] lps = new int[n];
        int j= 0;
        for (int i = 1; i <n;) {
            if (pattern.charAt(i) == pattern.charAt(j)){
                lps[i++] = ++j;
            } else {
                if (j == 0)
                    i++;
                else
                    j = lps[j-1];
            }
        }
        return lps;
    }

}

/**
 A string is called a happy prefix if is a non-empty prefix which is also a suffix (excluding itself).
 Given a string s, return the longest happy prefix of s. Return an empty string "" if no such prefix exists.
 Example 1:

 Input: s = "level"
 Output: "l"
 Explanation: s contains 4 prefix excluding itself ("l", "le", "lev", "leve"), and suffix ("l", "el", "vel", "evel"). The largest prefix which is also suffix is given by "l".
 Example 2:

 Input: s = "ababab"
 Output: "abab"
 Explanation: "abab" is the largest prefix which is also suffix. They can overlap in the original string.
 */
class LongestPrefixSuffix{
    public static void main(String[] args) {
        System.out.println("Test 1: " + longestPrefixSuffix("level") + " (Expected: l)");
        System.out.println("Test 2: " + longestPrefixSuffix("ababab") + " (Expected: abab)");
        System.out.println("Test 3: " + longestPrefixSuffix("") + " (Expected: )");
        System.out.println("Test 4: " + longestPrefixSuffix("a") + " (Expected: a)");
        System.out.println("Test 5: " + longestPrefixSuffix("aa") + " (Expected: aa)");
        System.out.println("Test 6: " + longestPrefixSuffix("abab") + " (Expected: ab)");
        System.out.println("Test 7: " + longestPrefixSuffix("abcdabc") + " (Expected: abc)");
        System.out.println("Test 8: " + longestPrefixSuffix("abcdabcd") + " (Expected: abcd)");
        System.out.println("Test 9: " + longestPrefixSuffix("abcdxabcd") + " (Expected: abcd)");
        System.out.println("Test 10: " + longestPrefixSuffix("abcdxabcdx") + " (Expected: abcdx)");
    }
    static String longestPrefixSuffix(String s){
        if(s == null)
            return "";
        int n =  s.length();
        if (n <= 1)
            return s;
        int[] lps = new int[n];
        lps[0] = 0;
        int j =0;
        int maxLen = 0;
        for (int i = 1; i < n;) {
            if (s.charAt(i) == s.charAt(j)){
                lps[i++] = ++j;
                maxLen =  Math.max(maxLen,lps[i-1]);
            }
            else{
                if (j == 0)
                    i++;
                else
                    j = lps[j-1];
            }
        }

        return s.substring(0, maxLen);
    }
}
