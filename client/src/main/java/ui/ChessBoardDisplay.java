package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

import static ui.EscapeSequences.*;


public class ChessBoardDisplay {

    private static final int BOARD_SIZE = 8;

    enum PlaceColor {
        WHITE,
        BLACK
    }

    public static void drawChessBoard(chess.ChessBoard board, ChessGame.TeamColor view, Map<Integer, Set<Integer>> highlight) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        PlaceColor startingColor = PlaceColor.WHITE;
        drawLetterRow(out, view);
        for (int i = 0; i < BOARD_SIZE; i++) {
            drawEmptyChessBoardRow(out, startingColor, highlight.get(i));
            drawPieceChessBoardRow(out, startingColor, i, board, view, highlight.get(i));
            drawEmptyChessBoardRow(out, startingColor, highlight.get(i));
            // In
            if (startingColor == PlaceColor.WHITE) {
                startingColor = PlaceColor.BLACK;
            } else {
                startingColor = PlaceColor.WHITE;
            }
        }
        drawLetterRow(out, view);
    }
    public static void drawChessBoard(chess.ChessBoard board, ChessGame.TeamColor view) {
        drawChessBoard(board, view,null);
    }

    private static void drawPieceChessBoardRow(PrintStream out, PlaceColor color, int i, ChessBoard board, ChessGame.TeamColor team, Set<Integer> highlight) {
        setLightGreyWithText(out);
        int boardRow = (team == ChessGame.TeamColor.BLACK) ? (i + 1) : (8 - i);
        out.print(boardRow);
        for (int j = 0; j < BOARD_SIZE; j++) {
            if (highlight.contains(i)){
                switch (color) {
                    case PlaceColor.WHITE -> setGreen(out);
                    case PlaceColor.BLACK -> setDarkGreen(out);
                }
            } else {
                switch (color) {
                    case PlaceColor.WHITE -> setWhite(out);
                    case PlaceColor.BLACK -> setRed(out);
                }
            }
            out.print(" ");
            ChessPosition pos;
            if (team == ChessGame.TeamColor.BLACK){
                pos = new ChessPosition(i + 1, 8 - j);
            } else {
                pos = new ChessPosition(8 - i, j + 1);
            }

            printPiece(out, board.getPiece(pos));
            out.print(" ");
            if (color == PlaceColor.WHITE) {
                color = PlaceColor.BLACK;
            } else {
                color = PlaceColor.WHITE;
            }
        }
        setLightGreyWithText(out);
        out.print(boardRow);
        out.println(RESET_BG_COLOR);
    }

    private static void drawEmptyChessBoardRow(PrintStream out, PlaceColor color, Set<Integer> highlight) {
        setLightGreyWithText(out);
        out.print(" ");
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (highlight.contains(i)){
                switch (color) {
                    case PlaceColor.WHITE -> setGreen(out);
                    case PlaceColor.BLACK -> setDarkGreen(out);
                }
            } else {
                switch (color) {
                    case PlaceColor.WHITE -> setWhite(out);
                    case PlaceColor.BLACK -> setRed(out);
                }
            }

            out.print(" " + EMPTY + " ");
            if (color == PlaceColor.WHITE) {
                color = PlaceColor.BLACK;
            } else {
                color = PlaceColor.WHITE;
            }
        }
        setLightGreyWithText(out);
        out.println(" " + RESET_BG_COLOR);
    }

    private static void drawLetterRow(PrintStream out, ChessGame.TeamColor color) {
        setLightGreyWithText(out);
        for (int c = 0; c < 8; c++) {
            char letter = "abcdefgh".charAt((color == ChessGame.TeamColor.WHITE) ? c : (7 - c));
            out.print(EMPTY + letter + " ");
        }
        out.println("  " + RESET_BG_COLOR);
    }

    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setRed(PrintStream out) {
        out.print(SET_BG_COLOR_RED);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void setDarkGreen(PrintStream out) {
        out.print(SET_BG_COLOR_DARK_GREEN);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void setGreen(PrintStream out) {
        out.print(SET_BG_COLOR_GREEN);
        out.print(SET_TEXT_COLOR_WHITE);
    }


    private static void setLightGreyWithText(PrintStream out) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void printPiece(PrintStream out, ChessPiece player) {
        out.print(SET_TEXT_COLOR_BLACK);
        String icon;
        if (player != null) {
            icon = switch (player.getPieceType()) {
                case KING -> player.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_KING : BLACK_KING;
                case QUEEN -> player.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_QUEEN : BLACK_QUEEN;
                case BISHOP -> player.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_BISHOP : BLACK_BISHOP;
                case KNIGHT -> player.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_KNIGHT : BLACK_KNIGHT;
                case ROOK -> player.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_ROOK : BLACK_ROOK;
                case PAWN -> player.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_PAWN : BLACK_PAWN;
            };
        } else {
            icon = EMPTY;
        }
        out.print(icon);
    }
}
