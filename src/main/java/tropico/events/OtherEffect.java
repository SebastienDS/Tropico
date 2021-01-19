package tropico.events;

import tropico.Player;

public class OtherEffect extends AbstractEffect {

	/**
	 * Necessary field to avoid warning while implementing Serializable
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Resources types available
	 */
	public static enum types {
		INDUSTRY, FARMING, TREASURY, FOODUNIT
	};

	/**
	 * The type of the Effect
	 */
	private final types type;

	/**
	 * <b>OtherEffect's constructor</b>
	 *
	 * Creates an Effect object, requiring the types of resources and the value.
	 *
	 * @param type			The type of resource to apply the effect
	 * @param value         The value of the effect
	 */
	public OtherEffect(types type, int value) {
		super(value);
		this.type = type;
	}

	@Override
	public String toString() {
		String text;
		switch (type) {
		case INDUSTRY:
			text = "% d'industrialisation";
			break;
		case FARMING:
			text = "% d'agriculture";
			break;
		case TREASURY:
			text = "$";
			break;
		case FOODUNIT:
			text = " unités de nourriture";
			break;

		default:
			text = "";
			break;
		}

		return super.toString() + text;
	}

	/**
	 * Apply effect to the Player p
	 *
	 * @param p The player to apply the effect
	 */
	@Override
	public void applyEffect(Player p) {
		switch (type) {
		case INDUSTRY:
			p.addIndustry(super.use());
			break;
		case FARMING:
			p.addFarming(super.use());
			break;
		case TREASURY:
			p.addMoney(super.use());
			break;
		case FOODUNIT:
			p.addFood(super.use());
		}

	}

}
