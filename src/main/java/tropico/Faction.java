package tropico;

import tropico.utils.Utils;

import java.io.Serializable;

public class Faction implements Serializable {

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

	public Faction(){
		this("Anonymous", 0, 0);
	}

	public String getName() {
		return name;
	}

	public int getSatisfaction() {
		return satisfaction;
	}

	/**
	 * add satisfaction to the faction
	 * 
	 * @param value
	 */
	public void addSatisfaction(int value) {
		if (satisfaction != SATISFACTION_MIN) {
			satisfaction = Utils.limit(satisfaction + value, SATISFACTION_MIN, SATISFACTION_MAX);
		}
	}

	public int getSupporter() {
		return supporter;
	}

	/**
	 * add supporter to the faction
	 * 
	 * @param count
	 */
	public void addSupporter(int count) {
		supporter += count;
	}

	/**
	 * changes the supporter count according to a percentage
	 * 
	 * @param percentage
	 */
	public void addSupporterPercentage(int percentage) {
		supporter += supporter * percentage / 100;
	}

	@Override
	public String toString() {
		return name + " : " + satisfaction + "% / " + supporter;
	}
	
	public boolean isName(String name) {
		return this.name.equals(name);
	}
	
	public void killSupporter() {
		supporter--;
		satisfaction = Utils.limit(satisfaction - 2, SATISFACTION_MIN, SATISFACTION_MAX);
	}
}
