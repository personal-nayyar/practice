package code.dp;


/**
 Given two strings, the task is to find the longest common substring present in the given strings in the same order.
 Input s1: “dadef”
 s2: “adwce”
 Output: 2
 Explanation: Substring “ad” of length 2 is the longest.

 Input s1: “abcdxyz”
 s2: “xyzabcd”
 Output: 4
 Explanation: Substring “abcd” of length 4 is the longest.
 * */
class LongestCommonSubstring {

    public static void main(String[] args)
    {
        String  X = "abcdxyz";
        String  Y = "xyzabcd";

        System.out.println(lengthOfLongestCommonSubstring(X, Y));
    }
    static int lengthOfLongestCommonSubstring(String str1, String str2){
        int m =  str1.length();
        int n = str2.length();
        int[][] lcs =  new int[m+1][n+1];

        for (int i = 0; i <=m; i++) { // consider str2 as empty
            lcs[i][0] = 0;
        }

        for (int j = 0;j <=n; j++) { // consider str1 as empty
            lcs[0][j] = 0;
        }

        int maxLen = Integer.MIN_VALUE;
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (str1.charAt(i-1) == str2.charAt(j-1)){
                    lcs[i][j] = lcs[i-1][j-1]+1;
                    maxLen =  Math.max(maxLen,  lcs[i][j]);
                }
                else{
                    lcs[i][j] = 0;
                }
            }
        }
        /*
                \0   A   B   C   D   X   Y   Z
           \0    0   0   0   0   0   0   0   0
           X     0   0   0   0   0   1   0   0
           Y     0   0   0   0   0   0   2   0
           Z     0   0   0   0   0   0   0   3
           A     0   1   0   0   0   0   0   0
           B     0   0   2   0   0   0   0   0
           C     0   0   0   3   0   0   0   0
           D     0   0   0   0   4   0   0   0
         */
        return maxLen;
    }
}
/**
 Given two strings text1 and text2, return the length of their longest common subsequence. If there is no common subsequence, return 0.

 A subsequence of a string is a new string generated from the original string with some characters (can be none) deleted without changing the relative order of the remaining characters.

 For example, "ace" is a subsequence of "abcde".
 A common subsequence of two strings is a subsequence that is common to both strings.

 Input: text1 = "abcde", text2 = "ace"
 Output: 3
 Explanation: The longest common subsequence is "ace" and its length is 3.
 Example 2:

 Input: text1 = "abc", text2 = "abc"
 Output: 3
 Explanation: The longest common subsequence is "abc" and its length is 3.
 Example 3:

 Input: text1 = "abc", text2 = "def"
 Output: 0

 * */
public class LongestCommonSubsequence {

    public static void main(String[] args)
    {
        System.out.println(lengthOfLongestCommonSubsequence("abcdxyz","xyzabcd"));
        System.out.println(lengthOfLongestCommonSubsequence("intention", "execution"));
    }
    public static int lengthOfLongestCommonSubsequence(String str1, String str2){
        int m =  str1.length();
        int n = str2.length();
        int lcs[][] = new int[m+1][n+1];

        for (int i = 0; i <=m; i++) {
            lcs[i][0] = 0;
        }

        for (int j = 0;j <=n; j++) {
            lcs[0][j] = 0;
        }

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (str1.charAt(i-1) == str2.charAt(j-1))
                    lcs[i][j] = lcs[i-1][j-1]+1;
                else
                    lcs[i][j] = Math.max(lcs[i][j-1], lcs[i-1][j]);
            }
        }
        return lcs[m][n];
    }
}

/**
 Given two strings str1 and str2, the task is to find the length of the shortest string that has both str1 and str2 as subsequences.
 Input:   str1 = "geek",  str2 = "eke"
 Output: 5
 Explanation:
 String "geeke" has both string "geek"
 and "eke" as subsequences.

 Input:   str1 = "AGGTAB",  str2 = "GXTXAYB"
 Output:  9
 Explanation:
 String "AGXGTXAYB" has both string
 "AGGTAB" and "GXTXAYB" as subsequences.
 * */
class ShortestCommonSupersequence{
    public static void main(String[] args) {
        System.out.println(shortestSuperSequence("geek", "eke"));
        System.out.println(shortestSuperSequence("AGGTAB", "GXTXAYB"));
    }
    static int shortestSuperSequence(String X, String Y)
    {
        int m = X.length();
        int n = Y.length();

        // find lcs
        int l = LongestCommonSubsequence.lengthOfLongestCommonSubsequence(X, Y);

        // Result is sum of input string
        // lengths - length of lcs
        return (m + n - l);
    }
}

/**
 Given two strings str1 and str2 and below operations that can be performed on str1.
 Find minimum number of edits (operations) required to convert ‘str1’ into ‘str2’.
 1. Insert
 2. Remove
 3. Replace
 All the above operations are of equal cost.

 nput:   str1 = “geek”, str2 = “gesek”
 Output:  1
 Explanation: We can convert str1 into str2 by inserting a ‘s’.

 Input:   str1 = “cat”, str2 = “cut”
 Output:  1
 Explanation: We can convert str1 into str2 by replacing ‘a’ with ‘u’.

 Input:   str1 = “sunday”, str2 = “saturday”
 Output:  3
 Explanation: Last three and first characters are same.  We basically need to convert “un” to “atur”.  This can be done using below three operations. Replace ‘n’ with ‘r’, insert t, insert a
 * */
class MinEditDistance{
    public static void main(String[] args) {
        System.out.println(minEditDistance("CART", "MARCH"));
        System.out.println(minEditDistance("geek", "gesek"));
        System.out.println(minEditDistance("cat", "cut"));
        System.out.println(minEditDistance("sunday", "saturday"));
    }
    static int minEditDistance(String str1, String str2){
        int m = str1.length();
        int n = str2.length();
        int[][] table =  new int[m+1][n+1];
        table[0][0] = 0;
        for (int i = 1; i <= m; i++) {
            table[i][0] = 1+table[i-1][0];
        }
        for (int j = 1; j <= n; j++) {
            table[0][j] = 1+table[0][j-1];
        }

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (str1.charAt(i - 1) == str2.charAt(j - 1))
                    table[i][j] = table[i - 1][j - 1];
                else
                    table[i][j] = 1 + minElem(
                                    table[i - 1][j], // insertion
                                    table[i][j - 1], // deletion
                                    table[i - 1][j - 1]); // replacement
            }
        }
        return table[m][n];
    }

    static int minElem(int a, int b, int c){
        return Math.min(a, Math.min(b, c));
    }
}
