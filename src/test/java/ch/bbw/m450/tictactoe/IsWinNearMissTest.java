package ch.bbw.m450.tictactoe;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import ch.bbw.m450.tictactoe.TicTacToePlayer.Stone;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Mutation-Testing: Jede der 8 Gewinnlinien wird einmal vollständig gesetzt (Sieg) und dann
 * mit je einem Feld des Gegners (Beinahe-Sieg) bzw. komplett vom Gegner besetzt geprüft.
 * So muss jede einzelne Vergleichsbedingung in isWin() relevant sein.
 */
class IsWinNearMissTest {

    private static final int[][] LINES = {
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
            {0, 4, 8}, {2, 4, 6}
    };

    static Stream<Arguments> lines() {
        return Stream.of(LINES).flatMap(l -> Stream.of(Stone.values()).map(s -> Arguments.of(l, s)));
    }

    static Stream<Arguments> nearMisses() {
        return lines().flatMap(a -> {
            int[] line = (int[]) a.get()[0];
            Stone stone = (Stone) a.get()[1];
            return Stream.of(0, 1, 2).map(gap -> Arguments.of(line, stone, gap));
        });
    }

    private static Stone[] fill(int[] line, Stone stone) {
        Stone[] board = BoardHelper.emptyBoard();
        for (int i : line) {
            board[i] = stone;
        }
        return board;
    }

    @ParameterizedTest(name = "Linie {0} komplett mit {1} => Sieg nur für {1}")
    @MethodSource("lines")
    @DisplayName("GIVEN eine volle Linie WHEN isWin geprüft wird THEN gewinnt nur das besetzende Symbol")
    void fullLineWinsOnlyForOwner(int[] line, Stone stone) {
        Stone[] board = fill(line, stone);

        assertThat(TicTacToeMain.isWin(board, stone)).isTrue();
        assertThat(TicTacToeMain.isWin(board, stone.opponent())).isFalse();
    }

    @ParameterizedTest(name = "Linie {0}: Feld {2} gehört dem Gegner von {1} => kein Sieg")
    @MethodSource("nearMisses")
    @DisplayName("GIVEN eine Linie mit einem Gegnerstein WHEN isWin geprüft wird THEN gewinnt niemand")
    void lineWithOneOpponentStoneDoesNotWin(int[] line, Stone stone, int gap) {
        Stone[] board = fill(line, stone);
        board[line[gap]] = stone.opponent();

        assertThat(TicTacToeMain.isWin(board, stone)).isFalse();
        assertThat(TicTacToeMain.isWin(board, stone.opponent())).isFalse();
    }

    @ParameterizedTest(name = "Linie {0}: Feld {2} leer => kein Sieg für {1}")
    @MethodSource("nearMisses")
    @DisplayName("GIVEN eine Linie mit einem leeren Feld WHEN isWin geprüft wird THEN gewinnt niemand")
    void lineWithOneEmptyCellDoesNotWin(int[] line, Stone stone, int gap) {
        Stone[] board = fill(line, stone);
        board[line[gap]] = null;

        assertThat(TicTacToeMain.isWin(board, stone)).isFalse();
    }
}
