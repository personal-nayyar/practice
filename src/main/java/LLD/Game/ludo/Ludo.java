package LLD.Game.ludo;


import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

interface IDice{
    int roll();
}

class StandardDice implements IDice{
    @Override
    public int roll(){
        return (int)(Math.random() * 6) + 1;
    }
}

enum TokenColor{
    RED, GREEN, BLUE, YELLOW;
}

class Token{
    String id;
    int position  =  -1;
    boolean isFinished =false;

    public Token(String id){
        this.id =  id;
    }
}

class Player{
    String id;
    TokenColor color;
    List<Token> tokens;
    TokenChoiceStrategy tokenChoiceStrategy;
    int score;
    public Player(String id, TokenColor color, int tokens){
        this.id = id;
        this.color = color;
        this.tokens = new ArrayList<>();
        for(int i = 0; i < tokens; i++){
            this.tokens.add(new Token(id + "_" + i));
        }
        this.score = 0;
        tokenChoiceStrategy =  new FirstAvailableTokenChoiceStrategy();
    }
}

@AllArgsConstructor
enum TokenState{
    BASE(-1), BOARD(0), HOME_PATH(52), HOME(57);
    int position;
}

class LudoBoard{
    public static final int BOARD_SIZE = 52;
    public static final int HOME_SIZE = 5;
}

interface TokenChoiceStrategy{
    Token chooseToken(Player player, int diceValue);
}

class RandomTokenChoiceStrategy implements TokenChoiceStrategy{
    @Override
    public Token chooseToken(Player player, int diceValue){
        // filter tokens that are not finished
        List<Token> tokens = player.tokens.stream().filter(token -> !token.isFinished).toList();

        // if dice value is not 6 filter tokens that are not in home
        if (diceValue != 6){
            tokens = tokens.stream().filter(token -> token.position != TokenState.BASE.position).toList();
        }

        // token which have valid move before home
        tokens = tokens.stream().filter(token -> token.position + diceValue <= LudoBoard.BOARD_SIZE).toList();

        // return random available
        if (tokens.isEmpty()){
            return null;
        }

        return tokens.get((int)(Math.random() * tokens.size()));
    }

}

class FirstAvailableTokenChoiceStrategy implements TokenChoiceStrategy{
    @Override
    public Token chooseToken(Player player, int diceValue){
        // filter tokens that are not finished
        List<Token> tokens = player.tokens.stream().filter(token -> !token.isFinished).toList();

        // if dice value is not 6 filter tokens that are not in home
        if (diceValue != 6){
            tokens = tokens.stream().filter(token -> token.position != TokenState.BASE.position).toList();
        }

        // token which have valid move before home
        tokens = tokens.stream().filter(token -> token.position + diceValue <= LudoBoard.BOARD_SIZE+LudoBoard.HOME_SIZE).toList();

        // return random available
        if (tokens.isEmpty()){
            return null;
        }

        return tokens.get(0);
    }
}

class Ludo{
    List<Player> players;
    LudoBoard board;
    IDice dice;
    int currentPlayerIndex = 0;
    Player winner;

    public Ludo(LudoBoard board, IDice dice){
        this.board = board;
        this.dice = dice;
        players = new ArrayList<>();
    }

    public void addPlayer(Player player){
        players.add(player);
    }

    private void nextTurn(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    public void startGame(){
        while(true){
            Player currentPlayer = players.get(currentPlayerIndex);
            printTokens(currentPlayer);
            int diceValue = dice.roll();
            System.out.println("Player " + currentPlayer.id + " rolled " + diceValue);

            // move token
            Token token = currentPlayer.tokenChoiceStrategy.chooseToken(currentPlayer, diceValue);
            if (token == null){
                System.out.println("Player " + currentPlayer.id + " has no available tokens");
                nextTurn();
                continue;
            }else
                System.out.println("Player " + currentPlayer.id + " chose token " + token.id);

            int newPosition = token.position + diceValue;
            System.out.println("Player " + currentPlayer.id + " moving token " + token.id + " to position " + newPosition);

            // validate new position
            if (newPosition > LudoBoard.BOARD_SIZE+LudoBoard.HOME_SIZE){
                System.out.println("Player " + currentPlayer.id + " cannot move token " + token.id + " as it is out of bounds");
                nextTurn();
                continue;
            }
                // move token
            token.position = newPosition;

            // check if token is finished
            if (token.position == LudoBoard.BOARD_SIZE+LudoBoard.HOME_SIZE){
                token.isFinished = true;
                currentPlayer.score++;
                System.out.println("Player " + currentPlayer.id + " finished token " + token.id);
            }

            // check for win
            if (checkWin(currentPlayer)){
                System.out.println("Player " + currentPlayer.id + " wins");
                winner =  currentPlayer;
                currentPlayer.score++;
                break;
            }

            // next turn
            nextTurn();
        }
    }

    private boolean checkWin(Player player){
        return player.tokens.stream().allMatch(token -> token.isFinished);
    }

    // print current player tokens
    private void printTokens(Player player){
        System.out.println("\n\nPlayer " + player.id + " tokens");
        player.tokens.forEach(token -> System.out.println(token.id + " : " + token.position));
    }
}

class Runner{
    public static void main(String[] args) {
        LudoBoard board = new LudoBoard();
        IDice dice = new StandardDice();
        Ludo game = new Ludo(board, dice);

        Player player1 = new Player("player1", TokenColor.RED, 2);
        Player player2 = new Player("player2", TokenColor.GREEN, 2);
        game.addPlayer(player1);
        game.addPlayer(player2);

        game.startGame();
        System.out.println("Winner is " + game.winner.id);
    }
}