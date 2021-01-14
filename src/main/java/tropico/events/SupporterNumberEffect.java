package tropico.events;

import tropico.Player;

public class SupporterNumberEffect extends AbstractEffect {
	private final String factionName;
	private final boolean percentage;

	public SupporterNumberEffect(String faction, int value, boolean percentage) {
		super(value);
		this.factionName = faction;
		this.percentage = percentage;
	}

	@Override
	public String toString() {
		String percent = (percentage) ? "% de " : "";
		return super.toString() + percent + factionName;
	}

	@Override
	public void applyEffect(Player p) {
		if (percentage) {
			p.getFactionFromName(factionName).addSupporterPercentage(super.use());
		} else {
			p.getFactionFromName(factionName).addSupporter(super.use());
		}

	}

}
