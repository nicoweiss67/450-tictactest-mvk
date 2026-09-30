package ch.bbw.m450.tictactoe;

import ch.bbw.m450.tictactoe.TicTacToePlayer.Stone;

/**
 * Test-Helper zum lesbaren Aufbau von Spielfeldern.
 */
final class BoardHelper {

    private BoardHelper() {
    }

    /**
     * Baut ein Spielfeld aus einem 9-Zeichen-String. 'X' = CROSS, 'O' = CIRCLE,
     * alles andere (z.B. '.') = leer. Whitespace wird ignoriert, so sind auch
     * mehrzeilige Angaben wie "XXX/OO./..." bzw. "XXX OO. ..." möglich.
     */
    static Stone[] boardOf(String layout) {
        String cells = layout.replaceAll("[\\s/|]", "");
        if (cells.length() != TicTacToeMain.BOARD_SIZE) {
            throw new IllegalArgumentException("board needs 9 cells but got: " + layout);
        }
        Stone[] board = new Stone[TicTacToeMain.BOARD_SIZE];
        for (int i = 0; i < cells.length(); i++) {
            board[i] = switch (cells.charAt(i)) {
                case 'X' -> Stone.CROSS;
                case 'O' -> Stone.CIRCLE;
                default -> null;
            };
        }
        return board;
    }

    static Stone[] emptyBoard() {
        return new Stone[TicTacToeMain.BOARD_SIZE];
    }
}
