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

class KingMoveCalculator implements ChessMoveCalculator{
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position){
        return List.of();
    }
}

class KnightMoveCalculator implements ChessMoveCalculator{
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position){
        return List.of();
    }
}

class QueenMoveCalculator implements ChessMoveCalculator{
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position){
        return List.of();
    }
}

class RookMoveCalculator implements ChessMoveCalculator{
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position){
        return List.of();
    }
}

class PawnMoveCalculator implements ChessMoveCalculator{
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position){
        return List.of();
    }
}