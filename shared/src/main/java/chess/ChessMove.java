package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {

    private final ChessPosition startPos;
    private final ChessPosition endPos;
    private final ChessPiece.PieceType proPiece;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.startPos = startPosition;
        this.endPos = endPosition;
        this.proPiece = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return startPos;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return endPos;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return proPiece;
    }

    @Override
    public String toString() {
        return String.format("%s%s", startPos, endPos);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessMove chessMove = (ChessMove) o;
        boolean startEq = Objects.equals(startPos, chessMove.startPos);
        boolean endEq = Objects.equals(endPos, chessMove.endPos);
        boolean proPieceEq = proPiece == chessMove.proPiece;
        return startEq && endEq && proPieceEq;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startPos, endPos, proPiece);
    }
}
