package game.engine.cards;

import game.engine.interfaces.CanisterModifier;
import game.engine.monsters.Monster;

public class EnergyStealCard extends Card implements CanisterModifier {
	private final int energy;

	public EnergyStealCard(String name, String description, int rarity, int energy) {
		super(name, description, rarity, true);
		this.energy = energy;
	}

	public int getEnergy() {
		return energy;
	}

	@Override
	public void modifyCanisterEnergy(Monster monster, int canisterValue) {
		monster.alterEnergy(canisterValue);
	}

	@Override
	public void performAction(Monster player, Monster opponent) {
		int stealAmount = Math.min(energy, opponent.getEnergy());
		int before = opponent.getEnergy();
		opponent.alterEnergy(-stealAmount);
		int actualStolen = before - opponent.getEnergy();
		if (actualStolen > 0) {
			player.alterEnergy(stealAmount);
		}
	}
}
