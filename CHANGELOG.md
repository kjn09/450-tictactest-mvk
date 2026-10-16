# Changelog

## 1.0.1 - 2026-10-16

### Added
- Keine

### Changed
- Keine

### Fixed
- Eingabe über Pipe oder Datei: Das Spiel bricht nach dem ersten Zug nicht mehr mit `NoSuchElementException` ab, weil `HumanPlayer` für alle Züge denselben `Scanner` verwendet

## 1.0.0 - 2026-10-16

### Added
- Erste veröffentlichte Version von TicTacTest als ausführbares JAR (`java -jar tictactest-1.0.0.jar`)
- Spielbares TicTacToe: Mensch gegen computergesteuerten Spieler
- Perfekt spielender `MinimaxPlayer` als zusätzliche Spieler-Implementierung, er verliert nie
- Automatisierte Tests mit JUnit, AssertJ und JUnit Pioneer; die Pipeline erzwingt mindestens 90 % Branch Coverage
- Mutation Testing mit PIT und Coverage-Verlauf auf GitHub Pages

### Changed
- Keine (erste Version)

### Fixed
- Keine (erste Version)