Title: Fix database sources and add SvgRenderer placeholder

Summary:
- Cleaned and restored corrupted Java sources under `src/main/java/com/example/logisim/db`.
- Converted raw SQL in `Migrations.java` into a Java `SCHEMA` string so sources compile.
- Added a minimal `SvgRenderer` placeholder in `src/main/java/com/example/logisim/util`.

Files changed:
- `src/main/java/com/example/logisim/db/Database.java`
- `src/main/java/com/example/logisim/db/Migrations.java`
- `src/main/java/com/example/logisim/util/SvgRenderer.java`

Build & Test:
- Compiled all sources with JDK (verified locally).
- Unit tests: 3 tests passed, 0 failed (JUnit Platform console run).
- Artifact produced: `dist/logisim-swing-project.jar` (in project `dist/`).

Notes:
- Release ZIP created locally: `release-<timestamp>.zip` in project root (not committed).
- I did not modify the `RAPH/` folder on `master`.

Suggested reviewer checklist:
- Verify DB helper if using H2; otherwise `FileDatabase` is the safe fallback.
- Run the JAR locally and validate UI and Logisim exporter output.
- Merge when satisfied.

To run the JAR locally:
```powershell
cd 'd:\SCD\PROJECT\CIRKIT\logisim-swing-project'
& 'C:\Program Files\Java\jdk-25\bin\java.exe' -jar dist\logisim-swing-project.jar
```
