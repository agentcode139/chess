package chess.movecalculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessMoveCalculator;
import chess.ChessPosition;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class KingMoveCalculator implements ChessMoveCalculator {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position){
        Set<ChessMove> validMoves = new HashSet<>();
        //UP
        // Check if move is out of bounds
        if (position.getRow()+1 <= 8){
            ChessPosition movePosition = new ChessPosition(position.getRow()+1,position.getColumn());
            if (board.getPiece(movePosition) != null){
                if (board.getPiece(movePosition).getTeamColor() != board.getPiece(position).getTeamColor()) {
                    validMoves.add(new ChessMove(position, movePosition, null));
                }
            } else {
                validMoves.add(new ChessMove(position, movePosition, null));
            }
        }


        //LEFT
        // Check if move is out of bounds
        if (position.getColumn()-1 >= 1){
            // IF PIECE in spot (Add if opposite color)
            ChessPosition movePosition = new ChessPosition(position.getRow(),position.getColumn()-1);
            if (board.getPiece(movePosition) != null){
                if (board.getPiece(movePosition).getTeamColor() != board.getPiece(position).getTeamColor()){
                    validMoves.add(new ChessMove(position,movePosition,null));
                }
            } else {
                validMoves.add(new ChessMove(position, movePosition, null));
            }
        }


        //DOWN
        // Check if move is out of bounds
        if (position.getRow()-1 >= 1){
            // IF PIECE in spot (Add if opposite color)
            ChessPosition movePosition = new ChessPosition(position.getRow()-1,position.getColumn());
            if (board.getPiece(movePosition) != null){
                if (board.getPiece(movePosition).getTeamColor() != board.getPiece(position).getTeamColor()){
                    validMoves.add(new ChessMove(position,movePosition,null));
                }
            } else {
            validMoves.add(new ChessMove(position,movePosition,null));
            }
        }
        //RIGHT
        // Check if move is out of bounds
        if (position.getColumn()+1 <= 8){
            // IF PIECE in spot (Add if opposite color)
            ChessPosition movePosition = new ChessPosition(position.getRow(),position.getColumn()+1);
            if (board.getPiece(movePosition) != null){
                if (board.getPiece(movePosition).getTeamColor() != board.getPiece(position).getTeamColor()){
                    validMoves.add(new ChessMove(position,movePosition,null));
                }
            } else {
                validMoves.add(new ChessMove(position,movePosition,null));
            }
        }

        //UPRIGHT
        // Check if move is out of bounds
        if (position.getRow()+1 <= 8 &&position.getColumn()+1 <= 8){
            // IF PIECE in spot (Add if opposite color)
            ChessPosition movePosition = new ChessPosition(position.getRow()+1,position.getColumn()+1);
            if (board.getPiece(movePosition) != null){
                if (board.getPiece(movePosition).getTeamColor() != board.getPiece(position).getTeamColor()){
                    validMoves.add(new ChessMove(position,movePosition,null));
                }
            } else {
                validMoves.add(new ChessMove(position,movePosition,null));
            }
        }

        //UPLEFT
        // Check if move is out of bounds
        if (position.getRow()+1 <= 8 && position.getColumn()-1 >= 1){
            // IF PIECE in spot (Add if opposite color)
            ChessPosition movePosition = new ChessPosition(position.getRow()+1,position.getColumn()-1);
            if (board.getPiece(movePosition) != null){
                if (board.getPiece(movePosition).getTeamColor() != board.getPiece(position).getTeamColor()){
                    validMoves.add(new ChessMove(position,movePosition,null));
                }
            } else {
                validMoves.add(new ChessMove(position, movePosition, null));
            }
        }


        //DOWNLEFT

            // Check if move is out of bounds
            if (position.getRow()-1 >= 1 && position.getColumn()-1 >= 1){
                // IF PIECE in spot (Add if opposite color)
                ChessPosition movePosition = new ChessPosition(position.getRow()-1,position.getColumn()-1);
                if (board.getPiece(movePosition) != null){
                    if (board.getPiece(movePosition).getTeamColor() != board.getPiece(position).getTeamColor()){
                        validMoves.add(new ChessMove(position,movePosition,null));
                    }
                } else {
                validMoves.add(new ChessMove(position,movePosition,null));
                }
            }

        //DOWNRIGHT
        // Check if move is out of bounds
        if (position.getRow()-1 >= 1 && position.getColumn()+1 <= 8){
            // IF PIECE in spot (Add if opposite color)
            ChessPosition movePosition = new ChessPosition(position.getRow()-1,position.getColumn()+1);
            if (board.getPiece(movePosition) != null){
                if (board.getPiece(movePosition).getTeamColor() != board.getPiece(position).getTeamColor()){
                    validMoves.add(new ChessMove(position,movePosition,null));
                }
            } else {
                validMoves.add(new ChessMove(position,movePosition,null));
            }
        }


        return validMoves;
    }
}
