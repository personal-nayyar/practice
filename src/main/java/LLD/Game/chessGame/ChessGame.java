package LLD.Game.chessGame;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class ChessGame {
}

enum PieceType {
    KING,
    QUEEN,
    HORSE,
    BISHOP,
    ROOK,
    PAWN,
    KNIGHT
}

@Data
abstract class Piece {
    boolean isWhite;
    boolean isCaptured;
    boolean isFirstMove;
    MoveStrategy moveStrategy;

    Piece(boolean isWhite){
        this.isWhite = isWhite;
    }
}

class KING extends Piece {
    KING(boolean isWhite){
        super(isWhite);
        this.moveStrategy = new KINGMoveStrategy();
    }
}

class QUEEN extends Piece {
    QUEEN(boolean isWhite){
        super(isWhite);
        this.moveStrategy = new QUEENMoveStrategy();
    }

}

class ROOK extends Piece {
    ROOK(boolean isWhite){
        super(isWhite);
        this.moveStrategy = new ROOKMoveStrategy();
    }
}

class BISHOP extends Piece {
    BISHOP(boolean isWhite){
        super(isWhite);
        this.moveStrategy = new BISHOPMoveStrategy();
    }
}

class KNIGHT extends Piece {
    KNIGHT(boolean isWhite){
        super(isWhite);
        this.moveStrategy = new KNIGHTMoveStrategy();
    }
}

class PAWN extends Piece {
    PAWN(boolean isWhite){
        super(isWhite);
        this.moveStrategy = new PAWNMoveStrategy();
    }
}

// factory design to create pieces
class PieceFactory {
    static Piece createPiece(PieceType type, boolean isWhite) {
        switch (type) {
            case KING:
                return new KING(isWhite);
            case QUEEN:
                return new QUEEN(isWhite);
            case ROOK:
                return new ROOK(isWhite);
            case BISHOP:
                return new BISHOP(isWhite);
            case KNIGHT:
                return new KNIGHT(isWhite);
            case PAWN:
                return new PAWN(isWhite);
            default:
                return null;
        }
    }
}

@Setter
@Getter
@AllArgsConstructor
class Block {
    int x;
    int y;
    Piece piece;
}


// strategy design to move pieces
interface MoveStrategy {
    boolean isValidMove(Block blockStart, Block blockEnd);
}

class KINGMoveStrategy implements MoveStrategy {
    @Override
    public boolean isValidMove(Block blockStart, Block blockEnd) {
        return false;
    }
}

class QUEENMoveStrategy implements MoveStrategy {
    @Override
    public boolean isValidMove(Block blockStart, Block blockEnd){
        return false;
    }
}

class ROOKMoveStrategy implements MoveStrategy {
    @Override
    public boolean isValidMove(Block blockStart, Block blockEnd){
        return false;
    }
}

class BISHOPMoveStrategy implements MoveStrategy {
    @Override
    public boolean isValidMove(Block blockStart, Block blockEnd){
        return false;
    }
}

class KNIGHTMoveStrategy implements MoveStrategy {
    @Override
    public boolean isValidMove(Block blockStart, Block blockEnd){
        return false;
    }
}

class PAWNMoveStrategy implements MoveStrategy {
    @Override
    public boolean isValidMove(Block blockStart, Block blockEnd){
        return false;
    }
}


class Move {
    private Player player;
    private Block startBlock;
    private Block endBlock;
    public Move(Player player, Block startBlock, Block endBlock){
        this.player = player;
        this.endBlock= endBlock;
        this.startBlock = startBlock;
    }
    public boolean isValid(){
        return !(startBlock.getPiece().isWhite == endBlock.getPiece().isWhite);
    }
    public Block getStartBlock() {
        return startBlock;
    }
    public Block getEndBlock() {
        return endBlock;
    }
}

enum Status {
    ACTIVE, SAVED, BLACK_WIN, WHITE_WIN, STALEMATE;
}

class ChessBoard {
    Block[][] blocks;
    PieceFactory pieceFactory;

    public ChessBoard() {
        blocks = new Block[8][8];
        pieceFactory = new PieceFactory();
    }

    public void initBoard() {
        // Setting White Pieces
        blocks[0][0] = new Block(0,0,pieceFactory.createPiece(PieceType.ROOK, true));
        blocks[0][1] = new Block(0,1,pieceFactory.createPiece(PieceType.KNIGHT, true));
        blocks[0][2] = new Block(0,2,pieceFactory.createPiece(PieceType.BISHOP, true));
        blocks[0][3] = new Block(0,3,pieceFactory.createPiece(PieceType.QUEEN, true));
        blocks[0][4] = new Block(0,4,pieceFactory.createPiece(PieceType.KING, true));
        blocks[0][5] = new Block(0,5,pieceFactory.createPiece(PieceType.BISHOP, true));
        blocks[0][6] = new Block(0,6,pieceFactory.createPiece(PieceType.KNIGHT, true));
        blocks[0][7] = new Block(0,7,pieceFactory.createPiece(PieceType.ROOK, true));
        for(int j=0; j<8 ; j++){
            blocks[1][j] = new Block(1,j,pieceFactory.createPiece(PieceType.PAWN, true));
        }
        //Setting Black Pieces
        blocks[7][0] = new Block(7,0,pieceFactory.createPiece(PieceType.ROOK, false));
        blocks[7][1] = new Block(7,1,pieceFactory.createPiece(PieceType.KNIGHT, false));
        blocks[7][2] = new Block(7,2,pieceFactory.createPiece(PieceType.BISHOP, false));
        blocks[7][3] = new Block(7,3,pieceFactory.createPiece(PieceType.QUEEN, false));
        blocks[7][4] = new Block(7,4,pieceFactory.createPiece(PieceType.KING, false));
        blocks[7][5] = new Block(7,5,pieceFactory.createPiece(PieceType.BISHOP, false));
        blocks[7][6] = new Block(7,6,pieceFactory.createPiece(PieceType.KNIGHT, false));
        blocks[7][7] = new Block(7,7,pieceFactory.createPiece(PieceType.ROOK, false));
        for(int j=0; j<8 ; j++){
            blocks[6][j] = new Block(6,j,pieceFactory.createPiece(PieceType.PAWN, false));
        }

        // Defining rest of the blocks having no pieces
        for(int i=2;i<6;i++){
            for( int j=0; j<8; j++){
                blocks[i][j] = new Block(i,j,null);
            }
        }
    }
}

@AllArgsConstructor
class Player{
    String name;
    String color;
}

enum GameStatus {
    ACTIVE, SAVED, BLACK_WIN, WHITE_WIN, STALEMATE;
}

interface ChessService{
    void initGame();
    void startGame();
    void makeMove(Move move);
    boolean checkMate(Move move);
    List<Move> getMoves();
 }

class ChessServiceImpl implements ChessService{
    ChessBoard board;
    Player playerA; // white
    Player playerB; // black
    boolean isWhiteTurn;
    List<Move> movesLog;
    GameStatus status;

    public void initGame(){
        board = new ChessBoard();
        board.initBoard();
        playerA = new Player("PlayerA", "white");
        playerB = new Player("PlayerB", "black");
        movesLog = new ArrayList<>();
        isWhiteTurn = true;
    }

    public void startGame(){
        // Continue the game till the status is active
        while(this.status== GameStatus.ACTIVE){
            // player1 will make the move if its white's turn
            // else player2 will make the move
            if(isWhiteTurn){
//                makeMove(new Move(startBlock,endBlock),player1);
            }
            else{
//                makeMove(new Move(startBlock,endBlock),player2);
            }
        }
    }

    public void makeMove(Move move){
        // Initial check for valid move
        // To check if source and destination doesn't contain
        // the same color pieces.
        if (move.isValid()){
            Piece sourcePiece = move.getStartBlock().getPiece();
            // Check if source piece can be moved or not
            if(sourcePiece.moveStrategy.isValidMove(move.getStartBlock(), move.getEndBlock())){
                Piece destinationPiece = move.getEndBlock().getPiece();

                // check if destination block contains some peice
                if(destinationPiece != null ){
                    // if destination block contains King
                    // and currently white is playing --> White wins
                    if(destinationPiece instanceof KING && isWhiteTurn){
                        this.status = GameStatus.WHITE_WIN;
                        return;
                    }
                    // if destination block contains King
                    // and currently Black is playing --> Black wins
                    if(destinationPiece instanceof KING && !isWhiteTurn){
                        this.status = GameStatus.BLACK_WIN;
                        return;
                    }
                    // Set the destination piece as killed
                    destinationPiece.setCaptured(true);
                }
                // Adding the valid move to game logs
                movesLog.add(move);

                // Moving the source piece to the destination block
                move.getEndBlock().setPiece(sourcePiece);

                // Setting the source block to null (means it doesn't have any piece)
                move.getStartBlock().setPiece(null);

                // Toggling the turn
                // If it is white Turn, next will be Black Turn
                // else if it is Black Turn, next will be White Turn
                isWhiteTurn = !isWhiteTurn;
            }
        }
    }

    @Override
    public boolean checkMate(Move move){
        // Check if the move puts the opponent in checkmate
        Piece destinationPiece = move.getEndBlock().piece;
        if (destinationPiece instanceof KING)
            return true;
        return false;
    }

    @Override
    public List<Move> getMoves(){
        return this.movesLog;
    }

}

