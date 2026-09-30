# DevContainer: Bau, Versionierung und Freigabe

Der DevContainer (`.devcontainer/Dockerfile`) ist Alpine-basiert mit Java 25, Gradle und
einem User `1000:1000`. Er wird als Image in der GitHub Container Registry veröffentlicht:

`ghcr.io/nicoweiss67/450-tictactest-mvk-devcontainer:<version>`

Er wird an zwei Orten benutzt: lokal über `.devcontainer/devcontainer.json` (VS Code) und
in der CI über `container:` in `.github/workflows/gradle.yml` und `coverage-trend.yml`.

## Versionierungskonzept

Semantic Versioning `vMAJOR.MINOR.PATCH`:

| Änderung | Beispiel | Version |
|---|---|---|
| Inkompatibel (Basis-Image, Java-Hauptversion, User) | Java 25 → 27 | MAJOR |
| Neue Tools/Extensions | `python3` hinzufügen | MINOR |
| Korrekturen, Versions-Updates ohne Wirkung auf Nutzung | Gradle 9.3.0 → 9.3.1 | PATCH |

## Freigabe: nur getaggte Versionen

Ein Image ist nur dann **freigegeben**, wenn ein Git-Tag `vX.Y.Z` darauf zeigt. Es wird
bewusst **nicht** bei jedem Push auf `main` veröffentlicht, sonst wäre jede ungeprüfte
Änderung sofort in CI und lokal im Einsatz.

Zusätzlich prüft der Workflow, dass der Tag auf einem Commit liegt, der in `main` enthalten
ist (also über einen gemergten Pull Request reviewt wurde). Sonst bricht er ab.
CI und `devcontainer.json` zeigen immer auf eine **feste Version** (nie auf `latest`),
damit ein Build reproduzierbar ist und nur freigegebene Images verwendet werden.

## Ablauf eines Releases

1. Änderung am `Dockerfile` per Pull Request nach `main` mergen (Review, CI grün).
2. Release taggen und pushen:
   ```bash
   git checkout main && git pull
   git tag v1.1.0
   git push origin v1.1.0
   ```
3. Der Workflow `.github/workflows/devcontainer-publish.yml` startet:
   1. prüft, dass der Tag auf `main` liegt,
   2. baut das Image und pusht `:v1.1.0` und `:latest` nach ghcr.io,
   3. öffnet automatisch einen Pull Request (Branch `devcontainer/update-v1.1.0`), der die
      Version in `devcontainer.json` und allen CI-Workflows ersetzt.
4. Den PR prüfen (die CI läuft darin bereits im neuen Container) und mergen.
5. Danach nutzen **alle CI-Jobs** die neue Version. **Lokal** baut VS Code beim nächsten
   "Rebuild Container" bzw. Öffnen den neuen Container aus ghcr.io.

## Voraussetzungen (einmalig)

- Repo-Secret `PAT_TOKEN`: Fine-grained Personal Access Token mit *Contents*, *Pull requests*
  und *Workflows* jeweils Read and write. Der eingebaute `GITHUB_TOKEN` darf keine Dateien
  unter `.github/workflows/` ändern. Mit dem PAT starten auf dem Update-PR auch die CI-Checks.
- Das Image trägt das Label `org.opencontainers.image.source`, wodurch das Paket dem Repo
  zugeordnet ist und die CI es mit `GITHUB_TOKEN` pullen darf.
- Branch-Schutz auf `main`: Änderungen laufen über Pull Requests.
