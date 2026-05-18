package game.gui.controllers;

import game.engine.monsters.Monster;
import game.gui.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class WinnerController {


    public static Monster pendingWinner;

    @FXML private Label trophyLabel;
    @FXML private Label winnerTitle;
    @FXML private Label winnerName;
    @FXML private Label winnerRole;
    @FXML private Label energyStat;
    @FXML private Label positionStat;

    @FXML
    public void initialize() {
        Monster winner = pendingWinner;
        if (winner == null) return;

        winnerName.setText(winner.getName());
        winnerRole.setText(winner.getRole().toString());
        energyStat.setText("Final Energy: " + winner.getEnergy());
        positionStat.setText("Final Position: " + winner.getPosition());

        boolean isScarer = winner.getRole().toString().equals("SCARER");
        winnerRole.getStyleClass().add(isScarer ? "role-scarer" : "role-laugher");
        trophyLabel.setText(isScarer ? "*** SCARER WINS ***" : "*** LAUGHER WINS ***");
    }

    @FXML
    private void onPlayAgain() {
        pendingWinner = null;
        SceneManager.getInstance().showRoleSelection();
    }

    @FXML
    private void onMainMenu() {
        pendingWinner = null;
        SceneManager.getInstance().showMainMenu();
    }
}
