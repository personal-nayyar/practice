package code.stack;

// Give the smallest possible integer after removing k digits from the number
// Note: 1.The result cannot have leading zeros, unless the result is 0.
//       2. Order of the remaining digits should be same as in the original number.
public class SmallestPossibleInteger {

    public static String removeKdigits(String num, int k) {
        // Edge case: if we remove all digits, result is "0"
        if (k >= num.length()) return "0";

        // Use StringBuilder as a stack to store digits
        StringBuilder stack = new StringBuilder();

        // Traverse each digit in the number
        for (char c : num.toCharArray()) {
            // While stack is not empty AND top of stack is greater than current digit
            // AND we still have digits to remove (k > 0),
            // pop from stack (remove larger digit to make number smaller)
            while (!stack.isEmpty() && stack.charAt(stack.length() - 1) > c && k > 0) {
                stack.deleteCharAt(stack.length() - 1);
                k--;
            }
            // Add current digit to stack
            stack.append(c);
        }

        // If we still have k digits to remove, remove from the end
        // (because the number is already increasing order in stack)
        while (k > 0 && !stack.isEmpty()) {
            stack.deleteCharAt(stack.length() - 1);
            k--;
        }

        // Remove leading zeros to avoid cases like "00123"
        int start = 0;
        while (start < stack.length() && stack.charAt(start) == '0') {
            start++;
        }

        // If all digits are removed or only zeros remain, return "0"
        return (start == stack.length()) ? "0" : stack.substring(start);
    }

    public static void main(String[] args) {
        // Example 1: remove 3 digits from "1432219"
        // Expected: "1219"
        System.out.println(removeKdigits("1432219", 3));

        // Example 2: remove 1 digit from "10200"
        // Expected: "200"
        System.out.println(removeKdigits("10200", 1));

        // Example 3: remove 2 digits from "10"
        // Expected: "0"
        System.out.println(removeKdigits("10", 2));

        // Example 4: remove 3 digits from "123456"
        // Expected: "123"
        System.out.println(removeKdigits("123456", 3));
    }
}
