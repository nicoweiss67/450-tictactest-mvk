package ch.bbw.m450.tictactoe;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.bbw.m450.tictactoe.TicTacToePlayer.Stone;
import ch.bbw.m450.tictactoe.players.HumanPlayer;

import static ch.bbw.m450.tictactoe.BoardHelper.boardOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests für den Spielablauf ({@link TicTacToeMain#play}), die Ausgabe
 * ({@link TicTacToeMain#toString}) und den {@link HumanPlayer}.
 * Konsole (System.in/out) wird pro Test in einer Fixture umgeleitet.
 */
class TicTacToePlayTest {

    private InputStream originalIn;
    private PrintStream originalOut;
    private ByteArrayOutputStream out;

    @BeforeEach
    void redirectConsole() {
        originalIn = System.in;
        originalOut = System.out;
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void restoreConsole() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    /**
     * Liefert die Eingabe byteweise. HumanPlayer erzeugt pro Zug einen neuen Scanner;
     * ein gepufferter Stream würde vom ersten Scanner komplett aufgebraucht.
     */
    private void input(String text) {
        var bytes = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
        System.setIn(new InputStream() {
            @Override
            public int read() {
                return bytes.read();
            }

            @Override
            public int read(byte[] b, int off, int len) {
                return len == 0 ? 0 : bytes.read(b, off, 1);
            }

            @Override
            public int available() {
                return 0;
            }
        });
    }

    // --- play() ---

    @Test
    @DisplayName("GIVEN X spielt 0,1,2 und O spielt 3,4 WHEN gespielt wird THEN gewinnt CROSS")
    void crossWinsGame() {
        var x = new ScriptedPlayer(0, 1, 2);
        var o = new ScriptedPlayer(3, 4);

        assertThat(TicTacToeMain.play(x, o)).isEqualTo(Stone.CROSS);
        assertThat(out.toString(StandardCharsets.UTF_8)).contains("the winner is: CROSS");
    }

    @Test
    @DisplayName("GIVEN O bildet die mittlere Reihe WHEN gespielt wird THEN gewinnt CIRCLE")
    void circleWinsGame() {
        var x = new ScriptedPlayer(0, 1, 8);
        var o = new ScriptedPlayer(3, 4, 5);

        assertThat(TicTacToeMain.play(x, o)).isEqualTo(Stone.CIRCLE);
    }

    @Test
    @DisplayName("GIVEN alle Felder werden ohne Dreierreihe belegt WHEN gespielt wird THEN ist es ein Unentschieden (null)")
    void drawGame() {
        // Endstand: XOX / XOO / OXX
        var x = new ScriptedPlayer(0, 2, 3, 7, 8);
        var o = new ScriptedPlayer(1, 4, 5, 6);

        assertThat(TicTacToeMain.play(x, o)).isNull();
        assertThat(out.toString(StandardCharsets.UTF_8)).contains("draw");
    }

    @ParameterizedTest(name = "ungültiger Zug {0} => IllegalStateException")
    @ValueSource(ints = {-1, 9, 100})
    @DisplayName("GIVEN ein Zug ausserhalb des Feldes WHEN gespielt wird THEN wird eine IllegalStateException geworfen")
    void moveOutOfRangeThrows(int move) {
        assertThatThrownBy(() -> TicTacToeMain.play(new ScriptedPlayer(move), new ScriptedPlayer(0)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("cannot play to position " + move);
    }

    @Test
    @DisplayName("GIVEN O spielt auf ein belegtes Feld WHEN gespielt wird THEN wird eine IllegalStateException geworfen")
    void moveOnOccupiedCellThrows() {
        assertThatThrownBy(() -> TicTacToeMain.play(new ScriptedPlayer(4), new ScriptedPlayer(4)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("cannot play to position 4");
    }

    // --- toString() ---

    @Test
    @DisplayName("GIVEN ein teilweise belegtes Board WHEN toString aufgerufen wird THEN erscheinen X, O und die freien Indizes")
    void toStringShowsStonesAndFreeIndexes() {
        String text = TicTacToeMain.toString(boardOf("XO. ... ..."));

        assertThat(text)
                .contains("\033[1mX\033[0m")
                .contains("\033[1mO\033[0m")
                .contains("\033[37m2\033[0m")
                .contains("\033[37m8\033[0m")
                .doesNotContain("\033[37m0\033[0m")
                .hasLineCount(3);
    }

    // --- HumanPlayer ---

    @Test
    @DisplayName("GIVEN die Eingabe '4' WHEN der HumanPlayer zieht THEN liefert er 4")
    void humanPlayerReadsMove() {
        input("4\n");

        int move = new HumanPlayer().play(boardOf("........."), Stone.CROSS);

        assertThat(move).isEqualTo(4);
        assertThat(out.toString(StandardCharsets.UTF_8)).contains("where to to put the next CROSS");
    }

    @Test
    @DisplayName("GIVEN eine nicht-numerische Eingabe WHEN der HumanPlayer zieht THEN wird eine NumberFormatException geworfen")
    void humanPlayerRejectsNonNumericInput() {
        input("abc\n");

        assertThatThrownBy(() -> new HumanPlayer().play(boardOf("........."), Stone.CROSS))
                .isInstanceOf(NumberFormatException.class);
    }

    // --- main() ---

    @Test
    @DisplayName("GIVEN ein Mensch (X) gegen den GreedyPlayer WHEN main läuft THEN gewinnt der Mensch mit 4,2,6")
    void mainPlaysHumanAgainstGreedy() {
        input("4\n2\n6\n");

        TicTacToeMain.main(new String[0]);

        assertThat(out.toString(StandardCharsets.UTF_8)).contains("the winner is: CROSS");
    }
}
