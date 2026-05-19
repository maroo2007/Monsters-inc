package game.gui.controllers;

import game.gui.SceneManager;
import javafx.application.Platform;
import javafx.fxml.FXML;

public class MainMenuController {

    @FXML
    private void onNewGame() {
        SceneManager.getInstance().showRoleSelection();
    }

    @FXML
    private void onHowToPlay() {
        SceneManager.getInstance().showInfo(
            "How to Play",
            "DoorDash  -  Scare vs Laugh Touchdown",
            "OBJECTIVE\n" +
            "Reach cell 99 with at least 1000 energy to WIN!\n\n" +
            "EACH TURN\n" +
            "  Roll Dice    - your monster moves automatically.\n" +
            "  Use Powerup  - costs 500 energy, activates your monster's special ability.\n" +
            "  Tip: Powerup is a BONUS action. You still roll after using it.\n\n" +
            "SPECIAL CELLS\n" +
            "  [D] Door         - gain or lose energy depending on your role.\n" +
            "  [C] Card         - draw a random card for a surprise effect.\n" +
            "  [>] Conveyor     - slides you forward automatically.\n" +
            "  [S] Sock         - slides you back and costs 100 energy.\n" +
            "  [M] Monster Cell - interact with a stationed monster.\n\n" +
            "STATUS EFFECTS\n" +
            "  [SHIELD]   - blocks the next negative energy effect.\n" +
            "  [FROZEN]   - skip your next turn.\n" +
            "  [CONFUSED] - your role is swapped temporarily.\n\n" +
            "MONSTER TYPES\n" +
            "  Dasher      - moves 2x dice (3x with powerup).\n" +
            "  Dynamo      - doubles all energy changes; powerup freezes opponent.\n" +
            "  MultiTasker - half movement speed; all energy changes get +200 bonus.\n" +
            "  Schemer     - all energy changes get +10 bonus; powerup steals 10 from everyone.\n\n" +
            "CHEAT KEYS  (for testing)\n" +
            "  W  -  teleport to cell 99 with max energy (instant win)\n" +
            "  E  -  add +500 energy to your monster"
        );
    }

    @FXML
    private void onExit() {
        Platform.exit();
    }
}
