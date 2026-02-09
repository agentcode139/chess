package chess.movecalculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public interface LinearMoveCalculator {
    Collection<ChessMove> validMovesOnLine(ChessBoard board, ChessPosition startPosition, int rowIteration, int colIteration);
}

class LineMoveCalculator implements LinearMoveCalculator {
    private boolean inBounds(int row, int col){
        return (row < 9) && (row > 0) && (col < 9) && (col > 0);
    }
    @Override
    public Collection<ChessMove> validMovesOnLine(ChessBoard board, ChessPosition startPosition, int rowIteration, int colIteration) {
        Set<ChessMove> moves = new HashSet<>();
        for (int i = 1; i<9; i++) {
            // Check if move is out of bounds
            if (!inBounds(startPosition.getRow()+(i*rowIteration), startPosition.getColumn()+(i*colIteration))) {
                break;
            }
            // IF PIECE in spot (Add if opposite color)
            ChessPosition movePosition = new ChessPosition(startPosition.getRow()+(i*rowIteration),startPosition.getColumn()+(i*colIteration));
            if (board.getPiece(movePosition) != null){
                if (board.getPiece(movePosition).getTeamColor() != board.getPiece(startPosition).getTeamColor()){
                    moves.add(new ChessMove(startPosition,movePosition,null));
                }
                break;
            }
            moves.add(new ChessMove(startPosition,movePosition,null));
        }
        return moves;
    }
}