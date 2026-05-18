package game.gui.components;

import game.engine.Constants;
import game.engine.monsters.*;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


public class MonsterPanel extends VBox {

    private final Label nameLabel   = new Label();
    private final Label typeLabel   = new Label();
    private final Label roleLabel   = new Label();
    private final Label posLabel    = new Label();
    private final Label energyLabel = new Label();
    private final ProgressBar energyBar = new ProgressBar(0);
    private final HBox statusRow    = new HBox(4);
    private final Label shieldIcon  = new Label("[SHIELD]");
    private final Label frozenIcon  = new Label("[FROZEN]");
    private final Label confusedIcon= new Label("[CONFUSED]");


    public MonsterPanel() {
        this("Monster");
    }

    public MonsterPanel(String panelTitle) {
        setSpacing(6);
        setAlignment(Pos.TOP_LEFT);
        setPrefWidth(220);
        getStyleClass().add("monster-panel");

        Label title = new Label(panelTitle);
        title.getStyleClass().add("panel-title");

        nameLabel.getStyleClass().add("monster-name");
        typeLabel.getStyleClass().add("monster-type");
        roleLabel.getStyleClass().add("monster-role");
        posLabel.getStyleClass().add("monster-pos");
        energyLabel.getStyleClass().add("energy-label");
        energyBar.setPrefWidth(200);
        energyBar.getStyleClass().add("energy-bar");

        shieldIcon.getStyleClass().add("status-icon");
        frozenIcon.getStyleClass().add("status-icon");
        confusedIcon.getStyleClass().add("status-icon");
        statusRow.setAlignment(Pos.CENTER_LEFT);

        getChildren().addAll(title, nameLabel, typeLabel, roleLabel,
                posLabel, energyBar, energyLabel, statusRow);
    }


    public void update(Monster m) {
        if (m == null) return;

        nameLabel.setText(m.getName());
        typeLabel.setText(monsterTypeName(m));
        roleLabel.setText("Role: " + m.getRole());
        posLabel.setText("Cell: " + m.getPosition());

        int energy = m.getEnergy();
        double ratio = Math.min(1.0, (double) energy / Constants.WINNING_ENERGY);
        energyBar.setProgress(ratio);
        energyLabel.setText("Energy: " + energy + " / " + Constants.WINNING_ENERGY);

        energyBar.getStyleClass().removeAll("bar-low", "bar-mid", "bar-high");
        if (ratio < 0.25)      energyBar.getStyleClass().add("bar-low");
        else if (ratio < 0.6)  energyBar.getStyleClass().add("bar-mid");
        else                   energyBar.getStyleClass().add("bar-high");

        statusRow.getChildren().clear();
        if (m.isShielded())   statusRow.getChildren().add(shieldIcon);
        if (m.isFrozen())     statusRow.getChildren().add(frozenIcon);
        if (m.isConfused())   statusRow.getChildren().add(confusedIcon);
    }

    public void setActive(boolean active) {
        getStyleClass().removeAll("panel-active", "panel-inactive");
        getStyleClass().add(active ? "panel-active" : "panel-inactive");
    }


    private String monsterTypeName(Monster m) {
        if (m instanceof Dasher)     return "Type: Dasher";
        if (m instanceof Dynamo)     return "Type: Dynamo";
        if (m instanceof MultiTasker)return "Type: MultiTasker";
        if (m instanceof Schemer)    return "Type: Schemer";
        return "Type: Unknown";
    }
}
