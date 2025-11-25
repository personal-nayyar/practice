package LLD.Game.SnakeNLadder;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SnakeNLadder {
}

@AllArgsConstructor
@Data
class Player{
    String name;
    int position;
}

interface IDice{
    int roll();
}

class SimpleDice implements IDice{
    @Override
    public int roll(){
        return (int)(Math.random() * 6) + 1;
    }
}

class TwoDice implements IDice{
    @Override
    public int roll(){
        return (int)(Math.random() * 6) + 1 + (int)(Math.random() * 6) + 1;
    }
}

class DiceFactory{
    public static IDice createDice(String diceType){
        switch(diceType){
            case "SimpleDice":
                return new SimpleDice();
            case "TwoDice":
                return new TwoDice();
            default:
                throw new IllegalArgumentException("Invalid dice type");
        }
    }
}

interface IBoard{
    int getBoardSize();
    int getNextPosition(int currentPosition, int diceValue);
}

class StandardBoard implements IBoard{
    int boardSize;
    Map<Integer, Integer> snake;
    Map<Integer, Integer> ladder;
    MovementStrategy movementStrategy;
    public StandardBoard(int boardSize, Map<Integer, Integer> snake, Map<Integer, Integer> ladder, MovementStrategy movementStrategy){
        this.boardSize = boardSize;
        this.snake = snake;
        this.ladder = ladder;
        this.movementStrategy = movementStrategy;
    }

    @Override
    public int getBoardSize(){
        return this.boardSize;
    }

    @Override
    public int getNextPosition(int currentPosition, int diceValue){
        if(currentPosition + diceValue > boardSize){
            System.out.println("Next position is greater than board size, remaining at current position");
            return currentPosition;
        }
        Map<Integer, Integer> obstacles = new HashMap<>(){{
            putAll(snake);
            putAll(ladder);
        }};
        return movementStrategy.calculateNextPosition(currentPosition, diceValue, obstacles);
    }
}

class BoardFactory{
    public static IBoard createStandardBoard(){
        Map<Integer, Integer> ladders =  new HashMap<>(){{
            put(4, 14);   // Example ladders
            put(9, 31);
            put(21, 42);
            put(28, 84);
            put(51, 67);
        }};

        Map<Integer, Integer> snakes = new HashMap<>();
        snakes.put(99, 8);    // Example snakes
        snakes.put(95, 75);
        snakes.put(93, 73);
        snakes.put(62, 19);
        snakes.put(54, 34);

        MovementStrategy movementStrategy =  new StandardMovementStrategy();
        return new StandardBoard(100, snakes, ladders, movementStrategy);
    }

    // Custom board
//    public static IBoard createCustomBoard(int boardSize, Map<Integer, Integer> snake, Map<Integer, Integer> ladder, MovementStrategy movementStrategy){
//        return new StandardBoard(boardSize, snake, ladder, movementStrategy);
//    }
}

interface MovementStrategy{
    int calculateNextPosition(int currentPosition, int diceValue, Map<Integer, Integer> obstacle);
}

class StandardMovementStrategy implements MovementStrategy{
    @Override
    public int calculateNextPosition(int currentPosition, int diceValue, Map<Integer, Integer> obstacle){
        int nextPosition = currentPosition + diceValue;
        if(obstacle.containsKey(nextPosition)){
            System.out.println("Obstacle found at position " + nextPosition);
            return calculateNextPosition(obstacle.get(nextPosition), 0, obstacle);
        }
        return nextPosition;
    }
}


enum GameStatus{
    NOT_STARTED,
    IN_PROGRESS,
    GAME_OVER;
}

class GameEngine{
    IDice dice;
    IBoard board;
    List<Player> players;
    int currentPlayerIndex;
    MovementStrategy movementStrategy;
    GameStatus gameStatus;

//     Singleton
    static GameEngine instance;
    public static GameEngine getInstance(){
        if(instance == null){
            instance = new GameEngine();
        }
        return instance;
    }

    private GameEngine() {}

    void initStandardGame(){
        dice = new SimpleDice();
        board = BoardFactory.createStandardBoard();
        movementStrategy = new StandardMovementStrategy();
        players = new ArrayList<>(){{
            add(new Player("Player1", 0));
            add(new Player("Player2", 0));
        }};
        currentPlayerIndex = 0;
    }

    void addPlayer(String name){
        Player player = new Player(name, 0);
        players.add(player);
        currentPlayerIndex = 0;
    }

    int rollDice(){
        int diceValue = dice.roll();
        int totalValue = diceValue;
        while (diceValue == 6){
            System.out.println("Dice value is 6, rolling again");
            diceValue = dice.roll();
            totalValue += diceValue;
        }
        return totalValue;
    }

    void startGame(){
        Player currentPlayer;
        while(gameStatus != GameStatus.GAME_OVER){
            currentPlayer = players.get(currentPlayerIndex);
            System.out.println("Current player " + currentPlayer.name + " at position :" + currentPlayer.getPosition());
            int diceValue = rollDice();
            System.out.println("Dice total Value: "+diceValue);
            int nextPosition = board.getNextPosition(currentPlayer.getPosition(), diceValue);
            System.out.println("Next Position: "+nextPosition);

            // check for win
            if(nextPosition == board.getBoardSize()){
                gameStatus = GameStatus.GAME_OVER;
                System.out.println("Player " + currentPlayer.name + " wins");
            }
            currentPlayer.setPosition(nextPosition);

            // next player
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            System.out.println("===============================");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

class Runner{
    public static void main(String[] args) {
        GameEngine gameEngine = GameEngine.getInstance();
        gameEngine.initStandardGame();
        gameEngine.startGame();
    }
}
