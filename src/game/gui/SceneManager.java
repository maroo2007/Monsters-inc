package game.gui;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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

    public void showInfo(String title, String header, String content) {
        Stage dialog = makeDialogStage(title);

        Label headerLabel = new Label(header);
        headerLabel.setStyle("-fx-font-size:15px; -fx-font-weight:bold;");
        headerLabel.setWrapText(true);

        Label contentLabel = new Label(content);
        contentLabel.setWrapText(true);
        contentLabel.setMaxWidth(540);

        Button ok = new Button("OK");
        ok.setDefaultButton(true);
        ok.setMinWidth(80);
        ok.setOnAction(new javafx.event.EventHandler<javafx.event.ActionEvent>() {
            @Override public void handle(javafx.event.ActionEvent e) { dialog.close(); }
        });

        VBox box = new VBox(14, headerLabel, contentLabel, ok);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(24, 32, 24, 32));

        dialog.setScene(new Scene(box));
        dialog.showAndWait();
    }

    public String showChoice(String title, String header, String content,
                              String option1, String option2) {
        final String[] picked = { option1 };
        Stage dialog = makeDialogStage(title);

        Label headerLabel = new Label(header);
        headerLabel.setStyle("-fx-font-size:15px; -fx-font-weight:bold;");

        Label contentLabel = new Label(content);
        contentLabel.setWrapText(true);
        contentLabel.setMaxWidth(360);

        Button btn1 = new Button(option1);
        btn1.setDefaultButton(true);
        btn1.setMinWidth(110);
        btn1.setOnAction(new javafx.event.EventHandler<javafx.event.ActionEvent>() {
            @Override public void handle(javafx.event.ActionEvent e) {
                picked[0] = option1;
                dialog.close();
            }
        });

        Button btn2 = new Button(option2);
        btn2.setMinWidth(110);
        btn2.setOnAction(new javafx.event.EventHandler<javafx.event.ActionEvent>() {
            @Override public void handle(javafx.event.ActionEvent e) {
                picked[0] = option2;
                dialog.close();
            }
        });

        HBox buttons = new HBox(12, btn1, btn2);
        buttons.setAlignment(Pos.CENTER);

        VBox box = new VBox(14, headerLabel, contentLabel, buttons);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(24, 32, 24, 32));

        dialog.setScene(new Scene(box));
        dialog.showAndWait();
        return picked[0];
    }

    private Stage makeDialogStage(String title) {
        Stage dialog = new Stage();
        if (primaryStage != null && primaryStage.isShowing()) {
            dialog.initOwner(primaryStage);
            dialog.initModality(Modality.WINDOW_MODAL);
        } else {
            dialog.initModality(Modality.APPLICATION_MODAL);
        }
        dialog.setTitle(title);
        dialog.setResizable(false);
        return dialog;
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
