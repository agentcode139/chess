package ui;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;

import static ui.EscapeSequences.*;


public class ChessBoard {

    private static final int BOARD_SIZE = 8;

    enum PlaceColor {
        WHITE,
        BLACK
    }

    public void drawChessBoard(PrintStream out, chess.ChessBoard board, ChessGame.TeamColor color) {
        //var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        PlaceColor startingColor = PlaceColor.WHITE;
        //TODO top row
        drawLetterRow(out, color);
        for (int i = 0; i < BOARD_SIZE; i++) {
            drawEmptyChessBoardRow(out, startingColor);
            drawPieceChessBoardRow(out, startingColor, i, board, color);
            drawEmptyChessBoardRow(out, startingColor);
            // In
            if (startingColor == PlaceColor.WHITE) {
                startingColor = PlaceColor.BLACK;
            } else {
                startingColor = PlaceColor.WHITE;
            }
        }
        //TODO bottom row
        drawLetterRow(out, color);
    }

    private void drawPieceChessBoardRow(PrintStream out, PlaceColor color, int i, chess.ChessBoard board, ChessGame.TeamColor team) {
        setLightGreyWithText(out);
        out.print((team == ChessGame.TeamColor.WHITE) ? i : (7 - i));
        for (int j = 0; j < BOARD_SIZE; j++) {
            switch (color) {
                case PlaceColor.WHITE -> setWhite(out);
                case PlaceColor.BLACK -> setBlack(out);
            }
            out.print(" ");
            printPiece(out, board.getPiece(new ChessPosition(i, j)));
            out.print(" ");
            if (color == PlaceColor.WHITE) {
                color = PlaceColor.BLACK;
            } else {
                color = PlaceColor.WHITE;
            }
        }
        setLightGreyWithText(out);
        out.print((team == ChessGame.TeamColor.WHITE) ? i : (7 - i));
        out.println();
    }

    private void drawEmptyChessBoardRow(PrintStream out, PlaceColor color) {
        setLightGreyWithText(out);
        out.print(" ");
        for (int i = 0; i < BOARD_SIZE; i++) {
            switch (color) {
                case PlaceColor.WHITE -> setWhite(out);
                case PlaceColor.BLACK -> setBlack(out);
            }
            out.print(" " + EMPTY + " ");
            if (color == PlaceColor.WHITE) {
                color = PlaceColor.BLACK;
            } else {
                color = PlaceColor.WHITE;
            }
        }
        setLightGreyWithText(out);
        out.print(" ");
        out.println();
    }

    private void drawLetterRow(PrintStream out, ChessGame.TeamColor color) {
        setLightGreyWithText(out);
        out.print(" ");
        for (int c = 0; c < 8; c++) {
            char letter = "abcdefgh".charAt((color == ChessGame.TeamColor.WHITE) ? c : (7 - c));
            out.print(EMPTY + letter + " ");
        }
        out.print(" ");
    }

    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void setLightGreyWithText(PrintStream out) {
        out.print(SET_TEXT_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void setRed(PrintStream out) {
        out.print(SET_BG_COLOR_RED);
        out.print(SET_TEXT_COLOR_RED);
    }

    private static void setGreen(PrintStream out) {
        out.print(SET_BG_COLOR_GREEN);
        out.print(SET_TEXT_COLOR_GREEN);
    }

    private static void printPiece(PrintStream out, ChessPiece player) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_BLACK);

        var icon = switch (player.getPieceType()) {
            case KING -> player.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_KING : BLACK_KING;
            case QUEEN -> player.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_QUEEN : BLACK_QUEEN;
            case BISHOP -> player.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_BISHOP : BLACK_BISHOP;
            case KNIGHT -> player.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_KNIGHT : BLACK_KNIGHT;
            case ROOK -> player.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_ROOK : BLACK_ROOK;
            case PAWN -> player.getTeamColor() == ChessGame.TeamColor.WHITE ? WHITE_PAWN : BLACK_PAWN;
        };
        out.print(player);

        setWhite(out);
    }
}
