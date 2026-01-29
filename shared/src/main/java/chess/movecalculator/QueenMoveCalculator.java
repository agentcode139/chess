package chess.movecalculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessMoveCalculator;
import chess.ChessPosition;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class QueenMoveCalculator implements ChessMoveCalculator {
    public Collection<ChessMove> moveCalculator(ChessBoard board, ChessPosition position){
        Set<ChessMove> validMoves = new HashSet<>();
        for (int d = 1; d < 9; d++) {
            validMoves.addAll(validMovesInDir(board,position,d));
        }
        return validMoves;
    }

    private boolean inBounds(int row, int col){
        return (row < 9) && (row > 0) && (col < 9) && (col > 0);
    }

    private Collection<ChessMove> validMovesInDir(ChessBoard board, ChessPosition position, int direction){
        Set<ChessMove> moves = new HashSet<>();
        int r;
        int c = switch (direction) {
            case 1 -> {
                r = 1;
                yield 0;
            }
            case 2 -> {
                r = 0;
                yield 1;
            }
            case 3 -> {
                r = 0;
                yield -1;
            }
            case 4 -> {
                r = -1;
                yield 0;
            }
            case 5 -> {
                r = 1;
                yield 1;
            }
            case 6 -> {
                r = -1;
                yield 1;
            }
            case 7 -> {
                r = 1;
                yield -1;
            }
            case 8 -> {
                r = -1;
                yield -1;
            }

            default -> {
                r = 0;
                yield 0;
            }
        };
        for (int i = 1; i<9; i++) {
            // Check if move is out of bounds
            if (!inBounds(position.getRow()+(i*r), position.getColumn()+(i*c))) {
                break;
            }
            // IF PIECE in spot (Add if opposite color)
            ChessPosition movePosition = new ChessPosition(position.getRow()+(i*r),position.getColumn()+(i*c));
            if (board.getPiece(movePosition) != null){
                if (board.getPiece(movePosition).getTeamColor() != board.getPiece(position).getTeamColor()){
                    moves.add(new ChessMove(position,movePosition,null));
                }
                break;
            }
            moves.add(new ChessMove(position,movePosition,null));
        }
        return moves;
    }
}
