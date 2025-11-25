package code.dp;
/**
 Given a string S, find the longest palindromic substring in S. Substring of string S: S[ i . . . . j ] where 0 ≤ i ≤ j < len(S).
 Palindrome string: A string which reads the same backwards. More formally, S is palindrome if reverse(S) = S.
 In case of conflict, return the substring which occurs first ( with the least starting index).

 Input:
 S = "aaaabbaa"
 Output: aabbaa
 Explanation: The longest Palindromic
 substring is "aabbaa".

 * */
public class LongestPalindromeSubString {
    public static void main(String[] args) {
        System.out.println(longestPalSubString("forgeeksskeegfor"));
    }

    public static int  longestPalSubString(String str){
        int n = str.length();
        boolean dp[][] =  new boolean[n][n];
        int maxLength =1, start = 0;
        // for length = 1
        for (int i = 0; i < n; i++) {
            dp[i][i] = true;
        }
        // for length = 2
        for (int i = 0; i < n-1; i++) {
            if(str.charAt(i) == str.charAt(i+1)){
                dp[i][i+1] = true;
                maxLength = 2;
            }
        }
        // for length >=3
        for (int k = 3; k <= n; k++) {
            for (int i = 0; i < n-k+1; i++) {
                int j = i+k-1;
                if (str.charAt(i) == str.charAt(j) && dp[i+1][j-1]){
                    dp[i][j] = true;
                    if (k > maxLength){
                        maxLength = k;
                        start = i;
                    }
                }
            }
        }
        /*
        maxLen = 1
              \0 F O R G E E K S S K E E G F O R
           \0  1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
            F  0 1 0
            O  0
            R  0
            G  0
            E  0
            E  0
            K  0
            S  0
            S  0
            K  0
            E  0
            E  0
            G  0
            F  0
            O  0
            R  0
         */

        System.out.println(str.substring(start, start+maxLength));
        return maxLength;
    }
}

/**
 Given string str, the task is to find the minimum number of characters to be 'inserted/deleted' to convert it to a palindrome.

 Before we go further, let us understand with a few examples:

 ab: Number of insertions required is 1 i.e. bab
 aa: Number of insertions required is 0 i.e. aa
 abcd: Number of insertions required is 3 i.e. dcbabcd
 abcda: Number of insertions required is 2 i.e. adcbcda which is the same as the number of insertions in the substring bcd(Why?).
 aab: Number of insertions required is 1 i.e. baab
 abcde: Number of insertions required is 4 i.e. edcbabcde
 * */
class MinimumNumberOfInsertionForPalindrome {
    public static void main(String[] args) {
        String str = "geeks";
        System.out.println(minInsertion(str));
        System.out.println(minInsertionDp(str));
    }

    public static int minInsertion(String str){
        return minInsertionUtil(str, 0, str.length()-1);
    }

    private static int minInsertionUtil(String str, int l, int h){
        // base cases
        if(h< l) return Integer.MAX_VALUE;
        if (l==h) return 0; // for string with len = 1
        if (l==h-1) return str.charAt(h) == str.charAt(l) ? 0 : 1; // for string with len = 2

        // check if first and last character are same, call recur accordingly
        return str.charAt(l) == str.charAt(h) ? minInsertionUtil(
                str, l+1, h-1) : // if first and last character are same, call recur for remaining string
                1+ // else, call recur for remaining string and add 1 because we need to insert/delete 1 character
                        Math.min(
                                minInsertionUtil(str, l+1, h), // remove first character
                                minInsertionUtil(str, l, h-1)); // remove last character
    }

    public static int minInsertionDp(String str){
        int n= str.length();
        int dp[][] = new int[n][n];
        // for length=1
        for(int i=0;i<n;i++){
            dp[i][i] = 0;
        }

        // for length=2
        for(int i=0;i<n-1;i++){
            dp[i][i+1]= str.charAt(i) == str.charAt(i+1) ? 0: 1;
        }

        // for length >=3
        for (int k = 3; k <=n; k++) {
            for (int i = 0; i < n-k+1; i++) {
                int j = i+k-1;
//                System.out.println("i="+i+" j="+j);
                dp[i][j] =  str.charAt(i) == str.charAt(j) ? dp[i+1][j-1] : 1+Math.min(dp[i+1][j], dp[i][j-1]);
            }
        }
        return dp[0][n-1];
    }
}

/**
 check if by removing an index makes the string palindrome
 if string is already palindrome return -1 else return index
 Input  : str = “abcba”
 Output : Yes
 we can remove character ‘c’ to make string palindrome

 Input  : str = “abcbea”
 Output : Yes
 we can remove character ‘e’ to make string palindrome

 Input : str = “abecbea”
 It is not possible to make this string palindrome
 just by removing one character
 * */
class PalindromeIndex {
    public static void main(String[] args) {
        System.out.println(palindromeIndex("aaab"));
    }
    public static int palindromeIndex(String str){ // O(n^2)
        if(checkPalindrome(str)) // if string is already palindrome
            return -1;
        for (int i = 0; i < str.length(); i++) { // check for each index by removing it
            if(checkPalindromeWithRemovalIndex(str, i))
                return i;
        }
        return -1;
    }

    public static boolean checkPalindromeWithRemovalIndex(String str, int index){
        if(index == -1 || index == str.length())
            return checkPalindrome(str);
        int i = 0, j=str.length()-1;
        while(i<j){

            if (i == index)
            {i++;continue;}
            else if(str.charAt(i) != str.charAt(j))
                return false;
        }
        return true;
    }

    private static boolean checkPalindrome(String str){
        int i=0, j=str.length()-1;
        while(i<j){
            if (str.charAt(i) != str.charAt(j))
                return false;
        }
        return true;
    }

    public static int palindromeIndexOptimised(String str){ // O(n)
        int i=0, j=str.length()-1;
        while (str.charAt(i) == str.charAt(j))
        {
            i++;j--;
        }
        if(i>j) // string is already palindrome
            return -1;
        return checkPalindrome(str.substring(i+1,j)) ? i  // by removing index i
                : checkPalindrome(str.substring(i,j-1)) ? j // by removing index j
                :1;
    }
}

/**
 You are given an array A of size N. The array contains integers and is of even length.
 The elements of the array represent N coin of values V1, V2, ....Vn.
 You play against an opponent in an alternating way.
 In each turn, a player selects either the first or last coin from the row, removes it from the row permanently, and receives the value of the coin.
 You need to determine the maximum possible amount of money you can win if you go first.
 Note: Both the players are playing optimally.
 Input:
 N = 4
 A[] = {5,3,7,10}
 Output: 15
 Explanation: The user collects maximum
 value as 15(10 + 5)

 Example 2:
 Input:
 N = 4
 A[] = {8,15,3,7}
 Output: 22
 Explanation: The user collects maximum
 value as 22(7 + 15)
 * */
class OptimalStrategyForAGame{
    public static void main(String[] args) {
        System.out.println(collectMax(new int[]{5,3,7,10}));
        System.out.println(collectMax(new int[]{8,15,3,7}));
    }
    static int collectMax(int[] arr){
        int n = arr.length;
        int[][] t =  new int[n][n];

        // for len=1
        for (int i = 0; i < n; i++) {
            t[i][i] = arr[i];
        }

        // for len=2
        for (int i = 0; i < n-1; i++) {
            t[i][i+1] = Math.max(arr[i], arr[i+1]);
        }


        // for len >=3
        for (int len = 3; len <=n; len++) {
            for (int i = 0; i <=n-len; i++) {
                int j = i+len-1;
                t[i][j] = Math.max(
                        arr[i] + // if you choose ith index
                                Math.min( // since both play optimally, opponent will left you with min value
                                        t[i+2][j],  // if opponent choose i+1 index
                                        t[i+1][j-1] // if opponent choose j index
                                ),
                        arr[j]+ // if you choose ith index
                                Math.min( // since both play optimally, opponent will left you with min value
                                        t[i][j-2], // if opponent choose j-1 index
                                        t[i+1][j-1]) // if choose i index
                );
            }
        }
        return t[0][n-1];
    }
}

/**
 You are given an array A of size N. The array contains integers and is of even length.
 The elements of the array represent N coin of values V1, V2, ....Vn.
 You play against an opponent in an alternating way.
 In each turn, a player selects either the first or last coin from the row, removes it from the row permanently, and receives the value of the coin.
 You need to determine the maximum possible amount of money you can win if you go first.
 Note: Both the players are playing optimally.

 N = 4
 A[] = {5,3,7,10}
 i      j
 Output: 15
 Explanation: The user collects maximum
 value as 15(10 + 5)
 N = 4
 A[] = {8,15,3,7}
 i      j
 Output: 22
 Explanation: The user collects maximum
 value as 22(7 + 15)
 * */

class MaxSumCollectByPickingCornerCoinOptimalStrategy{
    public static void main(String[] args) {
        System.out.println(maxSumCollectByPickingCornerCoin(new int[]{8,15,3,7}));
        System.out.println(maxSumCollectByPickingCornerCoin(new int[]{5,3,7,10}));

        System.out.println(maxCollectCornerCoins(new int[]{8,15,3,7}));
        System.out.println(maxCollectCornerCoins(new int[]{5,3,7,10}));
    }

    public static int maxSumCollectByPickingCornerCoin(int[] arr){
        int n = arr.length;
        int[][]  table =  new int[n][n];
        int gap, i, j, x, y, z;

        for (int k = 0; k < n; k++) { // let's say number of coin available [coin to consider]
            for (i = 0, j= k; j < n; i++, j++) {
                // if you pick ith index
                // opp can pick only i+1, j index
                x = (i+2) <= j ? table[i + 2][j]: 0; // if opp pick i+1 index
                y = (i+1) <= j-1 ? table[i + 1][j - 1]: 0; // if opp pick jth index

                // if you pick jth index
                // opp can pick only i, j-1 index
                y = (i+1) <= j-1 ? table[i + 1][j - 1]: 0; // if opp pick ith index
                z = i <= (j-2) ? table[i][j - 2]: 0; // if opp pick j-1 index

                // if both the player play optimally, you'll leave the opponent with minimum value
                table[i][j] = Math.max(
                        arr[i] + Math.min(x, y), // you pick ith index
                        arr[j] + Math.min(y, z) // you pick jth index
                );
            }
        }
        return table[0][n-1];
    }

    static int maxCollectCornerCoins(int[] arr){
        return maxCollectCornerCoinsUtil(arr, 0, arr.length-1);
    }

    static int maxCollectCornerCoinsUtil(int[] arr, int i, int j){ // O(n^2)
        if(i>j)  // no coin left
            return 0;
        if(i==j) // only one coin left
            return arr[i];
        if(j==i+1) // only two coin left
            return Math.max(arr[i], arr[j]);
        return Math.max(
                arr[i] // p1 chosen i
                        +Math.min(
                        maxCollectCornerCoinsUtil(arr, i+2, j), // if opponent choose i+1
                        maxCollectCornerCoinsUtil(arr, i+1, j-1) // if opponent choosen j  // repeating
                ),
                arr[j] // p1 chosen j
                        +Math.min(
                        maxCollectCornerCoinsUtil(arr, i+1, j-1) // if opp choosen i   // repeating
                        ,maxCollectCornerCoinsUtil(arr, i, j-2) // if opp chosen j-1
                )
        );
    }

    static int[][] dp;
    static int maxCollectCornerCoinsDp(int[] arr){
        int n = arr.length;
        dp = new int[n+1][n+1];
        return maxCollectCornerCoinsDpUtil(arr, 0, n-1);
    }

    static int maxCollectCornerCoinsDpUtil(int[] arr, int i, int j){ // O(n^2)
        if(i>j) // no coin left
            return 0;
        if(i==j) //
            return arr[i];
        if(dp[i][j]  != -1) // already computed
            return dp[i][j];
        dp[i][j] =  Math.max(
                arr[i] // p1 choosen i
                        +Math.min(
                        maxCollectCornerCoinsUtil(arr, i+2, j), // if opponent choose i+1
                        maxCollectCornerCoinsUtil(arr, i+1, j-1) // if opponent choosen j  // repeating
                ),
                arr[j]
                        +Math.min(
                        maxCollectCornerCoinsUtil(arr, i+1, j-1) // if opp choosen i   // repeating
                        ,maxCollectCornerCoinsUtil(arr, i, j-2) // if opp chosen j-1
                )
        );
        return dp[i][j];
    }
}

