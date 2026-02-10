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
            validMoves.addAll(new LineMoveCalculator().validMovesOnLine(board,position,r,c, 8));
        }
        return validMoves;
    }
}
