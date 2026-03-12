package chess.movecalculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessMoveCalculator;
import chess.ChessPosition;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static java.lang.Math.pow;

public class KnightMoveCalculator implements ChessMoveCalculator {
    public Collection<ChessMove> moveCalculator(ChessBoard board, ChessPosition position) {
        Set<ChessMove> validMoves = new HashSet<>();
        //UP
        for (int d = 0; d < 2; d++) {
            // Check if move is out of bounds
            if (position.getRow() + 2 > 8 || position.getColumn() + (int) pow(-1, d) < 1 || position.getColumn() + (int) pow(-1, d) > 8) {
                continue;
            }
            // IF PIECE in spot (Add if opposite color)
            ChessPosition movePosition = new ChessPosition(position.getRow() + 2, position.getColumn() + (int) pow(-1, d));
            if (board.getPiece(movePosition) != null) {
                if (board.getPiece(movePosition).getTeamColor() != board.getPiece(position).getTeamColor()) {
                    validMoves.add(new ChessMove(position, movePosition, null));
                }
                continue;
            }
            validMoves.add(new ChessMove(position, movePosition, null));
        }
        //DOWN
        for (int d = 0; d < 2; d++) {
            // Check if move is out of bounds
            if (position.getRow() - 2 < 1 || position.getColumn() + (int) pow(-1, d) < 1 || position.getColumn() + (int) pow(-1, d) > 8) {
                continue;
            }
            // IF PIECE in spot (Add if opposite color)
            ChessPosition movePosition = new ChessPosition(position.getRow() - 2, position.getColumn() + (int) pow(-1, d));
            if (board.getPiece(movePosition) != null) {
                if (board.getPiece(movePosition).getTeamColor() != board.getPiece(position).getTeamColor()) {
                    validMoves.add(new ChessMove(position, movePosition, null));
                }
                continue;
            }
            validMoves.add(new ChessMove(position, movePosition, null));
        }
        //LEFT
        for (int d = 0; d < 2; d++) {
            // Check if move is out of bounds
            if (position.getColumn() - 2 < 1 || position.getRow() + (int) pow(-1, d) < 1 || position.getRow() + (int) pow(-1, d) > 8) {
                continue;
            }
            // IF PIECE in spot (Add if opposite color)
            ChessPosition movePosition = new ChessPosition(position.getRow() + (int) pow(-1, d), position.getColumn() - 2);
            if (board.getPiece(movePosition) != null) {
                if (board.getPiece(movePosition).getTeamColor() != board.getPiece(position).getTeamColor()) {
                    validMoves.add(new ChessMove(position, movePosition, null));
                }
                continue;
            }
            validMoves.add(new ChessMove(position, movePosition, null));
        }
        //RIGHT
        for (int d = 0; d < 2; d++) {
            // Check if move is out of bounds
            if (position.getColumn() + 2 > 8 || position.getRow() + (int) pow(-1, d) < 1 || position.getRow() + (int) pow(-1, d) > 8) {
                continue;
            }
            // IF PIECE in spot (Add if opposite color)
            ChessPosition movePosition = new ChessPosition(position.getRow() + (int) pow(-1, d), position.getColumn() + 2);
            if (board.getPiece(movePosition) != null) {
                if (board.getPiece(movePosition).getTeamColor() != board.getPiece(position).getTeamColor()) {
                    validMoves.add(new ChessMove(position, movePosition, null));
                }
                continue;
            }
            validMoves.add(new ChessMove(position, movePosition, null));
        }

        return validMoves;
    }
}
