Testkonzept – Projekt TicTacToe

1. Einleitung
Dieses Dokument beschreibt den aktuellen IST-Zustand des Testens im Projekt TicTacTest. Es dokumentiert, welche Teststrategie, Teststruktur, Testziele und Testfälle heute im Projekt tatsächlich vorhanden und umgesetzt sind (Ist Stand).
TicTacTest ist eine einfache Java-Implementation des Spiels Tic-Tac-Toe (❌⭕), bestehend aus der Spiellogik, einer Spieler-Schnittstelle sowie zwei Spieler-Implementationen (Mensch- und Computerspieler). Das Projekt wird mit Gradle gebaut und über GitHub Actions automatisiert gebaut und getestet.

2. Testobjekt
Getestet wird das Projekt TicTacTest im Repository nicoweiss67/450-tictactest-mvk. Das Projekt besteht aus folgenden Hauptkomponenten:

TicTacToeMain – Spiellogik: Spielschleife (play), Gewinnerkennung (isWin) und Board-Ausgabe (toString)
TicTacToePlayer – Schnittstelle für Spieler-Implementationen sowie das Enum Stone (CROSS / CIRCLE)
GreedyPlayer – einfache Computerspieler-Implementation, die immer auf das erste freie Feld zieht
HumanPlayer – Spieler-Implementation, die Züge über die Konsole (Scanner/stdin) entgegennimmt

3. Teststrategie (IST-Zustand)
Im Projekt sind aktuell ausschliesslich automatisierte Unit-Tests vorhanden.

Testframeworks: JUnit 5 (junit-jupiter, via junit-bom 5.14.4) und AssertJ (assertj-core 3.27.7), eingebunden über build.gradle
Testmuster: Alle fachlichen Tests folgen dem GIVEN-WHEN-THEN-Muster, sowohl im Code, als auch parallel dokumentiert in testDescription.md
Testausführung: Tests laufen über den Gradle-Task test (useJUnitPlatform())
Testautomatisierung: Jeder Push auf einen beliebigen Branch löst über GitHub Actions automatisch Build und Testlauf aus
Es ist kein separates Test-Framework für Mocking, da wir bis jetzt keine Mock Daten brauchen
Es ist aktuell kein Code-Coverage-Tool eingerichtet

4. Teststruktur
Die Tests liegen, analog zur Produktivstruktur, unter src/test/java/ch/bbw/m450/tictactoe/. Es existieren zwei Testklassen:

Testklasse, Datei,	Anzahl Tests,	Pfad:
TicTacToeMainTest, TicTacToeMainTest.java,	5, src/test/java/ch/bbw/m450/tictactoe/
DummySetupTest, dummyTest.java,	2, src/test/java/ch/bbw/m450/tictactoe/

Die fachlichen Testfälle in TicTacToeMainTest sind zusätzlich in testDescription.md im Projekt-Root nach dem GIVEN-WHEN-THEN-Schema festgehalten

5. Testziele
Aus dem heute vorhandenen Testcode lassen sich folgende, aktuell verfolgte Testziele ableiten:

Sicherstellen, dass die Gewinnerkennung (isWin) für die zentralen Grundfälle korrekt funktioniert: kein Gewinner auf leerem Feld, Sieg durch eine volle Reihe, Sieg durch eine volle Diagonale.
Sicherstellen, dass eine ungültige Spielkonfiguration – zwei identische Spieler-Instanzen – erkannt und mit einer aussagekräftigen Exception abgelehnt wird.
Sicherstellen, dass die Computerspieler-Implementation GreedyPlayer sich deterministisch verhält und auf einem leeren Feld stets das erste freie Feld wählt.

6. Testfälle und Bezug zu den Testzielen
Die folgende Tabelle zeigt alle aktuell im Projekt vorhandenen Testfälle sowie ihren Bezug zu den formulierten Testzielen.

ID,	Testfall,	Geprüft wird,	Bezug zu Testziel
TF-01	emptyBoardHasNoWinner (TicTacToeMainTest),	isWin() liefert auf einem leeren Feld für CROSS und CIRCLE jeweils false.	Ziel 1
TF-02	topRowWinsForCross (TicTacToeMainTest),	isWin() erkennt eine volle oberste Reihe (Index 0,1,2) als Sieg für CROSS.	Ziel 1
TF-03	diagonalWinsForCircle (TicTacToeMainTest),	isWin() erkennt eine volle Diagonale (Index 0,4,8) als Sieg für CIRCLE.	Ziel 1
TF-04	playWithSamePlayerInstanceThrows (TicTacToeMainTest),	TicTacToeMain.play(player, player) wirft eine IllegalArgumentException mit der Meldung "players must differ", wenn beide Spieler dieselbe Instanz sind.	Ziel 2
TF-05	greedyPlayerPlaysFirstFreeCell (TicTacToeMainTest),	GreedyPlayer.play() gibt auf einem leeren Feld immer Index 0 zurück.	Ziel 3
TF-06	junitIstEingerichtet (dummyTest / DummySetupTest),	JUnit 5 ist korrekt eingebunden und ein Test kann erfolgreich laufen.	Ziel 4
TF-07	assertJIstEingerichtet (dummyTest / DummySetupTest),	AssertJ ist korrekt eingebunden und Assertions funktionieren wie erwartet.	Ziel 4

7. Testumgebung
Die Tests werden sowohl lokal (über den Gradle-Wrapper gradlew) als auch automatisiert in der Continuous-Integration-Pipeline ausgeführt. Die Pipeline ist als GitHub-Actions-Workflow (.github/workflows/gradle.yml) definiert und umfasst folgende, heute vorhandene Schritte:

Trigger: bei jedem Push auf jeden Branch (push → branches: '**')
Umgebung: ubuntu-latest, JDK 21 (Temurin-Distribution, via actions/setup-java)
Vorbereitung: Checkout des Codes, gradlew ausführbar machen, Caching der Gradle-Abhängigkeiten
Build: ./gradlew assemble --no-daemon
Test: ./gradlew test --no-daemon
Nachbearbeitung: Die Testergebnisse (**/build/test-results/test/*.xml) werden unabhängig vom Ergebnis als Artefakt test-results hochgeladen