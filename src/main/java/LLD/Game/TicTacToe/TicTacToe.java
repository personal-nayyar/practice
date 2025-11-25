package LLD.Game.TicTacToe;

import lombok.AllArgsConstructor;
import lombok.Data;
import utils.DSAUtils;

import java.util.Random;
import java.util.Scanner;

public class TicTacToe {
}

enum GameStatus{
    NOT_STARTED,
    IN_PROGRESS,
    DRAW,
    O_WON,
    X_WON;
}

@AllArgsConstructor
@Data
class Position{
    int row;
    int col;
}

interface IPlayer{
    String getPlayerName();
    String getPlayerMark();
    Position getNextMove(IBoard board);
}

interface IBoard{
    void printBoard();
    boolean isMoveValid(Position position);
    void makeMove(Position position, String playerMark);
    String getCellMark(Position position);
    int getBoardSize();
}

interface IWinCheckerStrategy{
    String checkWin(IBoard board, Position lastMove);
    boolean checkFull(IBoard board);
    boolean checkDraw(IBoard board);
}

class HumanPlayer implements IPlayer{
    private String playerName;
    private String playerMark;
    Scanner scanner;
    public HumanPlayer(String playerName, String playerMark){
        this.playerName = playerName;
        this.playerMark = playerMark;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public String getPlayerName(){
        return this.playerName;
    }

    @Override
    public String getPlayerMark(){
        return this.playerMark;
    }

    @Override
    public Position getNextMove(IBoard board){
        System.out.println("Enter row:");
        int row = scanner.nextInt();

        System.out.println("Enter col:");
        int col = scanner.nextInt();
        return new Position(row, col);
    }
}

class ComputerPlayer implements IPlayer{
    private String playerName;
    private String playerMark;
    private Random rnd;
    public ComputerPlayer(String playerName, String playerMark){
        this.playerName = playerName;
        this.playerMark = playerMark;
        this.rnd = new Random();
    }

    @Override
    public String getPlayerName(){
        return this.playerName;
    }

    @Override
    public String getPlayerMark(){
        return this.playerMark;
    }

    @Override
    public Position getNextMove(IBoard board){
        int row, col;
        boolean isPositionValid;
        do{
            row = rnd.nextInt(board.getBoardSize());
            col = rnd.nextInt(board.getBoardSize());
            isPositionValid = board.getCellMark(new Position(row, col)).equalsIgnoreCase(" ");
        }while(!isPositionValid);
        return new Position(row, col);
    }
}

class Board implements IBoard{
    private String[][] board;
    private int size;
    public Board(int size){
        this.size = size;
        this.board = new String[size][size];
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                this.board[i][j] = " ";
            }
        }
    }

    @Override
    public void printBoard(){
        DSAUtils.print2DArray(this.board);
        System.out.println("==============================");
    }

    @Override
    public boolean isMoveValid(Position position){
        return position.row >= 0 && position.row < this.size
                && position.col >= 0 && position.col < this.size &&
                this.board[position.row][position.col].equalsIgnoreCase( " ");
    }

    @Override
    public void makeMove(Position position, String playerMark){
        this.board[position.row][position.col] = playerMark;
    }

    @Override
    public String getCellMark(Position position){
        return this.board[position.row][position.col];
    }

    @Override
    public int getBoardSize(){
        return this.size;
    }
}

class WinCheckerStrategy implements IWinCheckerStrategy{
    @Override
    public String checkWin(IBoard board, Position lastMove){
        String playerMark = board.getCellMark(lastMove);
        // check for row
        for (int col = 0; col < board.getBoardSize(); col++) {
            if (!board.getCellMark(new Position(lastMove.row, col)).equalsIgnoreCase(playerMark)) {
                break;
            }
            if (col == board.getBoardSize() - 1) {
                return playerMark;
            }
        }

        // check for column
        for (int row = 0; row < board.getBoardSize(); row++) {
            if (!board.getCellMark(new Position(row, lastMove.col)).equalsIgnoreCase(playerMark)) {
                break;
            }
            if (row == board.getBoardSize() - 1) {
                return playerMark;
            }
        }


        // check for diagonal bottom-left to top-right
        for (int i = 0; i < board.getBoardSize(); i++) {
            if (!board.getCellMark(new Position(i, i)).equalsIgnoreCase(playerMark)) {
                break;
            }
            if (i == board.getBoardSize() - 1) {
                return playerMark;
            }
        }

        // check for diagonal top-left to bottom-right
        for (int i = 0; i < board.getBoardSize(); i++) {
            if (!board.getCellMark(new Position(i, board.getBoardSize() - i - 1)).equalsIgnoreCase(playerMark)) {
                break;
            }
            if (i == board.getBoardSize() - 1) {
                return playerMark;
            }
        }
        return null;
    }

    @Override
    public boolean checkFull(IBoard board) {
        // check if board is full
        for (int row = 0; row < board.getBoardSize(); row++) {
            for (int col = 0; col < board.getBoardSize(); col++) {
                if (board.getCellMark(new Position(row, col)).equalsIgnoreCase(" ")) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean checkDraw(IBoard board){
        return checkFull(board);
    }
}

class GameEngine{
    private IBoard board;
    private IPlayer player1;
    private IPlayer player2;
    private IWinCheckerStrategy winCheckerStrategy;
    private GameStatus gameStatus;
    private IPlayer currentPlayer;
    public GameEngine(){
        this.board = new Board(3);
        this.player1 = new HumanPlayer("Player1", "X");
        this.player2 = new ComputerPlayer("Player2", "O");
        this.winCheckerStrategy = new WinCheckerStrategy();
        this.gameStatus = GameStatus.NOT_STARTED;
        this.currentPlayer = this.player1;
    }

    public void startGame(){
        gameStatus = GameStatus.IN_PROGRESS;
        while(this.gameStatus == GameStatus.IN_PROGRESS){
            // get the move
            Position move = this.currentPlayer.getNextMove(this.board);
            // validate the move
            if(!this.board.isMoveValid(move)){
                System.out.println("Invalid move, try gain");
                continue;
            }

            // make the move
            this.board.makeMove(move, this.currentPlayer.getPlayerMark());

            // check Win
            String winner = this.winCheckerStrategy.checkWin(this.board, move);
            if(winner != null){
                this.gameStatus = currentPlayer.getPlayerMark() == "X" ? GameStatus.X_WON : GameStatus.O_WON;
                System.out.println("Player " + winner + " wins");
            }

            // check draw
            if(this.winCheckerStrategy.checkDraw(this.board)){
                this.gameStatus = GameStatus.DRAW;
            }
            // print board
            this.board.printBoard();
            // next player
            this.currentPlayer = this.currentPlayer == this.player1 ? this.player2 : this.player1;
        }
    }
}

class Runner{
    public static void main(String[] args){
        GameEngine gameEngine = new GameEngine();
        gameEngine.startGame();
    }
}