package chess;

import java.util.*;

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
    //List<ChessGameMove> gameHistory;
    ChessGameMove priorMove;
    Map<ChessPosition, Boolean> hasMoved;

    public ChessGame() {
        // Turn Logic
        this.teamTurn = TeamColor.WHITE;
        this.priorMove = null;
        // TODO: init this.hasMoved to false for all pieces (Rooks and kings)
        this.hasMoved = new HashMap<>();
        // hasMoved[new ChessPosition(1,1)] = new Boolean(false); // Broken

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
                if (this.chessBoard.getPiece(spot) != null && this.chessBoard.getPiece(spot).getTeamColor() == team){
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
                if (this.chessBoard.getPiece(spot) != null
                        && this.chessBoard.getPiece(spot).getPieceType() == ChessPiece.PieceType.KING
                        && this.chessBoard.getPiece(spot).getTeamColor() == teamColor){
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
        Collection<ChessMove> moves = this.chessBoard.getPiece(startPosition).pieceMoves(this.chessBoard,startPosition);
        // Castling
        if (startPosition.equals(new ChessPosition(1, 4))) {
            moves.add(new ChessMove(startPosition, new ChessPosition(1, 6), null)); //TODO: Add all special moves
            moves.add(new ChessMove(startPosition, new ChessPosition(1, 3), null)); //TODO: Add all special moves
        } else if (startPosition.equals(new ChessPosition(8, 4))){
            moves.add(new ChessMove(startPosition, new ChessPosition(8, 6), null)); //TODO: Add all special moves
            moves.add(new ChessMove(startPosition, new ChessPosition(8, 3), null)); //TODO: Add all special moves
        }
        // En Passant REALLY HARD
//        if (priorMove != null && priorMove.piece().getPieceType() == ChessPiece.PieceType.PAWN){
//
//        }

        /* Find all moves the cause check for team */
        Collection<ChessMove> putsInCheck = new HashSet<>();

        for (ChessMove move : moves){
            /* Init Check game */
            ChessGame checkGame = new ChessGame();
            checkGame.setBoard(new ChessBoard(chessBoard));
            checkGame.setTeamTurn(teamColor);
            try {
                checkGame.makeMove(move);
                if (checkGame.isInCheck(teamColor)){
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
        ChessPiece pieceMoved = chessBoard.getPiece(move.getStartPosition());
        if (pieceMoved != null && pieceMoved.getTeamColor() != this.teamTurn){
            throw new InvalidMoveException("Move is out of turn");
        }
        // TODO: if move is special special update else
        if (pieceMoved != null && !pieceMoved.pieceMoves(this.chessBoard,move.getStartPosition()).contains(move)) { /*SPECIAL MOVES*/
            //special action
//            if (priorMove != null && priorMove.piece().getPieceType() == ChessPiece.PieceType.PAWN
//                    && pieceMoved.getPieceType() == ChessPiece.PieceType.PAWN){
//                // En passant:
//                // Move piece
//
//                // Remove Captured
//
//                // Check
//                if (isInCheck(pieceMoved.getTeamColor())){
//                    // undo
//                    chessBoard.addPiece(move.getStartPosition(), pieceMoved);
//                    chessBoard.addPiece(move.getEndPosition(), null);
//                    // Error
//                    throw new InvalidMoveException("Move is invalid");
//                }
//            } else {
                // Castling:
                // Check for any movement from king and rook
                ChessPosition rookToMovePosition = new ChessPosition(move.getStartPosition().getRow(),move.getEndPosition().getColumn()<4? 1:8);
                if (hasMoved.get(move.getStartPosition()) == true || hasMoved.get(rookToMovePosition) == true){
                    throw new InvalidMoveException("Move is invalid");
                }
                // Check if space is empty
                if (move.getEndPosition().getColumn()<4){
                    // Check left
                    for (int i = 2; i<4; i++) { //TODO: set correct spots to check
                        if (chessBoard.getPiece(new ChessPosition(move.getStartPosition().getRow(), i)) != null) {
                            throw new InvalidMoveException("Move is invalid");
                        }
                    }
                } else {
                    // Check right
                    for (int i = 6; i<8; i++) { //TODO: set correct spots to check
                        if (chessBoard.getPiece(new ChessPosition(move.getStartPosition().getRow(), i)) != null) {
                            throw new InvalidMoveException("Move is invalid");
                        }
                    }
                }
                // Move King
                chessBoard.addPiece(move.getEndPosition(), pieceMoved);
                chessBoard.addPiece(move.getStartPosition(), null);
                // Move Rook
                ChessPiece rookMoved = chessBoard.getPiece(rookToMovePosition);
                chessBoard.addPiece(move.getEndPosition(), rookMoved);
                chessBoard.addPiece(rookToMovePosition, null);
                // Check
                if (isInCheck(pieceMoved.getTeamColor())){
                    // undo
                    chessBoard.addPiece(move.getStartPosition(), pieceMoved);
                    chessBoard.addPiece(move.getEndPosition(), null);
                    chessBoard.addPiece(rookToMovePosition, rookMoved);
                    chessBoard.addPiece(move.getEndPosition(), null);
                    // Error
                    throw new InvalidMoveException("Move is invalid");
                }
//            }


        } else { /*NORMAL MOVES*/
            //update
            if (pieceMoved != null && pieceMoved.pieceMoves(chessBoard, move.getStartPosition()).contains(move)) {
                /* Move */
                if ((pieceMoved.getPieceType() == ChessPiece.PieceType.PAWN && move.getPromotionPiece() != null)) {
                    chessBoard.addPiece(move.getEndPosition(), new ChessPiece(pieceMoved.getTeamColor(), move.getPromotionPiece()));
                } else {
                    chessBoard.addPiece(move.getEndPosition(), pieceMoved);
                }
                chessBoard.addPiece(move.getStartPosition(), null);

                if (isInCheck(pieceMoved.getTeamColor())){
                    // undo
                    chessBoard.addPiece(move.getStartPosition(), pieceMoved);
                    chessBoard.addPiece(move.getEndPosition(), null);
                    // Error
                    throw new InvalidMoveException("Move is invalid");
                }
            } else {
                throw new InvalidMoveException("Move is invalid");
            }
        }
        // Status update
        setTeamTurn(getTeamTurn() == TeamColor.WHITE ? TeamColor.BLACK:TeamColor.WHITE);
        this.priorMove = new ChessGameMove(pieceMoved,move);

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
        for (ChessPosition piecePosition : getTeamPositions(teamColor)){
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
