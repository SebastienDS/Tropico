package tropico;

import tropico.utils.Utils;

import java.io.Serializable;
import java.util.Objects;

/**
 * This class contains all the informations about a faction, including :
 * <ul>
 * <li>The faction's name</li>
 * <li>The faction's satisfaction percentage</li>
 * <li>The faction's supporter count</li>
 * </ul>
 * 
 * @author Corentin OGER & Sébastien DOS SANTOS
 *
 */
public class Faction implements Serializable {

	/**
	 * Necessary field to avoid warning while implementing Serializable
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The minimum satisfaction possible
	 */
	private static final int SATISFACTION_MIN = 0;

	/**
	 * The maximum satisfaction possible
	 */
	private static final int SATISFACTION_MAX = 100;

	/**
	 * The name of the faction
	 */
	private final String name;

	/**
	 * Satisfaction is a int which represents a percentage
	 */
	private int satisfaction;

	/**
	 * The faction's supporter count
	 */
	private int supporter;

	/**
	 * <b>Faction's constructor</b>
	 * 
	 * Creates a faction object, requiring the faction's name, base satisfaction and
	 * base supporter count.
	 * 
	 * @param name         The name of the faction, must be non null.
	 * @param satisfaction The starting satisfaction, has to be between
	 *                     SATISFACTION_MIN and SATISFACTION_MAX.
	 * @param supporter    The starting number of supporters, has to be positive.
	 */
	public Faction(String name, int satisfaction, int supporter) {
		if (satisfaction < SATISFACTION_MIN || satisfaction > SATISFACTION_MAX) {
			throw new IllegalArgumentException("Satisfaction must be greater than " + SATISFACTION_MIN
					+ " and smaller than " + SATISFACTION_MAX + ".");
		}
		if (supporter < 0) {
			throw new IllegalArgumentException("Supporter must be greater than 0.");
		}
		
		this.name = Objects.requireNonNull(name);
		this.satisfaction = satisfaction;
		this.supporter = supporter;
	}

	/**
	 * Getter for the field name.
	 * 
	 * @return The faction's name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Getter for the field satisfaction.
	 * 
	 * @return The faction's satisfaction, an int between SATISFACTION_MIN and
	 *         SATISFACTION_MAX.
	 */
	public int getSatisfaction() {
		return satisfaction;
	}

	/**
	 * Getter for the field supporter.
	 * 
	 * @return The faction's supporter count, an int superior or equal to 0.
	 */
	public int getSupporter() {
		return supporter;
	}

	/**
	 * This method returns the cost to bribe this faction.
	 * 
	 * @return An int equal to the supporter value multplied by 15.
	 */
	public int getBribeCost() {
		return supporter * 15;
	}

	/**
	 * Checks if the satisfaction field is equal to 0, in which case it will never
	 * be able to change again.
	 * 
	 * @return Returns true if satisfaction is equal to 0, false otherwise.
	 */
	public boolean hasZeroSatisfaction() {
		return satisfaction == 0;
	}

	/**
	 * Checks if the name in parameter is equal to the field name.
	 * 
	 * @param name The name that you want to know if it's the faction name.
	 * @return Returns true if it's the right name, false otherwise.
	 */
	public boolean isName(String name) {
		return this.name.equals(name);
	}

	/**
	 * Adds the int value to the satisfaction, but limits it. Only works if the
	 * satisfaction isn't equal to 0.
	 * 
	 * @param value The int that will be added to the satisfaction.
	 */
	public void addSatisfaction(int value) {
		if (!hasZeroSatisfaction()) {
			satisfaction = Utils.limit(satisfaction + value, SATISFACTION_MIN, SATISFACTION_MAX);
		}
	}

	/**
	 * Adds the int value to the supporter field. Sets it to 0 if supporter + count
	 * is inferior to it.
	 * 
	 * @param count The int that will be added to supporter.
	 */
	public void addSupporter(int count) {
		supporter += count;
		if (supporter < 0) {
			supporter = 0;
		}
	}

	/**
	 * Changes the supporter count according to a percentage. Sets it to 0 if
	 * supporter + count is inferior to it.
	 * 
	 * @param percentage the percentage which will be added to supporter.
	 */
	public void addSupporterPercentage(int percentage) {
		supporter += supporter * percentage / 100;
		if (supporter < 0) {
			supporter = 0;
		}
	}

	/**
	 * This method removes a supporter, but throws an exception if supporter field
	 * is equal to 0.
	 */
	public void killSupporter() {
		if (supporter == 0) {
			throw new IllegalStateException("The supporter field is already at 0.");
		}
		supporter--;
	}

	@Override
	public String toString() {
		return name + " : " + satisfaction + "% / " + supporter;
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
