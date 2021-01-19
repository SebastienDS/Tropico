package tropico.events;

import tropico.Player;

public class SupporterNumberEffect extends AbstractEffect {

	/**
	 * Necessary field to avoid warning while implementing Serializable
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The name of the faction
	 */
	private final String factionName;

	/**
	 * Allows you to add effects in percentages or not
	 */
	private final boolean percentage;

	/**
	 * <b>SupporterNumberEffect's constructor</b>
	 *
	 * Creates an Effect object, requiring the faction, the value and the percentage
	 * boolean.
	 *
	 * @param faction    The name of the faction
	 * @param value      The value of the effect
	 * @param percentage Indicates whether the value is a percentage or not
	 */
	public SupporterNumberEffect(String faction, int value, boolean percentage) {
		super(value);
		this.factionName = faction;
		this.percentage = percentage;
	}

	@Override
	public String toString() {
		String percent = (percentage) ? "% de " : " ";
		return super.toString() + percent + factionName;
	}

	/**
	 * Apply effect to the Player p
	 *
	 * @param p The player to apply the effect
	 */
	@Override
	public void applyEffect(Player p) {
		if (percentage) {
			p.getFactionFromName(factionName).addSupporterPercentage(super.use());
		} else {
			p.getFactionFromName(factionName).addSupporter(super.use());
		}

	}

}
