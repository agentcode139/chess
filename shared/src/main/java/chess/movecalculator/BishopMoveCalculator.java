package chess.movecalculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessMoveCalculator;
import chess.ChessPosition;

import java.util.Collection;
import java.util.List;

public class BishopMoveCalculator implements ChessMoveCalculator {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position){
        return List.of(new ChessMove(position, new ChessPosition(1, 8), null));
    }
}
