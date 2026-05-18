# DoorDash: Scare vs Laugh Touchdown

A two-player Monsters Inc themed board game built with Java 8 and JavaFX 8.

---

## Project Overview

Players race along a 100-cell board (cells 0–99) collecting energy at Door Cells, drawing effect Cards, riding Conveyor Belts, and avoiding Contamination Socks. The first player to reach cell 99 with at least 1000 energy wins. Each player chooses a Monster type with unique passive abilities and a one-time-per-turn Powerup.

---

## Requirements

| Component | Required |
|---|---|
| Compile JDK | Eclipse Adoptium JDK 8 — `C:\Program Files\Eclipse Adoptium\jdk-8.0.492.9-hotspot` |
| Runtime JRE | BellSoft Liberica JRE 8 Full (includes JavaFX 8 native libraries) |
| JavaFX | Bundled in Liberica JRE 8 Full — also provided as `lib/jfxrt.jar` |
| JUnit | `lib/junit-4.13.2.jar` + `lib/hamcrest-core-1.3.jar` |

Liberica JRE 8 Full download: https://bell-sw.com/pages/downloads/#jdk-8

---

## How to Run

### Option 1 — Batch Script (recommended)

1. Install BellSoft Liberica JRE 8 Full.
2. Set `JAVA_HOME` to the Liberica JRE directory, e.g.:
   ```
   set JAVA_HOME=C:\path\to\bellsoft-jre8
   ```
3. Double-click `run_gui.bat` or run it from a terminal:
   ```
   run_gui.bat
   ```

The script automatically copies FXML/CSS resources to `bin/` before launching.

### Option 2 — Eclipse

1. Open Eclipse with Java 8 (JavaSE-1.8) configured.
2. Import existing project: `File > Import > Existing Projects into Workspace`.
3. Select this folder. Eclipse reads `.classpath` and `.project` automatically.
4. Right-click `src/game/gui/Main.java` > `Run As > Java Application`.

### Option 3 — VS Code

1. Install the Extension Pack for Java.
2. Open the folder in VS Code.
3. Set the JDK to Adoptium 8 in `.vscode/settings.json` (already configured).
4. Use the launch configuration `DoorDash GUI (Java 8)` from the Run panel.

---

## Compile Manually

```batch
"C:\Program Files\Eclipse Adoptium\jdk-8.0.492.9-hotspot\bin\javac.exe" ^
  -source 1.8 -target 1.8 -encoding UTF-8 ^
  -cp "src;lib\jfxrt.jar;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" ^
  -d bin ^
  @sources.txt
```

Where `sources.txt` is a newline-separated list of all `.java` files under `src/`.

After compiling, copy resources to bin:
```batch
xcopy /y src\game\gui\fxml\*.fxml bin\game\gui\fxml\
xcopy /y src\game\gui\css\*.css   bin\game\gui\css\
```

---

## Run Tests

```batch
"e:\bellsoft-jre8\jre8u432-full\bin\java.exe" ^
  -cp "bin;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" ^
  org.junit.runner.JUnitCore ^
  game.tests.Milestone1PublicTests game.tests.Milestone2PublicTests
```

Current test results: **428 / 428 passing**.

---

## Gameplay

### Objective

Reach cell 99 with at least 1000 energy.

### Each Turn

1. (Optional) Use your **Powerup** — costs 500 energy, activates your monster's special ability. This is a bonus action; you still roll after.
2. Click **Roll Dice** — your monster moves automatically.

### Special Cells

| Symbol | Cell | Effect |
|---|---|---|
| D | Door | Gain or lose energy depending on your role |
| D* | Activated Door | Already triggered this game |
| [C] | Card | Draw a random card for a surprise effect |
| >> | Conveyor Belt | Slides you forward automatically |
| [S] | Contamination Sock | Slides you back, costs 100 energy |
| [M] | Monster Cell | Interact with a stationed monster |

### Status Effects

| Effect | Description |
|---|---|
| [SHIELD] | Blocks the next negative energy effect |
| [FROZEN] | Skip your next turn |
| [CONFUSED] | Your role is swapped temporarily |

### Monster Types

| Monster | Passive | Powerup |
|---|---|---|
| Dasher | Moves 2× dice value | Moves 3× dice value |
| Dynamo | Doubles all energy changes | Freezes the opponent |
| MultiTasker | Half movement speed, all energy changes +200 | Same bonus doubled |
| Schemer | All energy changes +10 | Steals 10 energy from all other monsters |

---

## Controls

| Key | Action |
|---|---|
| W | Cheat — teleport to cell 99 with 1000 energy (instant win) |
| E | Cheat — add +500 energy to your monster |

---

## Project Structure

```
DoorDashM1/
  src/
    game/engine/          Game logic (Board, Game, Monster types, Cells, Cards)
    game/gui/             JavaFX application (Main, SceneManager, GameStateManager)
    game/gui/controllers/ FXML controllers
    game/gui/components/  Custom JavaFX components (BoardView, MonsterPanel, EventLog)
    game/gui/fxml/        FXML layout files
    game/gui/css/         Stylesheets
    game/tests/           JUnit test suites
  bin/                    Compiled classes + copied FXML/CSS
  lib/
    jfxrt.jar             JavaFX 8 runtime (from Liberica JRE 8 Full)
    junit-4.13.2.jar
    hamcrest-core-1.3.jar
    javafx-sdk-17.0.13/   JavaFX 17 SDK (reference only — not used at runtime)
  .classpath              Eclipse classpath config
  .project                Eclipse project config
  .settings/              Eclipse compiler settings
  .vscode/                VS Code launch and settings configs
  cards.csv               Card definitions
  cells.csv               Cell definitions
  monsters.csv            Monster definitions
  run_gui.bat             Launch script (portable, uses JAVA_HOME)
  run_tests.bat           Test runner script
```

---

## Troubleshooting

### `Content is not allowed in prolog` when loading FXML

**Cause:** FXML files have a UTF-8 BOM (byte order mark: `EF BB BF`) at the start.  
**Fix:** Strip the BOM from all FXML files. Save as UTF-8 **without** BOM.

The BOM makes the XML parser fail because the `<?xml` declaration is not the very first byte.

### `Exception in Application start method` / JavaFX class not found

**Cause:** Running with a JRE that does not include JavaFX (e.g., Temurin/Adoptium OpenJDK 8).  
**Fix:** Use BellSoft Liberica JRE 8 Full, which bundles JavaFX 8 and its native DLLs.

### Alert / Dialog crashes (Pause button, How To Play)

**Cause:** `Alert.showAndWait()` in JavaFX 8 requires `initOwner()` and `initModality()` to be called before showing. Without them, the dialog crashes on some JRE 8 builds.  
**Fix:** All dialogs are created via `SceneManager.buildAlert()` which always sets owner and modality correctly.

### Game compiles but FXML resources not found at runtime

**Cause:** `javac` does not copy non-Java files to `bin/`.  
**Fix:** `run_gui.bat` copies all FXML and CSS from `src/` to `bin/` before launching. If running manually, do this copy step first.

### Compilation fails with encoding errors

**Cause:** Java files with non-ASCII characters compiled without `-encoding UTF-8`.  
**Fix:** Always pass `-encoding UTF-8` to `javac`. The BOM must also be absent from source files.

---

## Fixes Applied

| Issue | Root Cause | Fix |
|---|---|---|
| FXML crash on startup | UTF-8 BOM in all 4 FXML files | Stripped BOM from every FXML file |
| Compiler encoding issues | UTF-8 BOM in all 43 Java source files | Stripped BOM from all Java sources |
| Alert/dialog crash | Missing `initOwner` + `initModality` on JavaFX 8 | `SceneManager.buildAlert()` factory method |
| Powerup breaks game | Powerup wrongly advanced the turn | Powerup is a bonus action; roll still required after |
| Win condition not shown | Missing win dialog trigger | `checkWin()` shows dialog and switches to winner screen |
| Hardcoded absolute paths | `run_gui.bat` referenced `e:\bellsoft-jre8\...` | Uses `%JAVA_HOME%` with fallback to PATH |
| "CSEN 401" text in UI | Label in `MainMenu.fxml` | Label removed |
