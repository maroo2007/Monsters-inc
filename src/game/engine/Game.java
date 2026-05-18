package game.engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import game.engine.dataloader.DataLoader;
import game.engine.exceptions.InvalidMoveException;
import game.engine.exceptions.OutOfEnergyException;
import game.engine.monsters.*;

public class Game {
	private Board board;
	private ArrayList<Monster> allMonsters;
	private Monster player;
	private Monster opponent;
	private Monster current;

	public Game(Role playerRole) throws IOException {
		this.allMonsters = DataLoader.readMonsters();
		this.player = selectRandomMonsterByRole(playerRole);
		this.opponent = selectRandomMonsterByRole(
			playerRole == Role.SCARER ? Role.LAUGHER : Role.SCARER
		);
		allMonsters.remove(player);
		allMonsters.remove(opponent);
		Board.setStationedMonsters(allMonsters);
		this.allMonsters = DataLoader.readMonsters();
		this.board = new Board(DataLoader.readCards());
		board.initializeBoard(DataLoader.readCells());
		this.current = player;
	}

	public Board getBoard() {
		return board;
	}

	public ArrayList<Monster> getAllMonsters() {
		return allMonsters;
	}

	public Monster getPlayer() {
		return player;
	}

	public Monster getOpponent() {
		return opponent;
	}

	public Monster getCurrent() {
		return current;
	}

	public void setCurrent(Monster current) {
		this.current = current;
	}

	private Monster selectRandomMonsterByRole(Role role) {
		Collections.shuffle(allMonsters);
		return allMonsters.stream()
				.filter(m -> m.getRole() == role)
				.findFirst()
				.orElse(null);
	}

	private Monster getCurrentOpponent() {
		if (current == player) return opponent;
		return player;
	}

	private int rollDice() {
		return new Random().nextInt(6) + 1;
	}

	public void usePowerup() throws OutOfEnergyException {
		if (current.getEnergy() >= Constants.POWERUP_COST) {
			current.alterEnergy(-Constants.POWERUP_COST);
			current.executePowerupEffect(getCurrentOpponent());
		} else {
			throw new OutOfEnergyException();
		}
	}

	public void playTurn() throws InvalidMoveException {
		if (current.isFrozen()) {
			current.setFrozen(false);
		} else {
			int roll = rollDice();
			board.moveMonster(current, roll, getCurrentOpponent());
		}
		switchTurn();
	}

	private void switchTurn() {
		current = getCurrentOpponent();
	}

	private boolean checkWinCondition(Monster monster) {
		return monster.getPosition() == Constants.WINNING_POSITION
				&& monster.getEnergy() >= Constants.WINNING_ENERGY;
	}

	public Monster getWinner() {
		if (checkWinCondition(player)) return player;
		if (checkWinCondition(opponent)) return opponent;
		return null;
	}
}
