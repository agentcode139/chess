package chess.movecalculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public interface LinearMoveCalculator {
    Collection<ChessMove> validMovesOnLine(ChessBoard board, ChessPosition startPosition, int rowIteration, int colIteration, int distanceChecked);
}

class LineMoveCalculator implements LinearMoveCalculator {
    private boolean inBounds(int row, int col){
        return (row < 9) && (row > 0) && (col < 9) && (col > 0);
    }

    public Collection<ChessMove> validMovesOnLine(ChessBoard board, ChessPosition startPos, int rIter, int cIter, int dist) {
        Set<ChessMove> moves = new HashSet<>();
        if (dist > 8) {
            dist = 8; // Max distance
        }
        for (int i = 1; i<=dist; i++) {
            // Check if move is out of bounds
            if (!inBounds(startPos.getRow()+(i*rIter), startPos.getColumn()+(i*cIter))) {
                break;
            }
            // IF PIECE in spot (Add if opposite color)
            ChessPosition movePosition = new ChessPosition(startPos.getRow()+(i*rIter),startPos.getColumn()+(i*cIter));
            if (board.getPiece(movePosition) != null){
                if (board.getPiece(movePosition).getTeamColor() != board.getPiece(startPos).getTeamColor()){
                    moves.add(new ChessMove(startPos,movePosition,null));
                }
                break;
            }
            moves.add(new ChessMove(startPos,movePosition,null));
        }
        return moves;
    }
}