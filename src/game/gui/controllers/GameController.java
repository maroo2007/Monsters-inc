package game.gui.controllers;

import game.engine.Constants;
import game.engine.exceptions.InvalidMoveException;
import game.engine.exceptions.OutOfEnergyException;
import game.engine.monsters.Monster;
import game.gui.GameStateManager;
import game.gui.SceneManager;
import game.gui.components.BoardView;
import game.gui.components.EventLog;
import game.gui.components.MonsterPanel;
import javafx.animation.PauseTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;

import java.util.Optional;

public class GameController {

    @FXML private BoardView    boardView;
    @FXML private MonsterPanel playerPanel;
    @FXML private MonsterPanel opponentPanel;
    @FXML private EventLog     eventLog;
    @FXML private Label        turnLabel;
    @FXML private Label        diceLabel;
    @FXML private Label        progressLabel;
    @FXML private Button       btnRoll;
    @FXML private Button       btnPowerup;

    private final GameStateManager gsm = GameStateManager.getInstance();
    private boolean powerupUsedThisTurn = false;


    @FXML
    public void initialize() {
        btnRoll.sceneProperty().addListener(new ChangeListener<Scene>() {
            @Override
            public void changed(ObservableValue<? extends Scene> obs, Scene oldScene, Scene newScene) {
                if (newScene != null) {
                    newScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
                        @Override
                        public void handle(KeyEvent e) {
                            handleCheatKey(e);
                        }
                    });
                }
            }
        });

        refreshAll();
        eventLog.addSuccess(gsm.getLastEvent());
        eventLog.addEntry("Goal: reach cell 99 with at least " + Constants.WINNING_ENERGY + " energy!");
        eventLog.addEntry("Tip: POWERUP is a bonus action - you still roll after using it.");
        eventLog.addEntry("[CHEATS]  W = instant win   |   E = +500 energy");
    }


    private void handleCheatKey(KeyEvent event) {
        if (event.getCode() == KeyCode.W) {
            cheatWin();
        } else if (event.getCode() == KeyCode.E) {
            cheatEnergy();
        }
    }


    private void cheatWin() {
        if (gsm.getGame() == null) return;
        Monster player = gsm.getPlayer();
        player.setPosition(Constants.WINNING_POSITION);
        player.setEnergy(Constants.WINNING_ENERGY);
        refreshAll();
        eventLog.addWarning("[CHEAT W] Teleported to cell 99 with " + player.getEnergy() + " energy!");
        checkWin();
    }


    private void cheatEnergy() {
        if (gsm.getGame() == null) return;
        gsm.getPlayer().alterEnergy(500);
        refreshAll();
        eventLog.addWarning("[CHEAT E] Energy boosted! Now: " + gsm.getPlayer().getEnergy());
    }


    @FXML
    private void onRollDice() {
        if (!gsm.isPlayerTurn()) return;
        setControlsEnabled(false);

        try {
            GameStateManager.TurnResult result = gsm.takeTurn();

            if (result.skipped) {
                diceLabel.setText("FROZEN - turn skipped!");
                eventLog.addWarning(gsm.getPlayer().getName() + " was frozen - turn skipped!");
            } else {
                diceLabel.setText("Rolled: +" + result.roll
                        + "  (" + result.fromPos + " -> " + result.toPos + ")");
                eventLog.addEntry(gsm.getLastEvent());
            }

            refreshAll();
            if (checkWin()) return;
            scheduleOpponentTurn();

        } catch (InvalidMoveException e) {
            eventLog.addError("Blocked! Opponent is in the way. Roll again.");
            setControlsEnabled(true);
        }
    }

    @FXML
    private void onUsePowerup() {
        if (!gsm.isPlayerTurn() || powerupUsedThisTurn) return;

        try {
            gsm.usePowerup();
            powerupUsedThisTurn = true;
            eventLog.addSuccess(gsm.getLastEvent());
            diceLabel.setText("Powerup used! Now roll the dice.");
            refreshAll();
            btnRoll.setDisable(false);

        } catch (OutOfEnergyException e) {
            eventLog.addError("Not enough energy! Need " + Constants.POWERUP_COST
                    + " energy. You have " + gsm.getPlayer().getEnergy() + ".");
        }
    }

    @FXML
    private void onPause() {
        setControlsEnabled(false);

        Alert pause = SceneManager.getInstance().buildAlert(AlertType.CONFIRMATION);
        pause.setTitle("Game Paused");
        pause.setHeaderText("Game Paused");
        pause.setContentText("What would you like to do?");
        pause.getButtonTypes().setAll(new ButtonType("Resume"), new ButtonType("Quit to Menu"));

        Optional<ButtonType> choice = pause.showAndWait();
        if (choice.isPresent() && "Quit to Menu".equals(choice.get().getText())) {
            SceneManager.getInstance().showMainMenu();
        } else {
            setControlsEnabled(gsm.isPlayerTurn());
        }
    }


    private void scheduleOpponentTurn() {
        turnLabel.setText("Opponent is thinking...");
        diceLabel.setText("...");
        setControlsEnabled(false);

        PauseTransition delay = new PauseTransition(Duration.seconds(1.2));
        delay.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                executeOpponentTurn();
            }
        });
        delay.play();
    }

    private void executeOpponentTurn() {
        try {
            GameStateManager.TurnResult result = gsm.takeTurn();

            if (result.skipped) {
                diceLabel.setText("Opponent FROZEN - skipped!");
                eventLog.addWarning(gsm.getOpponent().getName() + " was frozen - skipped!");
            } else {
                diceLabel.setText("Opponent rolled: +" + result.roll
                        + "  (" + result.fromPos + " -> " + result.toPos + ")");
                eventLog.addEntry(gsm.getLastEvent());
            }

        } catch (InvalidMoveException e) {
            gsm.getGame().setCurrent(gsm.getPlayer());
            eventLog.addEntry("Opponent was blocked and could not move.");
        }

        refreshAll();

        if (!checkWin()) {
            powerupUsedThisTurn = false;
            setControlsEnabled(true);
            updateTurnLabel();
            updatePowerupButton();
        }
    }


    private void refreshAll() {
        boardView.refresh(
            gsm.getGame().getBoard(),
            gsm.getPlayer(),
            gsm.getOpponent(),
            gsm.getGame().getAllMonsters()
        );
        playerPanel.update(gsm.getPlayer());
        playerPanel.setActive(gsm.isPlayerTurn());
        opponentPanel.update(gsm.getOpponent());
        opponentPanel.setActive(!gsm.isPlayerTurn());
        updateTurnLabel();
        updatePowerupButton();
        updateProgress();
    }

    private void updateTurnLabel() {
        if (gsm.isPlayerTurn()) {
            turnLabel.setText("YOUR TURN  -  " + gsm.getPlayer().getName());
            turnLabel.getStyleClass().remove("turn-opponent");
            if (!turnLabel.getStyleClass().contains("turn-player")) {
                turnLabel.getStyleClass().add("turn-player");
            }
        } else {
            turnLabel.setText("OPPONENT  -  " + gsm.getOpponent().getName());
            turnLabel.getStyleClass().remove("turn-player");
            if (!turnLabel.getStyleClass().contains("turn-opponent")) {
                turnLabel.getStyleClass().add("turn-opponent");
            }
        }
    }

    private void updatePowerupButton() {
        int energy = gsm.getPlayer().getEnergy();
        boolean canAfford = energy >= Constants.POWERUP_COST;
        boolean disabled  = !canAfford || !gsm.isPlayerTurn() || powerupUsedThisTurn;
        btnPowerup.setDisable(disabled);
        String label = powerupUsedThisTurn
            ? "POWERUP (already used this turn)"
            : "POWERUP  cost:" + Constants.POWERUP_COST + "  [you:" + energy + "]";
        btnPowerup.setText(label);
    }

    private void updateProgress() {
        if (progressLabel == null) return;
        Monster p = gsm.getPlayer();
        progressLabel.setText(
            "Your progress:  Cell " + p.getPosition() + " / 99"
            + "     Energy " + p.getEnergy() + " / " + Constants.WINNING_ENERGY
        );
    }

    private void setControlsEnabled(boolean enabled) {
        btnRoll.setDisable(!enabled);
        if (!enabled) {
            btnPowerup.setDisable(true);
        } else {
            updatePowerupButton();
        }
    }


    private boolean checkWin() {
        Monster winner = gsm.getWinner();
        if (winner == null) return false;

        setControlsEnabled(false);
        WinnerController.pendingWinner = winner;

        boolean playerWon = (winner == gsm.getPlayer());
        String headline = playerWon ? "YOU WIN!" : "YOU LOSE!";
        String body = winner.getName() + " reached cell 99 with "
                + winner.getEnergy() + " energy!\n\nClick OK to see the results.";

        eventLog.addSuccess(headline + "  " + winner.getName()
                + " wins with " + winner.getEnergy() + " energy!");

        Alert alert = SceneManager.getInstance().buildAlert(AlertType.INFORMATION);
        alert.setTitle(playerWon ? "Victory!" : "Defeat...");
        alert.setHeaderText(headline);
        alert.setContentText(body);
        alert.showAndWait();

        SceneManager.getInstance().showWinnerScreen();
        return true;
    }
}
