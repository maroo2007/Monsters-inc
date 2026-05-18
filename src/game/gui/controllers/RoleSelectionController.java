package game.gui.controllers;

import game.engine.Role;
import game.gui.GameStateManager;
import game.gui.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;

public class RoleSelectionController {

    @FXML private Label statusLabel;

    @FXML
    private void onScarerSelected() {
        startGame(Role.SCARER);
    }

    @FXML
    private void onLaugherSelected() {
        startGame(Role.LAUGHER);
    }

    @FXML
    private void onBack() {
        SceneManager.getInstance().showMainMenu();
    }


    private void startGame(Role role) {
        statusLabel.setText("Loading game...");
        try {
            GameStateManager.getInstance().startNewGame(role);
            SceneManager.getInstance().showGameScreen();
        } catch (IOException e) {
            statusLabel.setText("Error: Could not load game data — " + e.getMessage());
        }
    }
}
