# Testkonzept TicTacTest

| | |
|---|---|
| Projekt | TicTacTest |
| Autor | Kajinth |
| Version | 0.1 (IST) |
| Datum | 2026-10-09 |
| Scope | Aktuelle Testpraxis im Repository (IST-Zustand, nicht SOLL) |

## 1. Einleitung

TicTacTest ist ein Java-Projekt (Build mit Gradle) mit einem TicTacToe-Spiel. Es besteht aus der Klasse `TicTacToeMain` (Spielablauf, Gewinnerkennung, Ausgabe), dem Interface `TicTacToePlayer` (mit dem Enum `Stone`) und den Spielern `GreedyPlayer` und `HumanPlayer`.

Ausgangslage: Das Projekt hatte keine Tests. Dieses Dokument beschreibt, was heute im Repository tatsächlich an Tests, Tooling und Automatisierung vorhanden ist. Geplante, aber noch nicht umgesetzte Dinge stehen ausschliesslich im Abschnitt «Bekannte Lücken und offene Risiken».

Rollen: Entwickler und Tester ist Kajinth (Einzelarbeit). Die Bewertung erfolgt durch Dominik Berner (BBW).

## 2. Testziele

| ID | Testziel | Stand |
|---|---|---|
| TZ-01 | Die Gewinnerkennung `isWin` erkennt für Spieler X alle 8 Gewinnlinien (3 Zeilen, 3 Spalten, 2 Diagonalen). | abgedeckt |
| TZ-02 | Die Gewinnerkennung `isWin` erkennt Gewinnlinien für Spieler O. | teilweise (3 von 8 Linien) |
| TZ-03 | Die Gewinnerkennung meldet keinen Gewinner bei leerem Board, bei Unentschieden und bei einer unvollständigen Partie. | abgedeckt |
| TZ-04 | Die Tests sind lesbar und ohne Wiederholung geschrieben (Helper, Parameterized Tests). | abgedeckt |
| TZ-05 | Die Tests laufen bei jedem Push automatisch in der CI, und der Coverage-Report wird aufbewahrt. | abgedeckt |

## 3. Teststrategie und Teststufen (IST)

| Teststufe | Stand |
|---|---|
| Unit-Tests | vorhanden, automatisiert, White-Box, mit JUnit Jupiter und AssertJ. Getestet wird nur `isWin`. |
| Integrationstests | keine |
| Systemtests | keine automatisierten Tests; manuelle Durchläufe sind nicht dokumentiert |
| Acceptance-Tests | keine |

Begründung: Der Einstieg erfolgt über die reine Logik. `isWin` ist statisch, hat keine Seiteneffekte und lässt sich schnell und stabil testen. Die Tests prüfen die Verifikation (macht der Code, was er soll) und dienen bei jedem Push gleichzeitig als Regressionstests.

### Teststrukturen

- **Helper `boardFrom(String)`:** Baut aus einem lesbaren Layout wie `"XXX OO. ..."` das `Stone[9]`-Board. Index 0 ist oben links, `.` steht für ein leeres Feld.
- **Helper-Assertions `assertWinner` und `assertNoWinner`:** Kapseln die eigentliche Prüfung, damit der Testkörper kurz bleibt.
- **Parameterized Tests:** `@ParameterizedTest` mit `@MethodSource`. Die Boards werden mit `Arguments.of(...)` übergeben, damit JUnit das Array als einen einzigen Parameter behandelt.
- **Fixtures (`@BeforeEach`):** Werden nicht verwendet, weil `isWin` statisch ist und die Tests keinen gemeinsamen Ausgangszustand brauchen.

## 4. Testobjekte und Testabdeckung (IST)

| Testobjekt | Testziele | Stand |
|---|---|---|
| `TicTacToeMain.isWin` | TZ-01 bis TZ-03 | getestet |
| `TicTacToeMain.play` (Spielablauf, Fehlerfälle) | – | nicht getestet |
| `TicTacToeMain.toString` und `main` | – | nicht getestet |
| `TicTacToePlayer.Stone.opponent()` | – | nicht getestet |
| `GreedyPlayer` | – | nicht getestet |
| `HumanPlayer` (Eingabe über StdIn) | – | nicht getestet |

Gemessene Coverage (JaCoCo, 2026-10-09):

| Messwert | Wert |
|---|---|
| Instructions | 45 % (204 von 375 verpasst) |
| Branches | 51 % (38 von 78 verpasst) |
| Package `ch.bbw.m450.tictactoe` | Branches 54 % |
| Package `ch.bbw.m450.tictactoe.players` | Branches 0 % |

## 5. Testrahmen und Erfolgskriterien

- **Rollen:** Kajinth führt alle Tests aus.
- **Zeitpunkt:** Lokal vor einem Commit mit `./gradlew test`, automatisch bei jedem Push auf jeden Branch (GitHub Actions).
- **Bestanden:** Alle Tests sind grün, der Build ist grün.
- **Nicht bestanden:** Mindestens ein Test schlägt fehl. Der Build wird rot, der Report wird trotzdem als Artifact gespeichert.
- **Abbruchbedingung:** Ein Kompilierfehler oder ein fehlschlagender Test beendet den Lauf.
- **Coverage-Kriterium:** Die JaCoCo-Regel «Branch Coverage mindestens 90 %» ist in `build.gradle` definiert (`jacocoTestCoverageVerification`). Sie ist aber nicht in `build` oder `check` eingehängt und würde mit aktuell 51 % fehlschlagen.

## 6. Testumgebung und Testinfrastruktur

- **Sprache und Build:** Java, Gradle (Wrapper, Gradle 9.7.0)
- **Testframeworks:** JUnit Jupiter 6.1.3 (inklusive Parameterized Tests), AssertJ 3.27.7, `junit-platform-launcher` zur Laufzeit
- **Coverage:** JaCoCo-Gradle-Plugin, Reports als HTML und XML unter `build/reports/jacoco/test/`
- **CI:** GitHub Actions, Workflow `.github/workflows/test.yml`, `ubuntu-latest`, Temurin JDK 21
  - Trigger: Push auf jeden Branch, Pull Request auf `main`
  - Schritte: Checkout, JDK einrichten, Gradle einrichten, Build (`./gradlew assemble`), Tests (`./gradlew test`), Upload von `build/reports` als Artifact `build-reports`
- **Lokal:** Windows, VS Code, Gradle Wrapper

## 7. Testfallbeschreibungen (IST)

Testklasse: `src/test/java/ch/bbw/m450/tictactoe/TicTacToeMainTest.java`

Voraussetzung für alle Testfälle: Ein Board als `Stone[9]`, erzeugt mit `boardFrom`. Das Board wird zeilenweise gelesen, Zeilen sind durch Leerzeichen getrennt.

| ID | Ziel | Testmethode | Board | Schritt | Erwartung |
|---|---|---|---|---|---|
| TC-01 | TZ-01 | `detectsWinsForX` | `XXX OO. ...` | `isWin(board, CROSS)` | `true` (Zeile oben) |
| TC-02 | TZ-01 | `detectsWinsForX` | `OO. XXX ...` | `isWin(board, CROSS)` | `true` (Zeile Mitte) |
| TC-03 | TZ-01 | `detectsWinsForX` | `OO. ... XXX` | `isWin(board, CROSS)` | `true` (Zeile unten) |
| TC-04 | TZ-01 | `detectsWinsForX` | `X.. X.. X..` | `isWin(board, CROSS)` | `true` (Spalte links) |
| TC-05 | TZ-01 | `detectsWinsForX` | `.X. .X. .X.` | `isWin(board, CROSS)` | `true` (Spalte Mitte) |
| TC-06 | TZ-01 | `detectsWinsForX` | `..X ..X ..X` | `isWin(board, CROSS)` | `true` (Spalte rechts) |
| TC-07 | TZ-01 | `detectsWinsForX` | `X.. .X. ..X` | `isWin(board, CROSS)` | `true` (Diagonale) |
| TC-08 | TZ-01 | `detectsWinsForX` | `..X .X. X..` | `isWin(board, CROSS)` | `true` (Diagonale) |
| TC-09 | TZ-02 | `detectsWinsForO` | `OOO XX. ...` | `isWin(board, CIRCLE)` | `true` (Zeile oben) |
| TC-10 | TZ-02 | `detectsWinsForO` | `O.. O.. O..` | `isWin(board, CIRCLE)` | `true` (Spalte links) |
| TC-11 | TZ-02 | `detectsWinsForO` | `O.. .O. ..O` | `isWin(board, CIRCLE)` | `true` (Diagonale) |
| TC-12 | TZ-03 | `detectsNoWinner` | `... ... ...` | `isWin` für CROSS und CIRCLE | beide `false` (leeres Board) |
| TC-13 | TZ-03 | `detectsNoWinner` | `XOX OXO OXO` | `isWin` für CROSS und CIRCLE | beide `false` (Unentschieden) |
| TC-14 | TZ-03 | `detectsNoWinner` | `XO. OX. ...` | `isWin` für CROSS und CIRCLE | beide `false` (unvollständig) |

Tatsächliches Verhalten: Alle 14 Testfälle liefen am 2026-10-09 lokal und in der CI grün.

## 8. Testplan und Zuständigkeiten (IST)

- **Zuständig:** Kajinth für alle Tests, Dominik Berner für die Bewertung.
- **Durchführung:** Bei jedem Commit lokal und bei jedem Push in der CI.
- **Änderungsprozess:** Änderungen entstehen auf Feature-Branches und werden direkt nach `main` gemergt. Ein formaler Review per Pull Request findet nicht statt.

## 9. Bekannte Lücken und offene Risiken (IST)

- `play`, `toString`, `main` und beide Player sind nicht getestet. Die Branch Coverage liegt mit 51 % unter dem Zielwert von 90 %.
- Ungültige Eingaben und Fehlerfälle sind nicht getestet, etwa eine Position ausserhalb von 0 bis 8, ein bereits belegtes Feld oder zweimal derselbe Spieler.
- Die Standard-Ein- und Ausgabe wird nicht getestet.
- Für Spieler O sind nur 3 von 8 Gewinnlinien getestet.
- Es gibt keine Auswertung mit Mutation Testing.
- Die 90-%-Regel von JaCoCo wird im Build nicht erzwungen.

## Versionshistorie

| Version | Datum | Änderung |
|---|---|---|
| 0.1 | 2026-10-09 | Erste Fassung, IST-Zustand |
