package code.dp;
/**
 Imagine a frog at the bottom of a staircase with n steps. Each jump can be 1 step or 2 steps (sometimes even more, depending on the variant). The question:

 In how many distinct ways can the frog reach the top?

 It’s one of those deceptively simple problems that secretly whispers, “I’m Fibonacci.”
 * */
public class mimJumpSteps {
    public static void main(String[] args) {

    }

    public static int minJumpSteps(int n){
        if (n <= 1)
            return 1;
        return minJumpSteps(n-1) + minJumpSteps(n-2);
    }

    public static int minJumpStepsMemoization(int n){
        int[] memo = new int[n+1];
        return minJumpStepsMemoizationHelper(n, memo);
    }

    public static int minJumpStepsMemoizationHelper(int n, int[] memo){
        if (n <= 1)
            return 1;
        if (memo[n] > 0)
            return memo[n];
        memo[n] = minJumpStepsMemoizationHelper(n-1, memo) + minJumpStepsMemoizationHelper(n-2, memo);
        return memo[n];
    }



}
