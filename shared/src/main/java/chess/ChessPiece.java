package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        return switch (piece.getPieceType()) {
            case PieceType.BISHOP ->
                    List.of(new ChessMove(myPosition, new ChessPosition(1, 8), null));
            case PieceType.KING -> List.of();
            case PieceType.KNIGHT -> List.of();
            case PieceType.PAWN -> List.of();
            case PieceType.QUEEN -> List.of();
            case PieceType.ROOK -> List.of();// validRookMoves(piece, myPosition);
        };
    }

    // to memory heavy
    private Collection<ChessMove> validRookMoves(ChessPiece piece, ChessPosition position){
        //init
        List<ChessMove> moves = new ArrayList<ChessMove>();
        int i;
        // UP
        i = 1;
        while (position.getColumn() + i < 9){
            moves.add(new ChessMove(position, new ChessPosition(position.getRow(), position.getColumn()+i), null));
            i++;
        }
        // DOWN
        i = 1;
        while (position.getColumn() - i > 0){
            moves.add(new ChessMove(position, new ChessPosition(position.getRow(), position.getColumn()-i), null));
            i--;
        }
        // LEFT
        i = 1;
        while (position.getRow() - i > 0){
            moves.add(new ChessMove(position, new ChessPosition(position.getRow()-i, position.getColumn()), null));
            i--;
        }
        // RIGHT
        i = 1;
        while (position.getRow() + i < 9){
            moves.add(new ChessMove(position, new ChessPosition(position.getRow()+i, position.getColumn()), null));
            i++;
        }

        return moves;
    }
}
