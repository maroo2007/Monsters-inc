package game.engine;

import java.util.ArrayList;
import java.util.Collections;

import game.engine.cards.Card;
import game.engine.cells.*;
import game.engine.exceptions.InvalidMoveException;
import game.engine.monsters.Monster;

public class Board {
	private Cell[][] boardCells;
	private static ArrayList<Monster> stationedMonsters;
	private static ArrayList<Card> originalCards;
	private static ArrayList<Card> loadedCards;
	public static ArrayList<Card> cards;

	public Board(ArrayList<Card> readCards) {
		this.boardCells = new Cell[Constants.BOARD_ROWS][Constants.BOARD_COLS];
		stationedMonsters = new ArrayList<Monster>();
		loadedCards = new ArrayList<Card>(readCards);
		originalCards = readCards;
		cards = new ArrayList<Card>();
		setCardsByRarity();
		reloadCards();
	}

	public Cell[][] getBoardCells() {
		return boardCells;
	}

	public static ArrayList<Monster> getStationedMonsters() {
		return stationedMonsters;
	}

	public static void setStationedMonsters(ArrayList<Monster> stationedMonsters) {
		Board.stationedMonsters = stationedMonsters;
	}

	public static ArrayList<Card> getOriginalCards() {
		return loadedCards;
	}

	public static ArrayList<Card> getCards() {
		return cards;
	}

	public static void setCards(ArrayList<Card> cards) {
		Board.cards = cards;
	}

	private int[] indexToRowCol(int index) {
		int row = index / Constants.BOARD_COLS;
		int col;
		if (row % 2 == 0) {
			col = index % Constants.BOARD_COLS;
		} else {
			col = Constants.BOARD_COLS - 1 - (index % Constants.BOARD_COLS);
		}
		return new int[]{row, col};
	}

	private Cell getCell(int index) {
		int[] rowCol = indexToRowCol(index);
		return boardCells[rowCol[0]][rowCol[1]];
	}

	private void setCell(int index, Cell cell) {
		int[] rowCol = indexToRowCol(index);
		boardCells[rowCol[0]][rowCol[1]] = cell;
	}

	public void initializeBoard(ArrayList<Cell> specialCells) {
		ArrayList<DoorCell> doorCells = new ArrayList<>();
		ArrayList<ConveyorBelt> conveyorBelts = new ArrayList<>();
		ArrayList<ContaminationSock> contaminationSocks = new ArrayList<>();
		for (Cell c : specialCells) {
			if (c instanceof DoorCell) doorCells.add((DoorCell) c);
			else if (c instanceof ConveyorBelt) conveyorBelts.add((ConveyorBelt) c);
			else if (c instanceof ContaminationSock) contaminationSocks.add((ContaminationSock) c);
		}

		for (int i = 0; i < Constants.BOARD_SIZE; i++) {
			setCell(i, new Cell("Rest Cell " + i));
		}

		int doorIndex = 0;
		for (int i = 1; i < Constants.BOARD_SIZE && doorIndex < doorCells.size(); i += 2) {
			setCell(i, doorCells.get(doorIndex++));
		}

		for (int i = 0; i < Constants.CONVEYOR_CELL_INDICES.length && i < conveyorBelts.size(); i++) {
			setCell(Constants.CONVEYOR_CELL_INDICES[i], conveyorBelts.get(i));
		}

		for (int i = 0; i < Constants.SOCK_CELL_INDICES.length && i < contaminationSocks.size(); i++) {
			setCell(Constants.SOCK_CELL_INDICES[i], contaminationSocks.get(i));
		}

		for (int i = 0; i < Constants.CARD_CELL_INDICES.length; i++) {
			setCell(Constants.CARD_CELL_INDICES[i], new CardCell("Card Cell " + i));
		}

		for (int i = 0; i < stationedMonsters.size() && i < Constants.MONSTER_CELL_INDICES.length; i++) {
			int pos = Constants.MONSTER_CELL_INDICES[i];
			stationedMonsters.get(i).setPosition(pos);
			setCell(pos, new MonsterCell(stationedMonsters.get(i).getName(), stationedMonsters.get(i)));
		}
	}

	private void setCardsByRarity() {
		ArrayList<Card> expanded = new ArrayList<>();
		for (Card c : originalCards) {
			for (int i = 0; i < c.getRarity(); i++) {
				expanded.add(c);
			}
		}
		originalCards = expanded;
	}

	public static void reloadCards() {
		cards = new ArrayList<>(originalCards);
		Collections.shuffle(cards);
	}

	public static Card drawCard() {
		if (cards.isEmpty()) {
			reloadCards();
		}
		return cards.remove(0);
	}

	public void moveMonster(Monster currentMonster, int roll, Monster opponentMonster)
			throws InvalidMoveException {

		int oldPosition = currentMonster.getPosition();
		currentMonster.move(roll);

		if (currentMonster.getPosition() == opponentMonster.getPosition()) {
			currentMonster.setPosition(oldPosition);
			throw new InvalidMoveException("Cannot move to occupied cell");
		}

		Cell cell = getCell(currentMonster.getPosition());
		cell.onLand(currentMonster, opponentMonster);

		currentMonster.decrementConfusion();

		updateMonsterPositions(currentMonster, opponentMonster);
	}

	private void updateMonsterPositions(Monster player, Monster opponent) {
		for (int i = 0; i < Constants.BOARD_ROWS; i++) {
			for (int j = 0; j < Constants.BOARD_COLS; j++) {
				if (boardCells[i][j] != null) {
					boardCells[i][j].setMonster(null);
				}
			}
		}
		getCell(player.getPosition()).setMonster(player);
		getCell(opponent.getPosition()).setMonster(opponent);
	}
}
