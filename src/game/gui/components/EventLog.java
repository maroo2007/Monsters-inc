package game.gui.components;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;


public class EventLog extends VBox {

    private static final int MAX_ENTRIES = 50;

    private final VBox entries = new VBox(3);
    private final ScrollPane scroll;

    public EventLog() {
        setSpacing(0);
        getStyleClass().add("event-log");

        Label header = new Label("Event Log");
        header.getStyleClass().add("log-header");

        entries.getStyleClass().add("log-entries");

        scroll = new ScrollPane(entries);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(160);
        scroll.getStyleClass().add("log-scroll");

        getChildren().addAll(header, scroll);
    }


    public void addEntry(String text) {
        addEntry(text, "log-normal");
    }

    public void addEntry(String text, String styleClass) {
        Label entry = new Label("▶ " + text);
        entry.getStyleClass().addAll("log-entry", styleClass);
        entry.setWrapText(true);

        entries.getChildren().add(entry);

        while (entries.getChildren().size() > MAX_ENTRIES) {
            entries.getChildren().remove(0);
        }

        scroll.applyCss();
        scroll.layout();
        scroll.setVvalue(1.0);
    }

    public void addWarning(String text) { addEntry(text, "log-warning"); }
    public void addSuccess(String text) { addEntry(text, "log-success"); }
    public void addError(String text)   { addEntry(text, "log-error"); }

    public void clear() {
        entries.getChildren().clear();
    }
}
