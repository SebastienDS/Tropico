package tropico.events;

import tropico.Player;

public class FactionSatisfactionEffect extends AbstractEffect {

	/**
	 * Necessary field to avoid warning while implementing Serializable
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The factionName of the Effect
	 */
	private final String factionName;

	/**
	 * <b>FactionSatisfactionEffect's constructor</b>
	 *
	 * Creates an Effect object, requiring the faction and the value.
	 *
	 * @param faction		The name of the faction
	 * @param value         The value of the effect
	 */
	public FactionSatisfactionEffect(String faction, int value) {
		super(value);
		this.factionName = faction;
	}

	@Override
	public String toString() {
		return super.toString() + "% de satisfaction pour les " + factionName;
	}

	/**
	 * Apply effect to the Player p
	 *
	 * @param p The player to apply the effect
	 */
	@Override
	public void applyEffect(Player p) {
		p.getFactionFromName(factionName).addSatisfaction(super.use());
	}
	
}
