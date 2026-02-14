package chess.movecalculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessMoveCalculator;
import chess.ChessPosition;

import java.util.Collection;

public class QueenMoveCalculator implements ChessMoveCalculator {
    public Collection<ChessMove> moveCalculator(ChessBoard board, ChessPosition position){
        Collection<ChessMove> validMoves = new RookMoveCalculator().moveCalculator(board,position);
        validMoves.addAll(new BishopMoveCalculator().moveCalculator(board,position));
        return validMoves;
    }
}
