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
        int dir;
        if (board.getPiece(position).getTeamColor() == ChessGame.TeamColor.WHITE){
            startRow = 2;
            promoteRow = 7;
            dir = 1;
        } else {
            startRow = 7;
            promoteRow = 2;
            dir = -1;
        }
        /*FORWARD*/
        ChessPosition movePosition = new ChessPosition(position.getRow() + dir, position.getColumn());
        if (inBounds(position.getRow()+dir, position.getColumn())
                && board.getPiece(movePosition) == null) {
            ChessPosition movePosition2 = new ChessPosition(position.getRow() + dir * 2, position.getColumn());
            if (position.getRow() == startRow
                    && inBounds(position.getRow()+dir*2, position.getColumn())
                    && board.getPiece(movePosition2) == null) {
                possibleMoves.add(new ChessMove(position, movePosition, null));
                possibleMoves.add(new ChessMove(position, movePosition2, null));

            } else {
                possibleMoves.addAll(promotionCheckMoves(position,movePosition,promoteRow));
            }


        }
        /*CAPTURE*/
        for (int i = 0; i<=1; i++) {
            ChessPosition movePos = new ChessPosition(position.getRow()+dir, position.getColumn()+(int)Math.pow(-1,i));
            if (inBounds(position.getRow() + dir, position.getColumn()+(int)Math.pow(-1,i))
                    && board.getPiece(movePos) != null
                    && board.getPiece(movePos).getTeamColor() != board.getPiece(position).getTeamColor()){

                possibleMoves.addAll(promotionCheckMoves(position,movePos,promoteRow));
            }
        }

        return possibleMoves;
    }

    private Collection<ChessMove> promotionCheckMoves(ChessPosition pos, ChessPosition movePos, int promoteRow){
        Set<ChessMove> moves = new HashSet<>();
        if (pos.getRow() == promoteRow) {
            moves.add(new ChessMove(pos, movePos, ChessPiece.PieceType.BISHOP));
            moves.add(new ChessMove(pos, movePos, ChessPiece.PieceType.KNIGHT));
            moves.add(new ChessMove(pos, movePos, ChessPiece.PieceType.ROOK));
            moves.add(new ChessMove(pos, movePos, ChessPiece.PieceType.QUEEN));
        } else {
            moves.add(new ChessMove(pos, movePos, null));
        }
        return moves;
    }
}
