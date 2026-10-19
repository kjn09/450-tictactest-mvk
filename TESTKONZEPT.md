# Testkonzept TicTacTest

| | |
|---|---|
| Projekt | TicTacTest |
| Autor | Kajinth |
| Version | 0.2 (IST) |
| Datum | 2026-10-19 |
| Scope | Aktuelle Testpraxis im Repository (IST-Zustand, nicht SOLL) |

## 1. Einleitung

TicTacTest ist ein Java-Projekt (Build mit Gradle) mit einem TicTacToe-Spiel. Es besteht aus der Klasse `TicTacToeMain` (Spielablauf, Gewinnerkennung, Ausgabe), dem Interface `TicTacToePlayer` (mit dem Enum `Stone`) und den Spielern `GreedyPlayer`, `HumanPlayer` und `MinimaxPlayer` (perfekter Spieler). Das Programm wird über `TicTacToeMain.main` gestartet: Ein Mensch (`HumanPlayer`, Eingabe über die Standardeingabe) spielt gegen den `GreedyPlayer`.

Ausgangslage: Das Projekt hatte keine Tests. Dieses Dokument beschreibt, was heute im Repository tatsächlich an Tests, Tooling und Automatisierung vorhanden ist. Geplante, aber noch nicht umgesetzte Dinge stehen ausschliesslich im Abschnitt «Bekannte Lücken und offene Risiken».

Rollen: Entwickler und Tester ist Kajinth (Einzelarbeit). Die Bewertung erfolgt durch Dominik Berner (BBW).

## 2. Testziele

| ID | Testziel | Stand |
|---|---|---|
| TZ-01 | Die Gewinnerkennung `isWin` erkennt für Spieler X alle 8 Gewinnlinien (3 Zeilen, 3 Spalten, 2 Diagonalen). | abgedeckt |
| TZ-02 | Die Gewinnerkennung `isWin` erkennt Gewinnlinien für Spieler O. | teilweise (3 von 8 Linien) |
| TZ-03 | Die Gewinnerkennung meldet keinen Gewinner bei leerem Board, bei Unentschieden und bei einer unvollständigen Partie. | abgedeckt |
| TZ-04 | Der Spielablauf `play` ermittelt Sieg X, Sieg O und Unentschieden korrekt und weist ungültige Züge ab. | abgedeckt |
| TZ-05 | Die Spieler verhalten sich korrekt: `GreedyPlayer` wählt das erste freie Feld, `HumanPlayer` liest Eingaben, `MinimaxPlayer` nutzt Gewinnzüge, blockt und verliert nie. | abgedeckt |
| TZ-06 | Das komplette Spiel läuft von der ersten Eingabe bis zum Resultat (Sieg X, Sieg O, Unentschieden). | abgedeckt (E2E) |
| TZ-07 | Ungültige und böswillige Benutzereingaben führen zu einem definierten, dokumentierten Verhalten. | abgedeckt (Ist-Verhalten dokumentiert, Abweichungen als Funde F-02 bis F-05) |
| TZ-08 | Verhalten, das zu einem Denial of Service führen kann, ist durch Timeouts abgesichert. | teilweise (Eingabe ohne Zeilenende nicht testbar, siehe F-06) |
| TZ-09 | Die Tests sind lesbar und ohne Wiederholung geschrieben (Helper, Parameterized Tests). | abgedeckt |
| TZ-10 | Die Tests laufen bei jedem Push automatisch in der CI. Die Branch Coverage von mindestens 90 % wird erzwungen, Reports werden aufbewahrt. | abgedeckt |
| TZ-11 | Ein Release entsteht nur, wenn alle Tests grün sind. | abgedeckt (Release-Workflow) |

## 3. Teststrategie und Teststufen (IST)

| Teststufe | Stand |
|---|---|
| Unit-Tests | vorhanden, automatisiert, White-Box, mit JUnit Jupiter und AssertJ (Gradle-Task `test`) |
| Integrationstests | vorhanden: `TicTacToeMain.play` wird mit Test-Spielern (`ScriptedPlayer`) und mit `GreedyPlayer` und `MinimaxPlayer` zusammen geprüft |
| Systemtests | vorhanden: End-to-End-Tests über `TicTacToeMain.main` mit JUnit Pioneer (Gradle-Task `e2eTest`). Nach jedem Release wird das veröffentlichte JAR zusätzlich manuell gestartet. |
| Acceptance-Tests | keine |

Begründung: Der Einstieg erfolgte über die reine Logik. `isWin` ist statisch und lässt sich schnell und stabil testen. Danach folgen Spielablauf und Spieler, zuletzt das Gesamtsystem über die Standardeingabe und -ausgabe. Die Tests prüfen die Verifikation (macht der Code, was er soll) und dienen bei jedem Push gleichzeitig als Regressionstests.

Weitere Testarten und -ziele:

| Testart | Umsetzung |
|---|---|
| Regressionstests | alle automatisierten Tests bei jedem Push |
| Smoke-Test | manuelles Starten des veröffentlichten JAR nach jedem Release |
| Performance-/Timeout-Tests | `@Timeout` bei den DoS-Tests (riesige Eingabe, Minimax auf leerem Board) |
| Tests mit böswilliger Eingabe | `InvalidInputE2ETest` und `DenialOfServiceE2ETest` |
| Mutation Testing | PIT (Gradle-Task `pitest`) |
| Approval Tests | Die gesamte Spielausgabe eines Spiels wird mit einer genehmigten Datei verglichen |

### Teststrukturen

- **Helper `TestBoards.boardFrom(String)`:** Baut aus einem lesbaren Layout wie `"XXX OO. ..."` das `Stone[9]`-Board. Index 0 ist oben links, `.` steht für ein leeres Feld. Der Helper liegt in einer eigenen Klasse und wird von mehreren Testklassen verwendet.
- **Helper-Assertions `assertWinner` und `assertNoWinner`:** Kapseln die eigentliche Prüfung, damit der Testkörper kurz bleibt.
- **Parameterized Tests:** `@ParameterizedTest` mit `@MethodSource` und `@CsvSource`. Boards werden mit `Arguments.of(...)` übergeben, damit JUnit das Array als einen einzigen Parameter behandelt.
- **Eigene Test-Spieler statt Mocking:** `ScriptedPlayer` spielt eine feste Liste von Zügen. Es wird kein Mocking-Framework verwendet, weil das Assignment es ausschliesst.
- **JUnit Pioneer (`@StdIo`, `StdOut`):** Ersetzt die Standardeingabe und fängt die Standardausgabe ab. Damit werden `HumanPlayer` und das ganze Spiel getestet.
- **Tag `e2e`:** Markiert die End-to-End-Tests. Die Task `test` schliesst sie aus, die Task `e2eTest` führt nur sie aus.
- **`@Timeout`:** Sichert die DoS-Tests ab. Bei blockierendem Lesen läuft der Timeout in einem eigenen Thread.
- **Approval Tests:** Die Ausgabe wird ohne Farbcodes mit der Datei `*.approved.txt` verglichen. Neue Ausgaben müssen von Hand geprüft und genehmigt werden.
- **Fixtures (`@BeforeEach`):** Werden nicht verwendet, weil die Tests keinen gemeinsamen Ausgangszustand brauchen.

## 4. Testobjekte und Testabdeckung (IST)

| Testobjekt | Testziele | Stand |
|---|---|---|
| `TicTacToeMain.isWin` | TZ-01 bis TZ-03 | getestet |
| `TicTacToeMain.play` (Spielablauf, Fehlerfälle) | TZ-04 | getestet |
| `TicTacToeMain.toString` | TZ-04, TZ-06 | getestet (indirekt über Spielausgabe und Approval Test) |
| `TicTacToeMain.main` | TZ-06 bis TZ-08 | nur von den E2E-Tests aufgerufen |
| `TicTacToePlayer.Stone.opponent()` | TZ-05 | getestet |
| `GreedyPlayer` | TZ-05 | getestet |
| `HumanPlayer` | TZ-05, TZ-07 | getestet |
| `MinimaxPlayer` | TZ-05, TZ-08 | getestet |

Gemessene Coverage (2026-10-19):

| Messwert | JaCoCo 0.8.14 (Task `test`) | PIT 1.22.0 (Task `pitest`) |
|---|---|---|
| Instructions | 97 % (12 von 509 verpasst) | – |
| Branches | 100 % (0 von 104 verpasst) | – |
| Lines | 72 von 75 abgedeckt | 96 % (72 von 75) |
| Methoden | 12 von 14 abgedeckt | – |
| Mutation Coverage | – | 98 % (78 von 80 Mutanten getötet) |
| Test Strength | – | 98 % (78 von 80) |

Aufschlüsselung nach Package:

| Package | JaCoCo Branches | PIT Mutation Coverage |
|---|---|---|
| `ch.bbw.m450.tictactoe` | 100 % (74 von 74) | 96 % (49 von 51) |
| `ch.bbw.m450.tictactoe.players` | 100 % (30 von 30) | 100 % (29 von 29) |

Die 12 verpassten Instruktionen der JaCoCo-Messung betreffen den Standardkonstruktor von `TicTacToeMain` und die Methode `main`. `main` wird nur von den E2E-Tests aufgerufen, und diese fliessen nicht in die JaCoCo-Auswertung ein. PIT führt die E2E-Tests ebenfalls nicht aus (`excludedGroups = ['e2e']`), damit die Mutation-Läufe schnell bleiben.

Zahl der Tests: 41 Tests in der Task `test` (inklusive Approval Test) und 14 Tests in der Task `e2eTest`.

## 5. Testrahmen und Erfolgskriterien

- **Rollen:** Kajinth führt alle Tests aus.
- **Zeitpunkt:** Lokal vor einem Commit mit `./gradlew test` und `./gradlew e2eTest`, automatisch bei jedem Push auf jeden Branch (GitHub Actions). Die Mutation Tests laufen mit `./gradlew pitest`.
- **Bestanden:** Alle Tests sind grün, der Build ist grün.
- **Nicht bestanden:** Mindestens ein Test schlägt fehl oder die Branch Coverage liegt unter 90 %. Der Build wird rot, der Report wird trotzdem als Artifact gespeichert.
- **Abbruchbedingung:** Ein Kompilierfehler, ein fehlschlagender Test oder die verletzte Coverage-Regel beendet den Lauf.
- **Coverage-Kriterium:** Die JaCoCo-Regel «Branch Coverage mindestens 90 %» ist erzwungen. Der Report-Task ist mit der Prüfung (`jacocoTestCoverageVerification`) verbunden, daher schlägt jeder `./gradlew test`-Lauf mit zu tiefer Coverage fehl. Aktuell liegt die Branch Coverage bei 100 %.
- **Release-Kriterium:** Der Release-Workflow führt die Tests und die E2E-Tests aus und erstellt nur bei grünem Ergebnis ein Release.

## 6. Testumgebung und Testinfrastruktur

- **Sprache und Build:** Java, Gradle (Wrapper, Gradle 9.7.0)
- **Testframeworks:** JUnit Jupiter 6.1.3 (inklusive Parameterized Tests), AssertJ 3.27.7, `junit-platform-launcher` zur Laufzeit, JUnit Pioneer (Standardeingabe und -ausgabe), ApprovalTests 31.0.0
- **Coverage und Mutation Testing:** JaCoCo 0.8.14 (Reports als HTML und XML unter `build/reports/jacoco/test/`), PIT 1.22.0 mit dem Gradle-Plugin `info.solidsoft.pitest` (Report unter `build/reports/pitest/`)
- **Gradle-Tasks:** `test` (Unit- und Integrationstests, ohne Tag `e2e`), `e2eTest` (nur Tests mit Tag `e2e`), `jacocoTestReport`, `jacocoTestCoverageVerification`, `pitest`, `jar`
- **CI:** GitHub Actions auf `ubuntu-24.04`, Temurin JDK 21
  - `.github/workflows/test.yml`: Trigger sind Push auf jeden Branch und Pull Request auf `main`. Schritte: Checkout, JDK einrichten, Gradle einrichten, Build (`./gradlew assemble`), Tests (`./gradlew test`), End-to-End-Tests (`./gradlew e2eTest`), Mutation Tests (`./gradlew pitest`), Upload von `build/reports` als Artifact `build-reports`.
  - `.github/workflows/coverage-history.yml`: Bei Push auf `main` wird die Branch Coverage gemessen und der Verlauf auf GitHub Pages veröffentlicht.
  - `.github/workflows/release.yml`: Läuft nur bei Tags `v*` (siehe `RELEASE.md`).
- **Lokal:** Windows, VS Code, Gradle Wrapper

### Entscheid zu weiteren Testwerkzeugen

- **ApprovalTests** wurde als zusätzliches Testframework gewählt. Es passt zur E2E-Aufgabe, weil es die komplette Spielausgabe absichert.
- **jqwik** wurde geprüft und nicht eingesetzt: Ab Version 1.10 enthalten die Release-Notes eine Klausel gegen die Verwendung mit KI-Werkzeugen, und die Version basiert auf JUnit Platform 1.x, während das Projekt JUnit 6 verwendet.

## 7. Testfallbeschreibungen (IST)

### 7.1 Unit- und Integrationstests

Alle Testklassen liegen unter `src/test/java/ch/bbw/m450/tictactoe/`. Die Boards werden zeilenweise gelesen, Zeilen sind durch Leerzeichen getrennt.

| IDs | Ziel | Testklasse und Methode | Prüft | Erwartung |
|---|---|---|---|---|
| TC-01 bis TC-08 | TZ-01 | `TicTacToeMainTest.detectsWinsForX` | je ein Board für alle 8 Gewinnlinien von X | `isWin(board, CROSS)` ist `true` |
| TC-09 bis TC-11 | TZ-02 | `TicTacToeMainTest.detectsWinsForO` | Zeile oben, Spalte links, Diagonale für O | `isWin(board, CIRCLE)` ist `true` |
| TC-12 bis TC-14 | TZ-03 | `TicTacToeMainTest.detectsNoWinner` | leeres Board (`... ... ...`), Unentschieden (`XOX OXO OXO`), unvollständig (`XO. OX. ...`) | `isWin` für beide Spieler `false` |
| TC-15, TC-16 | TZ-05 | `StoneTest.opponentIsTheOtherColor` | `CROSS` und `CIRCLE` | `opponent()` liefert die andere Farbe |
| TC-17 bis TC-19 | TZ-05 | `GreedyPlayerTest` | leeres Board, belegte Felder, volles Board | Feld 0, erstes freies Feld, `IllegalStateException` (`cannot play at all`) |
| TC-20 bis TC-22 | TZ-04 | `TicTacToeMainPlayTest` | Spiele mit festen Zügen (`ScriptedPlayer`) | Sieg X, Sieg O, Unentschieden (`null`) |
| TC-23 bis TC-26 | TZ-04 | `TicTacToeMainPlayTest` | gleicher Spieler zweimal, Zug auf -1 und 9, Zug auf belegtes Feld | `IllegalArgumentException` (`players must differ`), `IllegalStateException` (`cannot play to position ...`) |
| TC-27 bis TC-30 | TZ-05 | `HumanPlayerTest` | Eingabe `4`, Prompt-Text, Eingabe `abc`, mehrere Züge aus demselben Eingabestrom | Rückgabe 4, Prompt für die Farbe, `NumberFormatException`, Züge 4 und 2 |
| TC-31 bis TC-33 | TZ-05 | `MinimaxPlayerTest.takesTheWinningMove` | drei Boards mit Gewinnzug | Spieler wählt den Gewinnzug |
| TC-34, TC-35 | TZ-05 | `MinimaxPlayerTest.blocksTheOpponentsWinningMove` | zwei Boards mit Drohung des Gegners | Spieler blockt das Feld |
| TC-36 bis TC-38 | TZ-05 | `MinimaxPlayerTest` | Board bleibt unverändert, volles Board, zwei Minimax-Spieler | unverändertes Board, `IllegalStateException`, Unentschieden |
| TC-39, TC-40 | TZ-05 | `MinimaxPlayerTest` | Minimax gegen `GreedyPlayer` als X und als O | Minimax gewinnt |
| TC-41 | TZ-06 | `GameOutputApprovalTest` | komplette Ausgabe von Greedy (X) gegen Minimax (O) | Ausgabe entspricht der genehmigten Datei (Sieg O) |

### 7.2 End-to-End-Tests mit erwartetem und tatsächlichem Verhalten

Alle E2E-Tests liegen unter `src/test/java/ch/bbw/m450/tictactoe/e2e/` und rufen `TicTacToeMain.main` mit einer simulierten Standardeingabe auf. Der Mensch spielt X, der `GreedyPlayer` spielt O und wählt immer das erste freie Feld. Die Spalte «Ergebnis» beschreibt, was der Test festhält.

| ID | Ziel | Eingabe | Erwartetes Verhalten (Soll) | Tatsächliches Verhalten (Ist) | Ergebnis |
|---|---|---|---|---|---|
| E2E-01 | TZ-06 | `4`, `2`, `6` | Letzte Zeile `...and the winner is: CROSS` | wie Soll | bestanden |
| E2E-02 | TZ-06 | `3`, `4`, `8` | Letzte Zeile `...and the winner is: CIRCLE` | wie Soll | bestanden |
| E2E-03 | TZ-06 | `1`, `3`, `4`, `6`, `8` | Letzte Zeile `it's a draw!` | wie Soll | bestanden |
| E2E-04 | TZ-07 | `abc` | verständliche Meldung, erneute Eingabe | Abbruch mit `NumberFormatException` | Abweichung F-02 |
| E2E-05 | TZ-07 | leere Zeile | verständliche Meldung, erneute Eingabe | Abbruch mit `NumberFormatException` | Abweichung F-02 |
| E2E-06 | TZ-07 | `4.0` | verständliche Meldung, erneute Eingabe | Abbruch mit `NumberFormatException` | Abweichung F-02 |
| E2E-07 | TZ-07 | ` 4` (führendes Leerzeichen) | Eingabe wird akzeptiert oder verständlich abgelehnt | Abbruch mit `NumberFormatException` | Abweichung F-02 |
| E2E-08 | TZ-07 | `99999999999` | verständliche Meldung, erneute Eingabe | Abbruch mit `NumberFormatException` | Abweichung F-02 |
| E2E-09 | TZ-07 | `9` | Meldung «Feld ausserhalb», erneute Eingabe | Abbruch mit `IllegalStateException` (`cannot play to position 9`) | Abweichung F-03 |
| E2E-10 | TZ-07 | `-1` | Meldung «Feld ausserhalb», erneute Eingabe | Abbruch mit `IllegalStateException` (`cannot play to position -1`) | Abweichung F-03 |
| E2E-11 | TZ-07 | `4`, `4` | Meldung «Feld belegt», erneute Eingabe | Abbruch mit `IllegalStateException` (`cannot play to position 4`) | Abweichung F-03 |
| E2E-12 | TZ-08 | Zeile mit 5 000 000 Ziffern | rasche Ablehnung mit kurzer Meldung (Timeout 10 s) | Ablehnung mit `NumberFormatException` in rund 0.7 s, Meldung enthält die ganze Eingabe | bestanden (Timeout), Abweichung F-04 |
| E2E-13 | TZ-08 | `MinimaxPlayer` auf leerem Board | erster Zug innerhalb von 5 s | rund 20 bis 80 ms (gemessen) | bestanden |
| E2E-14 | TZ-08 | keine Eingabe (leerer Eingabestrom) | Programm endet, wartet nicht endlos (Timeout 5 s) | Abbruch mit `NoSuchElementException`, endet sofort | bestanden, Abweichung F-03 |

Die Tests E2E-04 bis E2E-11 und E2E-14 halten das Ist-Verhalten fest. Sie sind grün, solange sich das Verhalten nicht ändert. Die Abweichung vom Soll steht als Fund in Abschnitt 8.

## 8. Gefundene Fehler und Beobachtungen

| ID | Beschreibung | Gefunden mit | Status |
|---|---|---|---|
| F-01 | `HumanPlayer` erzeugt bei jedem Zug einen neuen `Scanner` auf der Standardeingabe. Bei Eingabe über Pipe oder Datei liest der erste Scanner alle Zeilen in seinen Puffer. Der zweite Zug findet nichts mehr und das Spiel bricht nach dem ersten Zug mit `NoSuchElementException: No line found` ab. | Pipe-Eingabe `"4","2","6"` an `java -jar tictactest-1.0.0.jar` | **behoben** in Version 1.0.1, test-first: roter Test (Commit `6d6e47e`), Fix (Commit `53500a6`) |
| F-02 | Text, Dezimalzahlen, Zahlen mit Leerzeichen, leere Zeilen und zu grosse Zahlen beenden das Spiel mit `NumberFormatException`, statt eine Meldung anzuzeigen. | E2E-04 bis E2E-08 | offen, bewusst nicht behoben: Der Originalcode soll nicht wesentlich geändert werden |
| F-03 | Eine Position ausserhalb von 0 bis 8, ein belegtes Feld oder fehlende Eingabe beenden das Spiel mit einer Exception, statt erneut zu fragen. | E2E-09 bis E2E-11, E2E-14 | offen, wie F-02 |
| F-04 | Die Fehlermeldung bei einer ungültigen Zahl enthält die komplette Eingabe. Bei einer Eingabe von 5 MB entsteht rund 5 MB Ausgabe (Log-Flooding). | E2E-12 | offen |
| F-05 | Beobachtung: Nicht-ASCII-Ziffern (zum Beispiel die arabische Ziffer für 4) sowie `+4` und `04` werden als gültige Zahl akzeptiert. Das ist das Verhalten von `Integer.parseInt`. Die Beobachtung ist nicht automatisiert getestet. Ebenfalls Beobachtung: Der Prompt enthält einen Tippfehler («where to to put»), der Test hält ihn fest. | manuelle Prüfung | offen, kosmetisch |
| F-06 | Risiko: Eine Eingabe ohne Zeilenende und ohne Ende des Stroms füllt den Speicher, weil `Scanner.nextLine` die Zeile unbegrenzt puffert. Das ist nicht automatisiert getestet, weil der Test den Speicher des Test-Prozesses füllen würde. | Überlegung zum Verhalten von `Scanner`, nicht gemessen | offen |

## 9. Testplan und Zuständigkeiten (IST)

- **Zuständig:** Kajinth für alle Tests, Dominik Berner für die Bewertung.
- **Durchführung:** Bei jedem Commit lokal und bei jedem Push in der CI. Vor jedem Release zusätzlich der manuelle Test des veröffentlichten JAR.
- **Änderungsprozess:** Änderungen entstehen auf Feature-Branches und werden direkt nach `main` gemergt. Ein formaler Review per Pull Request findet nicht statt. Fehler werden nach Möglichkeit test-first behoben (zuerst der fehlschlagende Test, dann der Fix).

## 10. Bekannte Lücken und offene Risiken (IST)

- Die offenen Funde F-02 bis F-06 sind nicht behoben. Das Spiel bricht bei ungültigen Eingaben ab, statt zu antworten.
- Die JaCoCo-Messung enthält die E2E-Tests nicht. `main` und der Standardkonstruktor von `TicTacToeMain` gelten deshalb als nicht abgedeckt.
- PIT meldet zwei überlebende Mutanten im Package `ch.bbw.m450.tictactoe`. Sie sind nicht einzeln untersucht (Details im PIT-Report).
- Für Spieler O sind nur 3 von 8 Gewinnlinien getestet.
- Einige Tests prüfen mit `isTrue()` und `isFalse()` statt mit ausdrucksstärkeren Assertions.
- Es gibt keine Acceptance-Tests und keine automatisierten Tests des fertigen JAR. Das JAR wird nur manuell geprüft.

## Versionshistorie

| Version | Datum | Änderung |
|---|---|---|
| 0.1 | 2026-10-09 | Erste Fassung, IST-Zustand |
| 0.2 | 2026-10-19 | Neue Tests (Spielablauf, Spieler, Minimax, Pioneer, Approval, E2E), erzwungene Coverage-Regel, PIT, Release-Prozess, E2E-Testfälle mit Soll und Ist, gefundene Fehler, aktuelle Messwerte |