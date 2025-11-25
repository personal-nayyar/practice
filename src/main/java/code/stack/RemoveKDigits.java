package code.stack;

public class RemoveKDigits {

    public static String removeKDigits(String num, int k) {
        int n = num.length();
        StringBuilder stack = new StringBuilder();

        for (char digit : num.toCharArray()) {
            // Remove digits from stack if they are larger than current digit
            while (stack.length() > 0 && k > 0 && stack.charAt(stack.length() - 1) > digit) {
                stack.deleteCharAt(stack.length() - 1);
                k--;
            }
            stack.append(digit);
        }

        // If k is still > 0, remove from the end
        while (k > 0 && stack.length() > 0) {
            stack.deleteCharAt(stack.length() - 1);
            k--;
        }

        // Remove leading zeros
        while (stack.length() > 0 && stack.charAt(0) == '0') {
            stack.deleteCharAt(0);
        }

        // If result is empty, return "0"
        if (stack.length() == 0) return "0";

        return stack.toString();
    }

    public static void main(String[] args) {
        String num1 = "1432219";
        int k1 = 3;
        System.out.println(removeKDigits(num1, k1)); // Output: 1219

        String num2 = "10200";
        int k2 = 1;
        System.out.println(removeKDigits(num2, k2)); // Output: 200

        String num3 = "10";
        int k3 = 2;
        System.out.println(removeKDigits(num3, k3)); // Output: 0

        // Another example
        String num4 = "765028321";
        int k4 = 5;
        System.out.println(removeKDigits(num4, k4)); // Output: 221
    }
}