package code.stack;

import java.util.Stack;

/**
    * Given an expression string exp.
 * Examine whether the pairs and the orders of “{“,”}”,”(“,”)”,”[“,”]” are correct in exp.
 *
 */
public class ParenthesisChecker {
    public static void main(String[] args) {
        String exp = "{([])}";
        System.out.println(isBalanced(exp));
    }

    static boolean isBalanced(String str){
        Stack<Character> st =  new Stack<>();
        char[] chars =  str.toCharArray();
        for (Character c : chars) {
            if(c == '[' || c  == '{' || c == '(')
                st.add(c);
            else{
                if(st.empty() || c == ']' && st.peek() != '['
                        || c == ')' && st.peek() != '('
                        || c == '}' && st.peek() != '{')
                    return false;
                st.pop();
            }
        }
        return true;
    }
}
