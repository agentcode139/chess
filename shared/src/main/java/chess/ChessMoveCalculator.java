package chess;

import java.util.Collection;
import java.util.List;

public interface ChessMoveCalculator {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position);
}

