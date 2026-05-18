package game.gui.components;

import game.engine.Role;
import game.engine.cells.*;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;


public class CellView extends StackPane {

    private static final double SIZE = 58;

    private final int index;
    private final Label indexLabel;
    private final Label monsterLabel;
    private final Label typeLabel;

    public CellView(int index) {
        this.index = index;
        setPrefSize(SIZE, SIZE);
        setMinSize(SIZE, SIZE);
        setMaxSize(SIZE, SIZE);

        indexLabel  = new Label(String.valueOf(index));
        indexLabel.getStyleClass().add("cell-index");

        typeLabel   = new Label("");
        typeLabel.getStyleClass().add("cell-type-icon");

        monsterLabel = new Label("");
        monsterLabel.getStyleClass().add("cell-monster-marker");

        VBox content = new VBox(1, typeLabel, monsterLabel);
        content.setAlignment(Pos.CENTER);

        getChildren().addAll(content, indexLabel);
        StackPane.setAlignment(indexLabel, Pos.TOP_LEFT);

        getStyleClass().add("board-cell");
        applyCellStyle(null);
    }



    public void update(Cell cell, boolean playerHere, boolean opponentHere) {
        applyCellStyle(cell);
        updateMonsterMarker(playerHere, opponentHere);
    }


    private void applyCellStyle(Cell cell) {
        getStyleClass().removeIf(s -> s.startsWith("cell-"));
        getStyleClass().add("board-cell");

        if (cell == null) {
            getStyleClass().add("cell-regular");
            typeLabel.setText("");
            return;
        }

        if (cell instanceof MonsterCell) {
            getStyleClass().add("cell-monster");
            typeLabel.setText("[M]");
        } else if (cell instanceof CardCell) {
            getStyleClass().add("cell-card");
            typeLabel.setText("[C]");
        } else if (cell instanceof ConveyorBelt) {
            getStyleClass().add("cell-conveyor");
            typeLabel.setText(">>");
        } else if (cell instanceof ContaminationSock) {
            getStyleClass().add("cell-sock");
            typeLabel.setText("[S]");
        } else if (cell instanceof DoorCell) {
            DoorCell door = (DoorCell) cell;
            if (door.getRole() == Role.SCARER) {
                getStyleClass().add("cell-door-scarer");
                typeLabel.setText(door.isActivated() ? "D*" : "D");
            } else {
                getStyleClass().add("cell-door-laugher");
                typeLabel.setText(door.isActivated() ? "D*" : "D");
            }
        } else {
            getStyleClass().add("cell-regular");
            typeLabel.setText(index == 0 ? "GO" : index == 99 ? "WIN" : "");
        }
    }

    private void updateMonsterMarker(boolean playerHere, boolean opponentHere) {
        getStyleClass().removeIf(s -> s.equals("cell-player-here") || s.equals("cell-opponent-here"));
        if (playerHere && opponentHere) {
            monsterLabel.setText("YOU+OPP");
            monsterLabel.setStyle("-fx-text-fill: #17202a; -fx-font-weight: bold;");
            getStyleClass().add("cell-player-here");
        } else if (playerHere) {
            monsterLabel.setText("YOU");
            monsterLabel.setStyle("-fx-text-fill: #17202a; -fx-font-weight: bold;");
            getStyleClass().add("cell-player-here");
        } else if (opponentHere) {
            monsterLabel.setText("OPP");
            monsterLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-weight: bold;");
            getStyleClass().add("cell-opponent-here");
        } else {
            monsterLabel.setText("");
            monsterLabel.setStyle("");
        }
    }
}
