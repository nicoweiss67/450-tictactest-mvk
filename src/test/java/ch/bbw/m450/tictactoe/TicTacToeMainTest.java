package ch.bbw.m450.tictactoe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import ch.bbw.m450.tictactoe.TicTacToePlayer.Stone;
import ch.bbw.m450.tictactoe.players.GreedyPlayer;

import static ch.bbw.m450.tictactoe.BoardHelper.boardOf;
import static ch.bbw.m450.tictactoe.BoardHelper.emptyBoard;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests für die Kernlogik von {@link TicTacToeMain}, dokumentiert nach dem
 * GIVEN_WHEN_THEN-Muster (siehe auch testDescription.md im Projekt-Root).
 * Boards werden über {@link BoardHelper} aufgebaut, gemeinsame Objekte über
 * Fixtures (@BeforeEach).
 */
class TicTacToeMainTest {

    // Fixtures
    private Stone[] board;
    private GreedyPlayer greedyPlayer;

    @BeforeEach
    void setUp() {
        board = emptyBoard();
        greedyPlayer = new GreedyPlayer();
    }

    @Test
    @DisplayName("GIVEN ein komplett leeres Spielfeld WHEN geprüft wird ob CIRCLE oder CROSS gewonnen hat THEN gewinnt keiner")
    void emptyBoardHasNoWinner() {
        // GIVEN: leeres Board aus Fixture

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
        board = boardOf("XXX ... ...");

        // WHEN / THEN
        assertThat(TicTacToeMain.isWin(board, Stone.CROSS)).isTrue();
    }

    @Test
    @DisplayName("GIVEN eine volle Diagonale mit CIRCLE WHEN auf Sieg geprüft wird THEN gewinnt CIRCLE")
    void diagonalWinsForCircle() {
        // GIVEN
        board = boardOf("O.. .O. ..O");

        // WHEN / THEN
        assertThat(TicTacToeMain.isWin(board, Stone.CIRCLE)).isTrue();
    }

    @Test
    @DisplayName("GIVEN zwei identische Spieler-Instanzen WHEN eine Partie gestartet wird THEN wird eine IllegalArgumentException geworfen")
    void playWithSamePlayerInstanceThrows() {
        // GIVEN: greedyPlayer aus Fixture

        // WHEN / THEN
        assertThatThrownBy(() -> TicTacToeMain.play(greedyPlayer, greedyPlayer))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("players must differ");
    }

    @Test
    @DisplayName("GIVEN ein leeres Feld WHEN der GreedyPlayer zieht THEN spielt er auf das erste freie Feld (Index 0)")
    void greedyPlayerPlaysFirstFreeCell() {
        // GIVEN: leeres Board + greedyPlayer aus Fixture

        // WHEN
        int move = greedyPlayer.play(board, Stone.CROSS);

        // THEN
        assertThat(move).isZero();
    }

    // ---------------------------------------------------------------------
    // Parameterized Tests
    // ---------------------------------------------------------------------

    @ParameterizedTest(name = "{0} => {1} gewinnt")
    @CsvSource({
            // Reihen
            "XXX...... , CROSS",
            "...XXX... , CROSS",
            "......XXX , CROSS",
            // Spalten
            "X..X..X.. , CROSS",
            ".X..X..X. , CROSS",
            "..X..X..X , CROSS",
            // Diagonalen
            "X...X...X , CROSS",
            "..X.X.X.. , CROSS",
            // dieselben Linien für CIRCLE (Auswahl)
            "OOO...... , CIRCLE",
            ".O..O..O. , CIRCLE",
            "..O.O.O.. , CIRCLE",
            // Gewinn trotz gegnerischer Steine auf dem Feld
            "XXXOO.... , CROSS",
            "OO.XXXO.. , CROSS"
    })
    @DisplayName("GIVEN ein Board mit drei in einer Linie WHEN isWin geprüft wird THEN gewinnt das Symbol")
    void winningBoards(String layout, Stone winner) {
        // GIVEN
        board = boardOf(layout);

        // WHEN / THEN
        assertThat(TicTacToeMain.isWin(board, winner)).isTrue();
    }

    @ParameterizedTest(name = "{0} => kein Sieg für {1}")
    @CsvSource({
            "......... , CROSS",
            "......... , CIRCLE",
            "XX....... , CROSS",           // nur zwei in einer Reihe
            "XXO...... , CROSS",           // Reihe durch Gegner blockiert
            "X.X.O.O.X , CROSS",           // keine Linie
            "XOXXOOOXX , CROSS",           // volles Board, Unentschieden
            "XOXXOOOXX , CIRCLE",
            "XXX...... , CIRCLE"           // Linie gehört dem anderen Symbol
    })
    @DisplayName("GIVEN ein Board ohne Linie des Symbols WHEN isWin geprüft wird THEN false")
    void nonWinningBoards(String layout, Stone stone) {
        // GIVEN
        board = boardOf(layout);

        // WHEN / THEN
        assertThat(TicTacToeMain.isWin(board, stone)).isFalse();
    }

    @ParameterizedTest(name = "{0} => GreedyPlayer spielt {1}")
    @CsvSource({
            "......... , 0",
            "X........ , 1",
            "XO....... , 2",
            "XOXOX.... , 5",
            "XOXOXOO.. , 7",
            "XOXOXOOX. , 8",
            "X.X...... , 1"     // Lücke wird gefüllt
    })
    @DisplayName("GIVEN ein teilweise belegtes Board WHEN der GreedyPlayer zieht THEN wählt er das erste freie Feld")
    void greedyPlayerChoosesFirstFreeCell(String layout, int expectedMove) {
        // GIVEN
        board = boardOf(layout);

        // WHEN
        int move = greedyPlayer.play(board, Stone.CROSS);

        // THEN
        assertThat(move).isEqualTo(expectedMove);
    }

    @ParameterizedTest(name = "GreedyPlayer als {0} auf vollem Board => Exception")
    @EnumSource(Stone.class)
    @DisplayName("GIVEN ein volles Board WHEN der GreedyPlayer zieht THEN wird eine IllegalStateException geworfen")
    void greedyPlayerOnFullBoardThrows(Stone color) {
        // GIVEN
        board = boardOf("XOXXOOOXX");

        // WHEN / THEN
        assertThatThrownBy(() -> greedyPlayer.play(board, color))
                .isInstanceOf(IllegalStateException.class);
    }

    @ParameterizedTest(name = "{0}.opponent() => {1}")
    @CsvSource({"CROSS, CIRCLE", "CIRCLE, CROSS"})
    @DisplayName("GIVEN ein Stone WHEN opponent() aufgerufen wird THEN kommt der Gegner zurück")
    void opponentIsOtherStone(Stone stone, Stone expected) {
        assertThat(stone.opponent()).isEqualTo(expected);
    }
}
