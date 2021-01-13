package tropico.events;

import tropico.Player;

public class OtherEffect extends AbstractEffect {
	public static enum types {
		INDUSTRY, FARMING, TREASURY, FOODUNIT
	};

	private final types type;

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
