package chess;

import java.util.Collection;
import java.util.List;

interface ChessMoveCalculator {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position);
}

class BishopMoveCalculator implements ChessMoveCalculator{
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position){
        return List.of(new ChessMove(position, new ChessPosition(1, 8), null));
    }
}