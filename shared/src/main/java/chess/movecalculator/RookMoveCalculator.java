package chess.movecalculator;

import chess.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class RookMoveCalculator implements ChessMoveCalculator {
    public Collection<ChessMove> moveCalculator(ChessBoard board, ChessPosition position){
        Set<ChessMove> validMoves = new HashSet<>();
        //UP
        for (int i = 1; i<9; i++) {
            // Check if move is out of bounds
            if (position.getRow()+i > 8){
                break;
            }
            // IF PIECE in spot (Add if opposite color)
            ChessPosition movePosition = new ChessPosition(position.getRow()+i,position.getColumn());
            if (board.getPiece(movePosition) != null){
                if (board.getPiece(movePosition).getTeamColor() != board.getPiece(position).getTeamColor()){
                    validMoves.add(new ChessMove(position,movePosition,null));
                }
                break;
            }
            validMoves.add(new ChessMove(position,movePosition,null));
        }
        //LEFT
        for (int i = 1; i<9; i++) {
            // Check if move is out of bounds
            if (position.getColumn()-i < 1){
                break;
            }
            // IF PIECE in spot (Add if opposite color)
            ChessPosition movePosition = new ChessPosition(position.getRow(),position.getColumn()-i);
            if (board.getPiece(movePosition) != null){
                if (board.getPiece(movePosition).getTeamColor() != board.getPiece(position).getTeamColor()){
                    validMoves.add(new ChessMove(position,movePosition,null));
                }
                break;
            }
            validMoves.add(new ChessMove(position,movePosition,null));
        }
        //DOWN
        for (int i = 1; i<9; i++) {
            // Check if move is out of bounds
            if (position.getRow()-i < 1){
                break;
            }
            // IF PIECE in spot (Add if opposite color)
            ChessPosition movePosition = new ChessPosition(position.getRow()-i,position.getColumn());
            if (board.getPiece(movePosition) != null){
                if (board.getPiece(movePosition).getTeamColor() != board.getPiece(position).getTeamColor()){
                    validMoves.add(new ChessMove(position,movePosition,null));
                }
                break;
            }
            validMoves.add(new ChessMove(position,movePosition,null));
        }
        //RIGHT
        for (int i = 1; i<9; i++) {
            // Check if move is out of bounds
            if (position.getColumn()+i > 8){
                break;
            }
            // IF PIECE in spot (Add if opposite color)
            ChessPosition movePosition = new ChessPosition(position.getRow(),position.getColumn()+i);
            if (board.getPiece(movePosition) != null){
                if (board.getPiece(movePosition).getTeamColor() != board.getPiece(position).getTeamColor()){
                    validMoves.add(new ChessMove(position,movePosition,null));
                }
                break;
            }
            validMoves.add(new ChessMove(position,movePosition,null));
        }

        return validMoves;
    }
}
