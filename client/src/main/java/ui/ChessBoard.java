package ui;

import chess.ChessGame;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;


public class ChessBoard {

    private static final int BOARD_SIZE = 8;

    enum PlaceColor{
        WHITE,
        BLACK
    }


    public void drawChessBoard(PrintStream out){
        //var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        PlaceColor startingColor = PlaceColor.WHITE;
        for (int i = 0; i < BOARD_SIZE; i++){
            drawEmptyChessBoardRow(out,startingColor);
            drawPieceChessBoardRow(out,startingColor);
            drawEmptyChessBoardRow(out,startingColor);
            // In
            if (startingColor == PlaceColor.WHITE) {
                startingColor = PlaceColor.BLACK;
            } else {
                startingColor = PlaceColor.WHITE;
            }
        }
    }

    private void drawPieceChessBoardRow(PrintStream out, PlaceColor color) {
        // " " + printPiece() + " ";
        for (int i = 0; i<BOARD_SIZE; i++) {
            switch (color) {
                case PlaceColor.WHITE -> setWhite(out);
                case PlaceColor.BLACK -> setBlack(out);
            }
            out.print(" ");
            printPiece(out,BLACK_PAWN);//TODO make adapt to board
            out.print(" ");
            if (color == PlaceColor.WHITE) {
                color = PlaceColor.BLACK;
            } else {
                color = PlaceColor.WHITE;
            }
        }
        //TODO finish row
    }

    private void drawEmptyChessBoardRow(PrintStream out, PlaceColor color){
        for (int i = 0; i<BOARD_SIZE; i++) {
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
        //TODO finish row
    }

    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
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

    private static void printPiece(PrintStream out, String player) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_BLACK);

        out.print(player);

        setWhite(out);
    }
}
