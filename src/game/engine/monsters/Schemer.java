package game.engine.monsters;

import game.engine.Board;
import game.engine.Constants;
import game.engine.Role;

public class Schemer extends Monster {

	public Schemer(String name, String description, Role role, int energy) {
		super(name, description, role, energy);
	}

	private int stealEnergyFrom(Monster target) {
		int steal = Math.min(Constants.SCHEMER_STEAL, target.getEnergy());
		target.setShielded(false);
		target.alterEnergy(-steal);
		return steal;
	}

	@Override
	public void executePowerupEffect(Monster opponentMonster) {
		int totalStolen = stealEnergyFrom(opponentMonster);
		for (Monster m : Board.getStationedMonsters()) {
			totalStolen += stealEnergyFrom(m);
		}
		this.setEnergy(this.getEnergy() + totalStolen);
	}

	@Override
	public void setEnergy(int energy) {
		super.setEnergy(energy + Constants.SCHEMER_STEAL);
	}
}
