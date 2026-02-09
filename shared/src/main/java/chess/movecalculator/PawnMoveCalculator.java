package chess.movecalculator;

import chess.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class PawnMoveCalculator implements ChessMoveCalculator {
    private boolean inBounds(int row, int col){
        return row < 9 && row > 0 && col < 9 && col > 0;
    }

    @Override
    public Collection<ChessMove> moveCalculator(ChessBoard board, ChessPosition position) {
        Set<ChessMove> possibleMoves = new HashSet<>();
        int startRow;
        int promoteRow;
        int direction;
        if (board.getPiece(position).getTeamColor() == ChessGame.TeamColor.WHITE){
            startRow = 2;
            promoteRow = 7;
            direction = 1;
        } else {
            startRow = 7;
            promoteRow = 2;
            direction = -1;
        }
        /*FORWARD*/
        if (inBounds(position.getRow() + direction, position.getColumn())
                && board.getPiece(new ChessPosition(position.getRow() + direction, position.getColumn())) == null) {
            ChessPosition movePosition = new ChessPosition(position.getRow() + direction, position.getColumn());
            if (position.getRow() == startRow
                    && inBounds(position.getRow() + direction*2, position.getColumn())
                    && board.getPiece(new ChessPosition(position.getRow() + direction * 2, position.getColumn())) == null) {
                possibleMoves.add(new ChessMove(position, movePosition, null));
                ChessPosition movePosition2 = new ChessPosition(position.getRow() + direction * 2, position.getColumn());
                possibleMoves.add(new ChessMove(position, movePosition2, null));

            } else if (position.getRow() == promoteRow) {
                possibleMoves.add(new ChessMove(position, movePosition, ChessPiece.PieceType.BISHOP));
                possibleMoves.add(new ChessMove(position, movePosition, ChessPiece.PieceType.KNIGHT));
                possibleMoves.add(new ChessMove(position, movePosition, ChessPiece.PieceType.ROOK));
                possibleMoves.add(new ChessMove(position, movePosition, ChessPiece.PieceType.QUEEN));
            } else {
                possibleMoves.add(new ChessMove(position, movePosition, null));
            }


        }
        /*CAPTURE*/
        for (int i = 0; i<=1; i++) {
            if (inBounds(position.getRow() + direction, position.getColumn() + (int)Math.pow(-1,i))
                    && board.getPiece(new ChessPosition(position.getRow() + direction, position.getColumn() + (int)Math.pow(-1,i))) != null
                    && board.getPiece(new ChessPosition(position.getRow() + direction, position.getColumn() + (int)Math.pow(-1,i))).getTeamColor() != board.getPiece(position).getTeamColor()){
                ChessPosition movePosition = new ChessPosition(position.getRow() + direction, position.getColumn() + (int)Math.pow(-1,i));
                if (position.getRow() == promoteRow) {
                    possibleMoves.add(new ChessMove(position, movePosition, ChessPiece.PieceType.BISHOP));
                    possibleMoves.add(new ChessMove(position, movePosition, ChessPiece.PieceType.KNIGHT));
                    possibleMoves.add(new ChessMove(position, movePosition, ChessPiece.PieceType.ROOK));
                    possibleMoves.add(new ChessMove(position, movePosition, ChessPiece.PieceType.QUEEN));
                } else {
                    possibleMoves.add(new ChessMove(position, movePosition, null));
                }
            }
        }

        return possibleMoves;
    }
}
