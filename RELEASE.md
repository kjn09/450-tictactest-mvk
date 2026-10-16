# Release-Prozess TicTacTest

Dieses Dokument beschreibt, wie aus einem bestimmten Stand des Repositories eine veröffentlichte Version von TicTacTest entsteht. Es ist so geschrieben, dass eine andere Person den Release durchführen kann, ohne den Autor fragen zu müssen.

Kurzfassung: Ein Release entsteht bewusst. Wer einen Git-Tag der Form `vMAJOR.MINOR.PATCH` auf einen Commit von `main` pusht, startet den GitHub-Actions-Workflow `release.yml`. Dieser testet das Projekt, baut das ausführbare JAR und veröffentlicht es als GitHub Release.

## Überblick

```text
Entwicklung
 ↓
Änderungen nach main
 ↓
Tests und Build (CI)
 ↓
CHANGELOG aktualisieren
 ↓
Version festlegen
 ↓
Git-Tag erstellen
 ↓
Tag zu GitHub pushen
 ↓
GitHub Actions startet (release.yml)
 ↓
Tests → Build → JAR erstellen
 ↓
GitHub Release erstellen und JAR anhängen
 ↓
JAR herunterladen und testen
```

## Fragen und Antworten

### Wann wird ein Release erstellt?

Ein Release wird erstellt, wenn eine zusammenhängende Gruppe von Änderungen fertig auf `main` liegt und alle Bedingungen aus dem Abschnitt «Voraussetzungen vor einem Release» erfüllt sind. Die Entscheidung trifft eine Person bewusst. Es gibt keinen Automatismus.

### Wer beziehungsweise was startet einen Release?

Die verantwortliche Person (Entwickler) erstellt einen Release-Tag und pusht ihn. Der Push des Tags startet den Workflow `.github/workflows/release.yml`. Der Workflow läuft nur bei Tags, die mit `v` beginnen.

### Warum löst nicht jeder Push auf `main` einen Release aus?

- Nicht jede Änderung ist ein sinnvoller Veröffentlichungszeitpunkt. Zwischenstände sollen nicht als Version erscheinen.
- Eine Versionsnummer soll etwas aussagen. Würde jeder Push ein Release erzeugen, entstünden viele Versionen ohne klaren Unterschied.
- Der Zeitpunkt, die Versionsnummer und das Changelog sollen vor dem Release geprüft werden.

Ein normaler Push auf `main` startet deshalb nur die Workflows `Run Tests` und `Coverage History`, aber nicht `Release`.

### Wie wird die Versionsnummer festgelegt?

Die Versionsnummer folgt dem Schema `MAJOR.MINOR.PATCH`. Der Git-Tag trägt zusätzlich ein `v` (zum Beispiel `v1.0.1`). Der Tag ist die einzige Quelle der Version: Der Workflow entfernt das `v` und übergibt den Rest als Projektversion an Gradle. Daraus entsteht das JAR `tictactest-1.0.1.jar`. Lokal ohne Tag heisst das JAR `0.0.0-dev`.

| Erhöhen von | Wann | Beispiel für TicTacTest |
|---|---|---|
| MAJOR | Änderung, die bisherige Nutzung bricht | Start des Programms oder Bedienung ändert sich, Spielregeln ändern sich |
| MINOR | Neue Funktion, die bisherige Nutzung nicht bricht | Neuer Spielertyp, neue Spielvariante |
| PATCH | Fehlerbehebung oder kleine Korrektur ohne neue Funktion | Tippfehler in einer Ausgabe, behobener Fehler bei einer Eingabe |

Der erste Release ist `v1.0.0`.

### Wie wird ein Release-Tag erstellt?

```powershell
git checkout main
git pull origin main
git tag -a v1.0.1 -m "Release v1.0.1"
git push origin v1.0.1
```

Der Tag ist annotiert (`-a`), das heisst, er enthält Autor, Datum und eine Nachricht.

### Welcher Commit gehört zu einem Release?

Der Commit, auf den der Tag zeigt. Er lässt sich so anzeigen:

```powershell
git rev-list -n 1 v1.0.1
```

Auf GitHub steht der Commit in den Release Notes und auf der Release-Seite beim Tag. Der Workflow prüft zusätzlich, dass dieser Commit zu `main` gehört. Ein Tag auf einem anderen Branch bricht den Release ab.

### Wie wird das JAR gebaut?

Der Workflow führt nach den Tests diesen Befehl aus:

```bash
./gradlew jar "-Pversion=1.0.1"
```

Das Ergebnis liegt in `build/libs/tictactest-1.0.1.jar`. Im Manifest steht die Main-Class `ch.bbw.m450.tictactoe.TicTacToeMain`, deshalb startet das JAR mit:

```bash
java -jar tictactest-1.0.1.jar
```

Lokal lässt sich das JAR gleich bauen (unter Windows PowerShell muss der Parameter in Anführungszeichen stehen).

### Wo kann das JAR heruntergeladen werden?

Auf GitHub im Repository unter **Releases**, beim gewünschten Release im Abschnitt **Assets**. Das Repository ist privat, für den Download ist ein GitHub-Login mit Zugriff nötig.

### Was enthält das Changelog?

Die Datei `CHANGELOG.md` im Hauptverzeichnis enthält pro Version:

- Versionsnummer und Datum des Releases
- **Added:** neue Funktionen
- **Changed:** Änderungen an bestehenden Funktionen
- **Fixed:** behobene Fehler

Das Changelog beschreibt in verständlicher Sprache, was sich für Benutzer und Entwickler geändert hat. Es ist keine Liste aller Commits. Der Workflow übernimmt den Abschnitt der Version in die Release Notes.

### Was passiert, wenn beim Release ein Test fehlschlägt?

Der Workflow bricht im Schritt `Run tests` ab. Dazu gehört auch die Coverage-Prüfung (mindestens 90 % Branch Coverage). Es entsteht kein GitHub Release und kein JAR. Der Tag bleibt im Repository bestehen.

Vorgehen:

1. Fehler auf `main` beheben (über einen Feature-Branch und Merge).
2. Wurde noch **nichts veröffentlicht**, den Tag löschen und neu setzen:
   ```powershell
   git tag -d v1.0.1
   git push origin :refs/tags/v1.0.1
   ```
   Danach den Tag auf dem korrigierten Commit neu erstellen und pushen.
3. Wurde die Version bereits veröffentlicht, wird **nie** ein bestehender Tag verschoben. Stattdessen entsteht eine neue Version, mindestens mit erhöhtem PATCH.

## Voraussetzungen vor einem Release

Vor dem Tag müssen diese Bedingungen erfüllt sein:

| Bedingung | Wie sie geprüft wird |
|---|---|
| Die Anwendung ist auf dem gewünschten Stand | `git log` auf `main` ansehen, nur gewollte Änderungen sind enthalten |
| Alle vorgesehenen Änderungen sind auf `main` | `git status` ist sauber, `git branch --no-merged main` zeigt keine offenen Release-Branches |
| Das Projekt lässt sich bauen | `./gradlew jar "-Pversion=X.Y.Z"` läuft durch, das JAR startet mit `java -jar` |
| Alle automatisierten Tests sind erfolgreich | `./gradlew test` ist grün, die CI-Workflows auf `main` sind grün |
| Das Changelog ist aktualisiert | `CHANGELOG.md` enthält einen Abschnitt `## X.Y.Z - Datum` |
| Die Versionsnummer ist festgelegt | Nummer gemäss Tabelle oben gewählt und im Changelog eingetragen |

## Schritt für Schritt

1. Alle Änderungen liegen auf `main`, die CI-Workflows sind grün.
2. `CHANGELOG.md` mit neuem Abschnitt `## X.Y.Z - Datum` ergänzen, committen und nach `main` pushen.
3. Prüfen, dass die CI nach diesem Push grün ist.
4. Tag erstellen und pushen:
   ```powershell
   git tag -a vX.Y.Z -m "Release vX.Y.Z"
   git push origin vX.Y.Z
   ```
5. Auf GitHub unter **Actions** den Lauf `Release` abwarten.
6. Auf GitHub unter **Releases** prüfen, dass Tag, Commit, Änderungen und `tictactest-X.Y.Z.jar` vorhanden sind.
7. Das Release testen (siehe unten).

## Release testen

Ein Release gilt erst als erfolgreich, wenn das veröffentlichte Artefakt getestet wurde:

1. Auf GitHub das Release öffnen.
2. Das JAR aus **Assets** herunterladen.
3. Das heruntergeladene JAR starten, ohne es neu zu bauen:
   ```powershell
   java -jar tictactest-X.Y.Z.jar
   ```
4. Eine Partie spielen, zum Beispiel die Eingaben `4`, `2`, `6`. Am Ende erscheint `...and the winner is: CROSS`.

## Beteiligte Dateien

| Datei | Zweck |
|---|---|
| `build.gradle` | Baut das ausführbare JAR, Version kommt aus dem Projektparameter |
| `CHANGELOG.md` | Änderungen pro Version |
| `.github/workflows/release.yml` | Release-Workflow, startet nur bei Tags `v*` |
| `.github/workflows/test.yml` | Tests, Coverage und Mutation Tests bei jedem Push |