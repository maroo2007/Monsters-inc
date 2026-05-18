package game.gui.components;

import game.engine.Board;
import game.engine.Constants;
import game.engine.cells.Cell;
import game.engine.monsters.Monster;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;


public class BoardView extends GridPane {

    private final CellView[][] cellViews = new CellView[Constants.BOARD_ROWS][Constants.BOARD_COLS];

    public BoardView() {
        setHgap(2);
        setVgap(2);
        getStyleClass().add("board-grid");
        buildGrid();
    }



    public void refresh(Board board, Monster player, Monster opponent, ArrayList<Monster> stationed) {
        Cell[][] cells = board.getBoardCells();
        int playerPos   = player.getPosition();
        int opponentPos = opponent.getPosition();

        for (int row = 0; row < Constants.BOARD_ROWS; row++) {
            for (int col = 0; col < Constants.BOARD_COLS; col++) {
                int index  = rowColToIndex(row, col);
                Cell cell  = cells[row][col];
                boolean pHere = (index == playerPos);
                boolean oHere = (index == opponentPos);
                cellViews[row][col].update(cell, pHere, oHere);
            }
        }
    }



    private int rowColToIndex(int row, int col) {
        if (row % 2 == 0) {
            return row * Constants.BOARD_COLS + col;
        } else {
            return row * Constants.BOARD_COLS + (Constants.BOARD_COLS - 1 - col);
        }
    }

    private void buildGrid() {
        for (int row = 0; row < Constants.BOARD_ROWS; row++) {
            for (int col = 0; col < Constants.BOARD_COLS; col++) {
                int index = rowColToIndex(row, col);
                CellView cv = new CellView(index);
                cellViews[row][col] = cv;
                add(cv, col, row);
            }
        }
    }
}
