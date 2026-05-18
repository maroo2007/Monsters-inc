package game.gui;

import game.engine.Game;
import game.engine.Role;
import game.engine.exceptions.InvalidMoveException;
import game.engine.exceptions.OutOfEnergyException;
import game.engine.monsters.Monster;

import java.io.IOException;


public class GameStateManager {

    private static final GameStateManager INSTANCE = new GameStateManager();

    private Game game;
    private int lastRoll;
    private String lastEvent = "";

    private GameStateManager() {}

    public static GameStateManager getInstance() {
        return INSTANCE;
    }


    public void startNewGame(Role playerRole) throws IOException {
        game = new Game(playerRole);
        lastRoll = 0;
        lastEvent = "Game started! You are " + playerRole + ". Good luck!";
    }

    public Game getGame() {
        return game;
    }



    public TurnResult takeTurn() throws InvalidMoveException {
        Monster moving = game.getCurrent();
        int posBefore = moving.getPosition();
        boolean wasFrozen = moving.isFrozen();

        game.playTurn();

        if (wasFrozen) {
            lastEvent = moving.getName() + " was frozen — turn skipped!";
            lastRoll = 0;
            return new TurnResult(0, posBefore, posBefore, true, false);
        }

        int posAfter = moving.getPosition();
        int delta = posAfter - posBefore;
        if (delta < 0) delta += 100;
        lastRoll = delta;
        lastEvent = moving.getName() + " moved " + posBefore + " → " + posAfter;

        return new TurnResult(lastRoll, posBefore, posAfter, false, false);
    }


    public void usePowerup() throws OutOfEnergyException {
        Monster current = game.getCurrent();
        game.usePowerup();
        lastEvent = current.getName() + " activated their powerup!";
    }


    public Monster getPlayer()   { return game.getPlayer(); }
    public Monster getOpponent() { return game.getOpponent(); }
    public Monster getCurrent()  { return game.getCurrent(); }
    public Monster getWinner()   { return game.getWinner(); }
    public int     getLastRoll() { return lastRoll; }
    public String  getLastEvent(){ return lastEvent; }
    public void    setLastEvent(String e) { lastEvent = e; }

    public boolean isPlayerTurn() {
        return game.getCurrent() == game.getPlayer();
    }


    public static class TurnResult {
        public final int roll;
        public final int fromPos;
        public final int toPos;
        public final boolean skipped;
        public final boolean collision;

        TurnResult(int roll, int from, int to, boolean skipped, boolean collision) {
            this.roll = roll;
            this.fromPos = from;
            this.toPos = to;
            this.skipped = skipped;
            this.collision = collision;
        }
    }
}
