package code.string.sliding_window;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SubstringWithDistinctCharacter {

}


/**
 Given a string, find the length of the longest substring which has no repeating characters.

 or

 Given a string str, find the length of the longest substring without repeating characters.

 For “ABDEFGABEF”, the longest substring are “BDEFGA” and “DEFGAB”, with length 6.

 For “BBBB” the longest substring is “B”, with length 1.

 For “GEEKSFORGEEKS”, there are two longest substrings shown in the below diagrams, with length 7
 * */
class LongestDistinctCharacterSubstring {} // sliding window }

class LongestSubstringWithDistinctCharacter{
    static final int NO_OF_CHARACTER =  26;

    public static void main(String[] args) {
        System.out.println(longestSubstringWithDistinctCharacter("GEEKSFORGEEKS"));
    }

    static int longestSubstringWithDistinctCharacter(String str){
          int start=0, maxLen=1;
          int[] lastIndexOf =  new int[NO_OF_CHARACTER];

//        str = str.toLowerCase(Locale.ROOT);
//        int n = str.length();
//        char[] charArray = str.toCharArray();
//        boolean visited[] = new boolean[NO_OF_CHARACTER];
//        Arrays.fill(lastIndexOf, -1);
//        for (int i = 0; i < n; i++) {
//            if (visited[charArray[i]-'a']){
//                if(start <= lastIndexOf[charArray[i]-'a']){
//                    maxLen =  Math.max(maxLen, i-start);
//                    start = i;
//                }
//            }else {
//                visited[charArray[i]- 'a'] = true;
//            }
//            lastIndexOf[charArray[i]- 'a'] = i;
//        }

        // Move end of current window
        for (int i = 0; i < str.length(); i++) {

            // Find the last index of str[i]
            // Update start (starting index of current window) as maximum of current value of start and last index of i plus 1
            start = Math.max(start, lastIndexOf[str.charAt(i)] + 1);

            // Update result if we get a larger window
            maxLen = Math.max(maxLen, i - start + 1);

            // Update last index of j.
            lastIndexOf[str.charAt(i)] = i;
        }
        return maxLen;
    }
}

/**
 Given a string, find the length of the longest substring in it with no
 more than K distinct characters.
 Input: String="araaci", K=2
 Output: 4
 Explanation: The longest substring with no more than '2' di
 stinct characters is "araa".

 Input: String="araaci", K=1
 Output: 2
 Explanation: The longest substring with no more than '1' di
 stinct characters is "aa".
 * */
class LongestSubstringWithKDistinctCharacter{

    public static void main(String[] args) {
        System.out.println(LongestSubstringWithKDistinctCharacter("araaci", 2));
        System.out.println(LongestSubstringWithKDistinctCharacter("araaci", 1));
        System.out.println(LongestSubstringWithKDistinctCharacter("cbbebi", 3));
    }
    static String LongestSubstringWithKDistinctCharacter(String str, int k){
        str = str.toLowerCase(Locale.ROOT);
        Map<Character, Integer> freqMap = new HashMap<>(k);
        int wStart=0, maxLen =0, mStart=0;
        for (int wEnd = 0; wEnd < str.length(); wEnd++) {
            freqMap.put(str.charAt(wEnd), freqMap.getOrDefault(str.charAt(wEnd),0)+1);
            while (freqMap.size() > k){
                freqMap.put(str.charAt(wStart), freqMap.get(str.charAt(wStart))-1);
                if(freqMap.get(str.charAt(wStart)) == 0)
                    freqMap.remove(str.charAt(wStart));
                wStart++;
            }
            if(maxLen < wEnd-wStart+1){
                maxLen =  wEnd-wStart+1;
                mStart = wStart;
            }
        }
        return str.substring(mStart, mStart+maxLen);
    }
}

/**
 Given an array of characters where each character represents a fruit,
 you are given two baskets and your goal is to put maximum number of fruits in each basket.
 The only restriction is that each basket can have only one type of fruit.
 You can start with any tree, but once you have started you can’t skip a tree.
 You will pick one fruit from each tree until you cannot, i.e., you will stop when you have to pick from a third fruit type.
 Write a function to return the maximum number of fruits in both the baskets.

 In this problem, we need to find the length of the longest subarray with no more than two distinct characters (or fruit types!).
 This transforms the current problem into the longest Substring with K Distinct Characters where K=2.
 * */
class FruitBasketProblem{
}
