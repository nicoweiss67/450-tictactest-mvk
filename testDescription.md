Test 1 – Leeres Feld hat keinen Gewinner
GIVEN ein komplett leeres Spielfeld
WHEN geprüft wird, ob CROSS oder CIRCLE gewonnen hat
THEN liefert isWin(...) für beide Farben false

Test 2 – Oberste Reihe gewinnt für CROSS
GIVEN ein Feld, bei dem die oberste Reihe (Index 0, 1, 2) vollständig mit CROSS belegt ist
WHEN isWin(board, CROSS) aufgerufen wird
THEN liefert die Methode true

Test 3 – Diagonale gewinnt für CIRCLE
GIVEN ein Feld, bei dem die Diagonale (Index 0, 4, 8) vollständig mit CIRCLE belegt ist
WHEN isWin(board, CIRCLE) aufgerufen wird
THEN liefert die Methode true

Test 4 – Gleiche Spieler-Instanz wirft Exception
GIVEN ein und dieselbe GreedyPlayer-Instanz als X- und O-Spieler
WHEN eine Partie mit TicTacToeMain.play(player, player) gestartet wird
THEN wird eine IllegalArgumentException mit der Meldung "players must differ" geworfen

Test 5 – GreedyPlayer spielt auf das erste freie Feld
GIVEN ein leeres Spielfeld und ein GreedyPlayer
WHEN greedyPlayer.play(board, CROSS) aufgerufen wird
THEN gibt die Methode Index 0 zurück