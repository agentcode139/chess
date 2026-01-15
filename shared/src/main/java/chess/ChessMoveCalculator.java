package chess;

import java.util.Collection;

public interface ChessMoveCalculator {
    Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position);
}

