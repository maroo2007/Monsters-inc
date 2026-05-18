package game.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;


public class SceneManager {

    private static final SceneManager INSTANCE = new SceneManager();

    private Stage primaryStage;

    private SceneManager() {}

    public static SceneManager getInstance() {
        return INSTANCE;
    }

    public void init(Stage stage) {
        this.primaryStage = stage;
        primaryStage.setTitle("DoorDash: Scare vs Laugh Touchdown");
        primaryStage.setResizable(false);
    }

    public Stage getStage() {
        return primaryStage;
    }



    public Alert buildAlert(AlertType type) {
        Alert alert = new Alert(type);
        if (primaryStage != null && primaryStage.isShowing()) {
            alert.initOwner(primaryStage);
            alert.initModality(Modality.WINDOW_MODAL);
        } else {
            alert.initModality(Modality.APPLICATION_MODAL);
        }
        return alert;
    }


    public void showMainMenu() {
        loadScene("/game/gui/fxml/MainMenu.fxml", 900, 600);
    }

    public void showRoleSelection() {
        loadScene("/game/gui/fxml/RoleSelection.fxml", 900, 600);
    }

    public void showGameScreen() {
        loadScene("/game/gui/fxml/GameView.fxml", 1200, 750);
    }

    public void showWinnerScreen() {
        loadScene("/game/gui/fxml/Winner.fxml", 900, 600);
    }


    private void loadScene(String fxmlPath, double width, double height) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root, width, height);
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
            primaryStage.show();
        } catch (IOException e) {
            throw new RuntimeException("Cannot load FXML: " + fxmlPath, e);
        }
    }
}
