package chess.movecalculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessMoveCalculator;
import chess.ChessPosition;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class KingMoveCalculator implements ChessMoveCalculator {
    private boolean inBounds(int row, int col){
        return (row < 9) && (row > 0) && (col < 9) && (col > 0);
    }

    public Collection<ChessMove> moveCalculator(ChessBoard board, ChessPosition position){
        Set<ChessMove> validMoves = new HashSet<>();
        for (int d = 1; d < 9; d++) {
            int r;
            int c = switch (d) {
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

            // Check if move is out of bounds
            if (inBounds(position.getRow()+r,position.getColumn()+c)) {
                ChessPosition movePosition = new ChessPosition(position.getRow()+r, position.getColumn()+c);
                if (board.getPiece(movePosition) == null
                        || board.getPiece(movePosition).getTeamColor() != board.getPiece(position).getTeamColor()) {
                    validMoves.add(new ChessMove(position, movePosition, null));
                }
            }
        }
        return validMoves;
    }
}
