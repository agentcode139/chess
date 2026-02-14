package chess.movecalculator;

import chess.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class RookMoveCalculator implements ChessMoveCalculator {
    public Collection<ChessMove> moveCalculator(ChessBoard board, ChessPosition position){
        Set<ChessMove> validMoves = new HashSet<>();
        for (int d = 0; d < 2; d++) {
            int dir = (int)Math.pow(-1,d);
            validMoves.addAll(new LineMoveCalculator().validMovesOnLine(board,position,dir,0, 8));
            validMoves.addAll(new LineMoveCalculator().validMovesOnLine(board,position,0,dir, 8));
        }
        return validMoves;
    }
}
