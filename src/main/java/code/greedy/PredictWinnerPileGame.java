package code.greedy;

/**
 There are two players P1 and P2 and two piles of coins consisting of M and N coins respectively.
 At each turn, a player can choose only one of the piles out of these and discard the other one.
 This discarded pile cannot be used further in the game.
 The pile player chooses is further divided into two piles of non-zero parts.
 The player who cannot divide the pile i.e. the number of coins in the pile is < 2, loses the game.
 The task is to determine which player wins if P1 starts the game and both the players play optimally.
 * */
class PredictWinnerPileGame{
    // Driver code
    public static void main(String[] args)
    {
        int M = 1, N = 2;
        findWinner(M, N);
        System.out.println(winner(M,N));
    }
    // Function to print the winner of the game
    static void findWinner(int M, int N)
    {
        if (M % 2 == 0 || N % 2 == 0)
            System.out.println("Player 1");
        else
            System.out.println("Player 2");
    }

    static String winner(int M, int N){
        if(M%2 == 0 || N%2 == 0)
            return "P1";
        else
            return "P2";
    }
}
