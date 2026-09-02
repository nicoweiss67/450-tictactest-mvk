package ch.bbw.m450.tictactoe;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ch.bbw.m450.tictactoe.TicTacToePlayer.Stone;
import ch.bbw.m450.tictactoe.players.GreedyPlayer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests für die Kernlogik von {@link TicTacToeMain}, dokumentiert nach dem
 * GIVEN_WHEN_THEN-Muster (siehe auch TESTS.md im Projekt-Root).
 */
class TicTacToeMainTest {

    @Test
    @DisplayName("GIVEN ein leeres Feld WHEN auf Sieg geprüft wird THEN gibt es keinen Gewinner")
    void emptyBoardHasNoWinner() {
        // GIVEN
        Stone[] board = new Stone[TicTacToeMain.BOARD_SIZE];

        // WHEN
        boolean crossWins = TicTacToeMain.isWin(board, Stone.CROSS);
        boolean circleWins = TicTacToeMain.isWin(board, Stone.CIRCLE);

        // THEN
        assertThat(crossWins).isFalse();
        assertThat(circleWins).isFalse();
    }

    @Test
    @DisplayName("GIVEN eine volle oberste Reihe mit CROSS WHEN auf Sieg geprüft wird THEN gewinnt CROSS")
    void topRowWinsForCross() {
        // GIVEN
        Stone[] board = new Stone[TicTacToeMain.BOARD_SIZE];
        board[0] = Stone.CROSS;
        board[1] = Stone.CROSS;
        board[2] = Stone.CROSS;

        // WHEN
        boolean win = TicTacToeMain.isWin(board, Stone.CROSS);

        // THEN
        assertThat(win).isTrue();
    }

    @Test
    @DisplayName("GIVEN eine volle Diagonale mit CIRCLE WHEN auf Sieg geprüft wird THEN gewinnt CIRCLE")
    void diagonalWinsForCircle() {
        // GIVEN
        Stone[] board = new Stone[TicTacToeMain.BOARD_SIZE];
        board[0] = Stone.CIRCLE;
        board[4] = Stone.CIRCLE;
        board[8] = Stone.CIRCLE;

        // WHEN
        boolean win = TicTacToeMain.isWin(board, Stone.CIRCLE);

        // THEN
        assertThat(win).isTrue();
    }

    @Test
    @DisplayName("GIVEN zwei identische Spieler-Instanzen WHEN eine Partie gestartet wird THEN wird eine IllegalArgumentException geworfen")
    void playWithSamePlayerInstanceThrows() {
        // GIVEN
        var player = new GreedyPlayer();

        // WHEN / THEN
        assertThatThrownBy(() -> TicTacToeMain.play(player, player))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("players must differ");
    }

    @Test
    @DisplayName("GIVEN ein leeres Feld WHEN der GreedyPlayer zieht THEN spielt er auf das erste freie Feld (Index 0)")
    void greedyPlayerPlaysFirstFreeCell() {
        // GIVEN
        var greedyPlayer = new GreedyPlayer();
        Stone[] board = new Stone[TicTacToeMain.BOARD_SIZE];

        // WHEN
        int move = greedyPlayer.play(board, Stone.CROSS);

        // THEN
        assertThat(move).isZero();
    }
}