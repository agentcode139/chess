package chess;

import java.util.*;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    ChessBoard chessBoard;
    TeamColor teamTurn;
    List<ChessGameMove> gameHistory;

    public ChessGame() {
        // Turn Logic
        this.teamTurn = TeamColor.WHITE;
        this.gameHistory = new LinkedList<>();
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
    private Collection<ChessPosition> getTeamPositions(TeamColor team){
        Set<ChessPosition> pieceSpots = new HashSet<>();
        for (int i = 1; i <= 8; i++){
            for (int j = 1; j <= 8; j++){
                ChessPosition spot = new ChessPosition(i,j);
                if (this.chessBoard.getPiece(spot).getTeamColor() == team){
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
    private ChessPosition kingPosition(TeamColor teamColor){
        for (int i = 1; i <= 8; i++){
            for (int j = 1; j <= 8; j++){
                ChessPosition spot = new ChessPosition(i,j);
                if (this.chessBoard.getPiece(spot) != null && this.chessBoard.getPiece(spot).getPieceType() == ChessPiece.PieceType.KING && this.chessBoard.getPiece(spot).getTeamColor() == teamColor){
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
        /* all possible moves from piece */
        Collection<ChessMove> moves = this.chessBoard.getPiece(startPosition).pieceMoves(this.chessBoard,startPosition);
        moves.addAll(validSpecialMoves(startPosition));
        /* Find all moves the cause check for team */
        Collection<ChessMove> putsInCheck = new HashSet<>();
        TeamColor teamColor = this.chessBoard.getPiece(startPosition).getTeamColor();
        for (ChessMove move : moves){
            /* Init Check game */
            ChessGame checkGame = new ChessGame();
            checkGame.setBoard(chessBoard); //TODO: Make propper copy of chessBoard
            checkGame.setTeamTurn(teamColor);
            //TODO fix try block
            try {
                checkGame.makeMove(move);
            } catch (InvalidMoveException ignored) {
                putsInCheck.add(move); //Needs to be removed
            }
            if (checkGame.isInCheck(teamColor)){
                putsInCheck.add(move);
            }
        }
        /* Remove moves that put team in check */
        moves.removeAll(putsInCheck);

        return moves;
    }

    /**
     * Gets a valid special moves for a piece at the given location
     * Special moves are En passant and Castling
     *
     * @param startPosition
     * @return Set of valid special moves for requested piece, or null if no piece at startPosition
     */
    private Collection<ChessMove> validSpecialMoves(ChessPosition startPosition){
        // En passant: check latest move in log
        // Castling: check for any movement from king and rook
        //TODO: remove testing empty set
        return new HashSet<>();
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        //TODO: if move is special special update else
        if (false && validSpecialMoves(move.getStartPosition()).contains(move)) {
            //special action
        } else {
            //update
            ChessPiece pieceMoved = chessBoard.getPiece(move.getStartPosition());
            if (pieceMoved != null && pieceMoved.pieceMoves(chessBoard, move.getStartPosition()).contains(move)) {
                /* Move */
                chessBoard.addPiece(move.getEndPosition(), pieceMoved);
                chessBoard.addPiece(move.getStartPosition(), null);
                setTeamTurn(getTeamTurn() == TeamColor.WHITE ? TeamColor.BLACK:TeamColor.WHITE);
            } else {
                throw new InvalidMoveException("Move is invalid");
            }
        }

    }

    private void sudoMakeMove(ChessMove move){
        ChessPiece pieceMoved = chessBoard.getPiece(move.getStartPosition());
        chessBoard.addPiece(move.getEndPosition(),pieceMoved);
        chessBoard.addPiece(move.getStartPosition(),null);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        Set<ChessMove> validEnemyMoves = new HashSet<>();
        for (int i = 1; i <= 8; i++){
            for (int j = 1; j <= 8; j++){
                ChessPosition spot = new ChessPosition(i,j);
                if (chessBoard.getPiece(spot) != null && chessBoard.getPiece(spot).getTeamColor() != teamColor){
                    validEnemyMoves.addAll(chessBoard.getPiece(spot).pieceMoves(chessBoard,spot));
                }
            }
        }
        Set<ChessPosition> validEnemyMoveEndSpots = new HashSet<>();
        for(ChessMove move: validEnemyMoves){
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
        return isInCheck(teamColor) && validMoves(kingPosition(teamColor)).isEmpty();
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
        for (ChessPosition piecePosition : getTeamPositions(teamColor)){
            validTeamMoves.addAll(validMoves(piecePosition));
        }
        return validTeamMoves.isEmpty();
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
