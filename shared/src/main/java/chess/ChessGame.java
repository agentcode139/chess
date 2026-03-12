package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    /* Data */
    ChessBoard chessBoard;
    /* Status Variables */
    TeamColor teamTurn;

    public ChessGame() {
        // Turn Logic
        this.teamTurn = TeamColor.WHITE;
        // Board Setup
        this.chessBoard = new ChessBoard();
        this.chessBoard.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }

    /**
     * Find the position of a team's king on the board
     *
     * @param team of team to check for
     * @return ChessPosition of king of the team
     */
    private Collection<ChessPosition> getTeamPositions(TeamColor team) {
        Set<ChessPosition> pieceSpots = new HashSet<>();
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition spot = new ChessPosition(i, j);
                if (this.chessBoard.getPiece(spot) != null && this.chessBoard.getPiece(spot).getTeamColor() == team) {
                    pieceSpots.add(spot);
                }
            }
        }
        return pieceSpots;
    }

    /**
     * Find the position of a team's king on the board
     *
     * @param teamColor of team to check for
     * @return ChessPosition of king of the team
     */
    private ChessPosition kingPosition(TeamColor teamColor) {
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition spot = new ChessPosition(i, j);
                if (this.chessBoard.getPiece(spot) != null
                        && this.chessBoard.getPiece(spot).getPieceType() == ChessPiece.PieceType.KING
                        && this.chessBoard.getPiece(spot).getTeamColor() == teamColor) {
                    return spot;
                }
            }
        }
        return null; // or throw error about no king?
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        TeamColor teamColor = this.chessBoard.getPiece(startPosition).getTeamColor();
        /* all possible moves from piece */
        Collection<ChessMove> moves = this.chessBoard.getPiece(startPosition).pieceMoves(this.chessBoard, startPosition);

        /* Find all moves the cause check for team */
        Collection<ChessMove> putsInCheck = new HashSet<>();

        for (ChessMove move : moves) {
            /* Init Check game */
            ChessGame checkGame = new ChessGame();
            checkGame.setBoard(new ChessBoard(chessBoard));
            checkGame.setTeamTurn(teamColor);
            try {
                checkGame.makeMove(move);
                if (checkGame.isInCheck(teamColor)) {
                    putsInCheck.add(move);
                }
            } catch (InvalidMoveException ignored) {
                putsInCheck.add(move);
            }
        }
        /* Remove moves that put team in check */
        moves.removeAll(putsInCheck);

        return moves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece pMoved = chessBoard.getPiece(move.getStartPosition());

        /*NORMAL MOVES*/
        //update
        if (pMoved != null
                && pMoved.pieceMoves(chessBoard, move.getStartPosition()).contains(move)
                && pMoved.getTeamColor() == this.teamTurn) {
            /* Move */
            if ((pMoved.getPieceType() == ChessPiece.PieceType.PAWN && move.getPromotionPiece() != null)) {
                chessBoard.addPiece(move.getEndPosition(), new ChessPiece(pMoved.getTeamColor(), move.getPromotionPiece()));
            } else {
                chessBoard.addPiece(move.getEndPosition(), pMoved);
            }
            chessBoard.addPiece(move.getStartPosition(), null);

            if (isInCheck(pMoved.getTeamColor())) {
                // undo
                chessBoard.addPiece(move.getStartPosition(), pMoved);
                chessBoard.addPiece(move.getEndPosition(), null);
                // Error
                throw new InvalidMoveException("Move is invalid");
            }
        } else {
            throw new InvalidMoveException("Move is invalid");
        }

        // Status update
        setTeamTurn(getTeamTurn() == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        Set<ChessMove> validEnemyMoves = new HashSet<>();
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition spot = new ChessPosition(i, j);
                if (chessBoard.getPiece(spot) != null && chessBoard.getPiece(spot).getTeamColor() != teamColor) {
                    validEnemyMoves.addAll(chessBoard.getPiece(spot).pieceMoves(chessBoard, spot));
                }
            }
        }
        Set<ChessPosition> validEnemyMoveEndSpots = new HashSet<>();
        for (ChessMove move : validEnemyMoves) {
            validEnemyMoveEndSpots.add(move.getEndPosition());
        }
        return validEnemyMoveEndSpots.contains(kingPosition(teamColor));
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        Collection<ChessMove> validTeamMoves = new HashSet<>();
        Collection<ChessPosition> teamSpots = getTeamPositions(teamColor);
        for (ChessPosition spot : teamSpots) {
            validTeamMoves.addAll(validMoves(spot));
        }
        return isInCheck(teamColor) && validTeamMoves.isEmpty();
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        Collection<ChessMove> validTeamMoves = new HashSet<>();
        for (ChessPosition piecePosition : getTeamPositions(teamColor)) {
            validTeamMoves.addAll(validMoves(piecePosition));
        }
        return validTeamMoves.isEmpty() && !isInCheckmate(teamColor);
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.chessBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.chessBoard;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(chessBoard, chessGame.chessBoard) && teamTurn == chessGame.teamTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(chessBoard, teamTurn);
    }
}
