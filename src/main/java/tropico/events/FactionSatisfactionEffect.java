package tropico.events;

import tropico.Player;

public class FactionSatisfactionEffect extends AbstractEffect {
	private final String factionName;
	
	public FactionSatisfactionEffect(String faction, int value) {
		super(value);
		this.factionName = faction;
	}

	@Override
	public String toString() {
		return super.toString() + "% de satisfaction pour les " + factionName;
	}
	
	@Override
	public void applyEffect(Player p) {
		p.getFactionFromName(factionName).addSatisfaction(super.use());
	}
	
}
