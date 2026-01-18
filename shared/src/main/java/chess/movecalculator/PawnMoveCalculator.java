package chess.movecalculator;

import chess.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static java.lang.Math.pow;

public class PawnMoveCalculator implements ChessMoveCalculator {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position){
        //TODO implement Pawn movement (needs direction)
        if (position.getRow()+1 > 8 || position.getRow()-1 < 1){
            return new HashSet<>();
        }

        Set<ChessMove> validMoves = new HashSet<>();
        int dir = board.getPiece(position).getTeamColor() == ChessGame.TeamColor.WHITE ? 1 : -1; //If white add else sub direction
        ChessPosition forward = new ChessPosition(position.getRow() + dir, position.getColumn());
        boolean promote = false;


        //Promotion
        if (position.getRow() == 2 && board.getPiece(position).getTeamColor() == ChessGame.TeamColor.BLACK){
            promote = true;
        } else if (position.getRow() == 7 && board.getPiece(position).getTeamColor() == ChessGame.TeamColor.WHITE){
            promote = true;
        }

        // Straight
        if (board.getPiece(forward) == null){
            if (promote){
                validMoves.add(new ChessMove(position, forward, ChessPiece.PieceType.BISHOP));
                validMoves.add(new ChessMove(position, forward, ChessPiece.PieceType.KNIGHT));
                validMoves.add(new ChessMove(position, forward, ChessPiece.PieceType.ROOK));
                validMoves.add(new ChessMove(position, forward, ChessPiece.PieceType.QUEEN));
            } else {
                validMoves.add(new ChessMove(position, forward, null));
            }
            ChessPosition forward2 = new ChessPosition(position.getRow() + 2 * dir, position.getColumn());
            if (((position.getRow() == 2 && board.getPiece(position).getTeamColor() == ChessGame.TeamColor.WHITE) || (position.getRow() == 7 && board.getPiece(position).getTeamColor() == ChessGame.TeamColor.BLACK)) && board.getPiece(forward2) == null){
                if (promote) {
                    validMoves.add(new ChessMove(position, forward2, ChessPiece.PieceType.BISHOP));
                    validMoves.add(new ChessMove(position, forward2, ChessPiece.PieceType.KNIGHT));
                    validMoves.add(new ChessMove(position, forward2, ChessPiece.PieceType.ROOK));
                    validMoves.add(new ChessMove(position, forward2, ChessPiece.PieceType.QUEEN));
                } else {
                    validMoves.add(new ChessMove(position, new ChessPosition(position.getRow() + 2 * dir, position.getColumn()), null));
                }
            }
        }

        //Catpture
        for (int d = 0; d<2; d++){
            if (position.getColumn() + (int)pow(-1,d) > 8 || position.getColumn() + (int)pow(-1,d) < 1){
                continue;
            }

            ChessPosition check = new ChessPosition(position.getRow() + dir, position.getColumn() + (int)pow(-1,d));
            if (board.getPiece(check) != null){
                if (board.getPiece(check).getTeamColor() != board.getPiece(position).getTeamColor()) {
                    if (promote) {
                        validMoves.add(new ChessMove(position, check, ChessPiece.PieceType.BISHOP));
                        validMoves.add(new ChessMove(position, check, ChessPiece.PieceType.KNIGHT));
                        validMoves.add(new ChessMove(position, check, ChessPiece.PieceType.ROOK));
                        validMoves.add(new ChessMove(position, check, ChessPiece.PieceType.QUEEN));
                    } else {
                        validMoves.add(new ChessMove(position, check, null));
                    }
                }
            }
        }

        return validMoves;
    }
}
