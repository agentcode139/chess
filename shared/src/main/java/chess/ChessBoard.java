package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    ChessPiece[][] gameboard;

    public ChessBoard() {
        this.gameboard = new ChessPiece[8][8]; //Valid spots range: {0 < i,j <= 8}
    }

    public ChessBoard(ChessBoard board){
        this.gameboard = new ChessPiece[8][8];
        for (int i = 1; i <= 8; i++){
            for (int j = 1; j <= 8; j++){
                this.gameboard[i-1][j-1] = board.getPiece(new ChessPosition(i,j));
            }
        }

    }

    // TODO: Implement deepcopy clone
    @Override
    public ChessBoard clone(){
        try {
            ChessBoard clone = (ChessBoard) super.clone();

            ChessPiece[][] cloneGameboard = gameboard.clone();
            clone.gameboard = cloneGameboard;

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        gameboard[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return gameboard[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        // Blank
        this.gameboard = new ChessPiece[8][8]; //Blank
        // Pawns
        ChessPiece.PieceType[] pieceOrder = {
                ChessPiece.PieceType.ROOK, ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.QUEEN,
                ChessPiece.PieceType.KING, ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.ROOK,
        };
        // Fill
        for (int i = 1; i <= 8; i++){
            // White
            this.addPiece(new ChessPosition(1,i), new ChessPiece(ChessGame.TeamColor.WHITE, pieceOrder[i-1]));
            this.addPiece(new ChessPosition(2,i), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));

            // Black
            this.addPiece(new ChessPosition(7,i), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
            this.addPiece(new ChessPosition(8,i), new ChessPiece(ChessGame.TeamColor.BLACK, pieceOrder[i-1]));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(gameboard, that.gameboard);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(gameboard);
    }
}
