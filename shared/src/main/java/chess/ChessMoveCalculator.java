package chess;

import java.util.Collection;

public interface ChessMoveCalculator {
    Collection<ChessMove> moveCalculator(ChessBoard board, ChessPosition position);
}

