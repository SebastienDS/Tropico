package tropico;

import tropico.utils.Utils;

import java.io.Serializable;
import java.util.Objects;

public class Faction implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final int SATISFACTION_MIN = 0;
	private static final int SATISFACTION_MAX = 100;

	private final String name;
	private int satisfaction;
	private int supporter;

	public Faction(String name, int satisfaction, int supporter) {
		this.name = name;
		this.satisfaction = satisfaction;
		this.supporter = supporter;
	}

	public Faction() {
		this("Anonymous", 0, 0);
	}

	public String getName() {
		return name;
	}

	public int getSatisfaction() {
		return satisfaction;
	}

	public int getSupporter() {
		return supporter;
	}

	public int getBribeCost() {
		return supporter * 15;
	}

	public boolean hasZeroSatisfaction() {
		return satisfaction == SATISFACTION_MIN;
	}

	/**
	 * add satisfaction to the faction
	 * 
	 * @param value
	 */
	public void addSatisfaction(int value) {
		if (!hasZeroSatisfaction()) {
			satisfaction = Utils.limit(satisfaction + value, SATISFACTION_MIN, SATISFACTION_MAX);
		}
	}

	/**
	 * add supporter to the faction
	 * 
	 * @param count
	 */
	public void addSupporter(int count) {
		supporter += count;
		if (supporter < 0) {
			supporter = 0;
		}
	}

	/**
	 * changes the supporter count according to a percentage
	 * 
	 * @param percentage
	 */
	public void addSupporterPercentage(int percentage) {
		supporter += supporter * percentage / 100;
		if (supporter < 0) {
			supporter = 0;
		}
	}

	public boolean isName(String name) {
		return this.name.equals(name);
	}

	@Override
	public String toString() {
		return name + " : " + satisfaction + "% / " + supporter;
	}

	public void killSupporter() {
		if (supporter == 0) {
			throw new IllegalStateException("La faction est n'a déjà plus aucun partisans.");
		}
		supporter--;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Faction)) {
			return false;
		}
		Faction other = (Faction) obj;
		return Objects.equals(name, other.name);
	}
}
