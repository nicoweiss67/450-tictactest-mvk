# Changelog

Format: [Keep a Changelog](https://keepachangelog.com/de/1.1.0/), Versionierung nach
[Semantic Versioning](https://semver.org/lang/de/). Jede Release-Version braucht hier einen
Abschnitt `## X.Y.Z - JJJJ-MM-TT` (der Release-Workflow bricht sonst ab).

## 1.0.2 - 25.10.2026

### Changed
- Ausgabe bei gewinnen von "...and the winner is:" zu "...and the great winner is:"

## 1.0.1 - 2026-10-18

### Changed
- Ausgabe bei Unentschieden von "draw" auf "Draw" umbennant

## 1.0.0 - 2026-10-17

### Added
- Erste veröffentlichte Version von TicTacTest als ausführbares JAR (`java -jar tictactest-1.0.0.jar`)
- Spiel Mensch gegen Computer (GreedyPlayer) auf der Konsole
- Automatisierte Tests mit JUnit 5 und AssertJ inkl. parametrisierter Tests
- Coverage-Messung mit JaCoCo, Coverage-Verlauf auf GitHub Pages
- Mutation Testing mit PIT (Mutation Score 100 %)
- DevContainer (Alpine, Java 25, Gradle) und CI im Container

### Changed
- CI läuft im eigenen DevContainer-Image aus der GitHub Container Registry

### Fixed
- Eingabe über Pipe oder Datei (`... | java -jar tictactest.jar`) wurde nach dem ersten Zug nicht mehr gelesen
- Gewinnerkennung und ungültige Spielzüge (ausserhalb des Feldes, belegtes Feld) sind durch Tests abgesichert
