package code.array.mix;

import java.util.HashMap;
import java.util.Map;

public class RomanToInteger {
    static Map<String, Integer> romanToInt =  new HashMap<>();
    static Map<Integer, String> intToRoman =  new HashMap<>();
    static {
        romanToInt.put("I", 1);
        romanToInt.put("V", 5);
        romanToInt.put("X", 10);
        romanToInt.put("L", 50);
        romanToInt.put("C", 100);
        romanToInt.put("D", 500);
        romanToInt.put("M", 1000);
        romanToInt.put("IV", 4); // special attention
        romanToInt.put("IX", 9); // special attention

        romanToInt.put("XL", 40); // special attention
        romanToInt.put("XC", 90); // special attention

        romanToInt.put("CD", 400); // special attention
        romanToInt.put("CM", 900); // special attention
    }

    public static void main(String[] args) {
        System.out.println(romanToInt("III"));
        System.out.println(romanToInt("LVIII"));
        System.out.println(romanToInt("MCMXCIV"));

        System.out.println(intToRoman(3));
        System.out.println(intToRoman(58));
        System.out.println(intToRoman(1994));

        System.out.println(intToRomanOptimised(3));
        System.out.println(intToRomanOptimised(58));
        System.out.println(intToRomanOptimised(1994));


    }

    static long romanToInt(String roman){
        char[] chars =  roman.toCharArray();
        String key = null;
        long res = 0;
        for (int i = 0; i < chars.length; i++) {
            char current = chars[i];
            key = String.valueOf(current);
            
            // Check for subtractive notation (IV, IX, XL, XC, CD, CM)
            if (i < chars.length - 1) {
                char next = chars[i + 1];
                if ((current == 'I' && (next == 'V' || next == 'X')) ||  // IV, IX
                    (current == 'X' && (next == 'L' || next == 'C')) ||  // XL, XC
                    (current == 'C' && (next == 'D' || next == 'M'))) {  // CD, CM
                    key = current + String.valueOf(next);
                    i++;  // Skip next character as it's part of this numeral
                }
            }
            res += romanToInt.get(key);
        }
        return res;
    }

    static String intToRomanOptimised(int n){
        String[] thousands = {"", "M", "MM", "MMM"};
        String[] hundreds = {"", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"};
        String[] tens = {"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"};
        String[] units = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};
        return thousands[n/1000]+hundreds[(n%1000)/100]+tens[(n%100)/10]+units[(n%10)/1];
    }

    static String intToRoman(int n) {
        // Define the values and their corresponding Roman numerals in descending order
        int[] values = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] numerals = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
        
        StringBuilder result = new StringBuilder();
        
        // Iterate through each value and append the corresponding numeral
        for (int i = 0; i < values.length; i++) {
            while (n >= values[i]) {
                result.append(numerals[i]);
                n -= values[i];
            }
        }
        
        return result.toString();
    }
}
