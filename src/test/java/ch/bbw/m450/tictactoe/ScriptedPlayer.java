package ch.bbw.m450.tictactoe;

/**
 * Test-Spieler, der vorgegebene Züge der Reihe nach spielt.
 */
final class ScriptedPlayer implements TicTacToePlayer {

    private final int[] moves;
    private int next;

    ScriptedPlayer(int... moves) {
        this.moves = moves;
    }

    @Override
    public int play(Stone[] board, Stone colorToPlay) {
        return moves[next++];
    }
}
