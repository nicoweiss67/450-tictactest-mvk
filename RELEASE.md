# Release-Prozess TicTacTest

Kurzfassung: Ein Release entsteht durch einen **Git-Tag `vX.Y.Z` auf `main`**. Der Tag startet
den Workflow [`release.yml`](.github/workflows/release.yml), der testet, das JAR baut und einen
GitHub Release mit dem JAR veröffentlicht.

```
Entwicklung → Pull Request → main → CHANGELOG + Version festlegen → Tag vX.Y.Z pushen
   → Workflow: Tag/Changelog prüfen → Tests → JAR bauen → Smoke-Test → GitHub Release + JAR
```

## Wann wird ein Release erstellt?
Wenn das Team bewusst entscheidet, dass der Stand von `main` veröffentlicht werden soll, z.B.
nach einem abgeschlossenen Feature oder einem wichtigen Bugfix. Nicht automatisch.

## Wer bzw. was startet einen Release?
Eine Person, die den Tag `vX.Y.Z` pusht. Der Push des Tags startet den Workflow. Sonst nichts.

## Warum löst nicht jeder Push auf `main` einen Release aus?
Der Workflow reagiert nur auf Tags der Form `vX.Y.Z`. Ein Release ist eine bewusste,
dokumentierte Entscheidung für einen bestimmten Commit. Ein Release pro Push würde ungeprüfte
Zwischenstände veröffentlichen, die Versionsnummern entwerten und das Changelog sinnlos machen.

## Wie wird die Versionsnummer festgelegt?
Semantic Versioning `MAJOR.MINOR.PATCH`, Git-Tag mit Präfix `v` (z.B. `v1.1.0`):

| Erhöhen | Wann | Beispiel |
|---|---|---|
| MAJOR | Inkompatible Änderung (Spielregeln, Start/Bedienung, Java-Mindestversion) | 1.4.2 → 2.0.0 |
| MINOR | Neue Funktion, abwärtskompatibel | 1.0.1 → 1.1.0 |
| PATCH | Nur Fehlerbehebung oder kleine Korrektur ohne neue Funktion | 1.0.0 → 1.0.1 |

Die Version steht **nur im Git-Tag**. Der Workflow übergibt sie als `-PreleaseVersion=X.Y.Z`
an Gradle, daraus entsteht der JAR-Name `tictactest-X.Y.Z.jar`. Lokale Builds heissen
`tictactest-1.0.0-SNAPSHOT.jar`. (Die Tags `devcontainer-v…` gehören zum DevContainer, siehe
[docs/devcontainer.md](docs/devcontainer.md), und starten keinen App-Release.)

## Checkliste vor dem Release
- [ ] Alle vorgesehenen Änderungen sind per Pull Request in `main` gemergt.
- [ ] Die CI auf `main` ist grün (Build, Tests, Mutation Testing).
- [ ] Lokal: `./gradlew clean test jar` läuft erfolgreich.
- [ ] `CHANGELOG.md` hat einen neuen Abschnitt `## X.Y.Z - JJJJ-MM-TT` (Added / Changed / Fixed).
- [ ] Die Versionsnummer ist nach obiger Tabelle festgelegt und noch nicht vergeben (`git tag`).

Der Workflow erzwingt davon: Tag liegt auf `main`, Changelog-Eintrag existiert, Tests sind grün.

## Release erstellen
```bash
git checkout main && git pull
git tag -a v1.1.0 -m "Release v1.1.0"
git push origin v1.1.0
```
Fortschritt: GitHub → Actions → "Release". Danach unter GitHub → Releases.

## Welcher Commit gehört zu einem Release?
Der Commit, auf den der Tag zeigt. Ersichtlich in `git show v1.1.0`, auf GitHub beim Tag und
im Release-Text (Feld "Commit"). Der Workflow baut genau diesen Stand.

## Wie wird das JAR gebaut?
Im Workflow mit `./gradlew jar -PreleaseVersion=X.Y.Z` (JDK 25, Temurin). Das JAR enthält den
`Main-Class`-Eintrag und ist direkt lauffähig. Vor dem Veröffentlichen startet der Workflow das
JAR mit Testeingaben und prüft die Ausgabe (Smoke-Test).

Lokal: `./gradlew clean jar`, Ergebnis in `build/libs/`.

## Wo kann das JAR heruntergeladen werden?
GitHub → Repository → Releases → gewünschte Version → Assets → `tictactest-X.Y.Z.jar`.
Starten: `java -jar tictactest-X.Y.Z.jar` (Java 25 oder neuer).
Das Release testen heisst: JAR dort herunterladen und ohne erneuten Build starten.

## Was enthält das Changelog?
Pro Version: Versionsnummer, Datum, **Added** (neue Funktionen), **Changed** (Änderungen
bestehender Funktionen), **Fixed** (Fehlerbehebungen), wenn möglich mit Issue-Verweis (`#12`).
Es beschreibt Auswirkungen für Benutzer und Entwickler und ist keine Liste von Commits.
Der Abschnitt wird als "Änderungen" in den GitHub Release übernommen.

## Was passiert, wenn beim Release ein Test fehlschlägt?
Der Workflow bricht ab, es entsteht **kein** GitHub Release und kein JAR. Dasselbe gilt, wenn
der Tag nicht auf `main` liegt oder das Changelog die Version nicht enthält.
Vorgehen: Fehler per Pull Request in `main` beheben, den fehlgeschlagenen Tag löschen und
auf dem korrigierten Commit neu setzen:
```bash
git push origin --delete v1.1.0
git tag -d v1.1.0
git checkout main && git pull
git tag -a v1.1.0 -m "Release v1.1.0"
git push origin v1.1.0
```
(Nur zulässig, solange für den Tag kein Release veröffentlicht wurde. Veröffentlichte
Versionen werden nie überschrieben, stattdessen gibt es eine neue PATCH-Version.)
