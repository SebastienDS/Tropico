package tropico;

import java.io.Serializable;

/**
 * This class contains all the informations about the player's resources,
 * including:
 * <ul>
 * <li>The island's industrialisation percentage</li>
 * <li>The island's farming percentage</li>
 * <li>The country's money</li>
 * <li>The country's food</li>
 * </ul>
 * 
 * @author Corentin OGER & Sébastien DOS SANTOS
 *
 */
public class Resources implements Serializable {

	/**
	 * Necessary field to avoid warning while implementing Serializable.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The minimum value for the fields industry and farming.
	 */
	private static final int RESOURCE_MIN = 0;

	/**
	 * The maximum value for the fields industry and farming.
	 */
	private static final int RESOURCE_MAX = 100;

	/**
	 * The number of food required to feed a supporter.
	 */
	private static final int FOOD_UNIT_PER_CITIZEN = 4;

	/**
	 * An int representing the island's industrialisation percentage.
	 */
	private int industry;

	/**
	 * An int representing the island's farming percentage.
	 */
	private int farming;

	/**
	 * An int representing the country's money.
	 */
	private int treasury;

	/**
	 * An int representing the country's food.
	 */
	private int foodUnit;

	/**
	 * <b>Resources's constructor</b>
	 * 
	 * Creates a Resource object based on the values it should start with.
	 * 
	 * @param industry An int representing the percentage of industrialization.
	 * @param farming  An int representing the percentage of farming.
	 * @param treasury An int representing the treasury.
	 * @param foodUnit An int representing the food units.
	 */
	public Resources(int industry, int farming, int treasury, int foodUnit) {
		if (industry + farming > RESOURCE_MAX) {
			throw new IllegalArgumentException("Industry and/or farming value is over " + RESOURCE_MAX + ".");
		}
		if (industry < RESOURCE_MIN) {
			throw new IllegalArgumentException("Industry value is inferior to " + RESOURCE_MIN + ".");
		}
		if (farming < RESOURCE_MIN) {
			throw new IllegalArgumentException("Farming value is inferior to " + RESOURCE_MIN + ".");
		}
		if (treasury < RESOURCE_MIN) {
			throw new IllegalArgumentException("Treasury value is inferior to " + RESOURCE_MIN + ".");
		}
		if (foodUnit < RESOURCE_MIN) {
			throw new IllegalArgumentException("FoodUnit value is inferior to " + RESOURCE_MIN + ".");
		}

		this.industry = industry;
		this.farming = farming;
		this.treasury = treasury;
		this.foodUnit = foodUnit;
	}

	/**
	 * Getter for the field industry.
	 * 
	 * @return Returns an int representing the island's industrialisation
	 *         percentage.
	 */
	public int getIndustry() {
		return industry;
	}

	/**
	 * Getter for the field farming.
	 * 
	 * @return Returns an int representing the island's farming percentage.
	 */
	public int getFarming() {
		return farming;
	}

	/**
	 * Getter for the field treasury.
	 * 
	 * @return Returns an int representing the treasury.
	 */
	public int getTreasury() {
		return treasury;
	}

	/**
	 * Getter for the field foodUnit.
	 * 
	 * @return Returns an int representing the number of food units.
	 */
	public int getFoodUnit() {
		return foodUnit;
	}

	/**
	 * Creates a copy of a Resources object.
	 * 
	 * @return A copy of the resources.
	 */
	public Resources copy() {
		return new Resources(industry, farming, treasury, foodUnit);
	}

	/**
	 * Adds the int value to the field industry.
	 * 
	 * @param value The value to add.
	 */
	public void addIndustry(int value) {
		industry += value;
		industry = limit(industry, farming);
	}

	/**
	 * Adds the int value to the field farming.
	 * 
	 * @param value The value to add.
	 */
	public void addFarming(int value) {
		farming += value;
		farming = limit(farming, industry);
	}

	/**
	 * Adds the int value to the field treasury.
	 * 
	 * @param value The value to add.
	 */
	public void addMoney(int value) {
		if (treasury < -value) {
			treasury = 0;
		}
		treasury += value;
	}

	/**
	 * Adds the int value to the field treasury.
	 * 
	 * @param value The value to add.
	 */
	public void addFood(int value) {
		if (foodUnit < -value) {
			foodUnit = 0;
		}
		foodUnit += value;
	}

	/**
	 * Generates money based on industrialization level (industry) and returns its
	 * value.
	 * 
	 * @return Returns an int representing the money generated.
	 */
	public int generateMoney() {
		int money = industry * 10;
		addMoney(money);
		return money;
	}

	/**
	 * Generates food based on the level of agriculture (farming) and returns its
	 * value.
	 * 
	 * @return Returns an int representing the food generated.
	 */
	public int generateFood() {
		int foodUnit = farming * 40;
		addFood(foodUnit);
		return foodUnit;
	}

	/**
	 * Limits the int a between RESOURCE_MIN and RESOURCE_MAX less b (b has to be
	 * positive).
	 * 
	 * @param a The int that has to fit between the bounds.
	 * @param b A positive int that limits a.
	 * @return Returns an int. RESOURCE_MIN if a is below it, RESOURCE_MAX less b if
	 *         above, and a otherwise.
	 */
	private static int limit(int a, int b) {
		if (b < 0) {
			throw new IllegalArgumentException("b inferior to 0.");
		}
		if (a < RESOURCE_MIN) {
			return RESOURCE_MIN;
		}
		return Math.min(a, RESOURCE_MAX - b);
	}

	@Override
	public String toString() {
		return "Ressources :\n" + "Industrialisation : " + industry + "% / Agriculture : " + farming + "%\n"
				+ "Trésorerie : " + treasury + "$\n" + "Nourriture : " + foodUnit + " unités";
	}

	/**
	 * Consume the number of food needed for the population. If it's too low,
	 * returns a number of citizen who will die
	 * 
	 * @param pop The total number of supporters you want to feed.
	 * @return Returns a number of supporters that couldn't be fed.
	 */
	public int consumeFood(int pop) {
		int totalNeeded = pop * FOOD_UNIT_PER_CITIZEN;

		if (totalNeeded > foodUnit) {
			int nbr = pop - foodUnit / FOOD_UNIT_PER_CITIZEN;
			foodUnit %= FOOD_UNIT_PER_CITIZEN;
			return nbr;
		}

		foodUnit -= totalNeeded;
		return 0;
	}

	/**
	 * Calculates if there is enough farming to feed pop persons.
	 * 
	 * @param pop The total number of supporters.
	 * @return Returns true the consomation of food is strictly inferior to the
	 *         production.
	 */
	public boolean hasEnoughFarming(int pop) {
		return pop * FOOD_UNIT_PER_CITIZEN < farming * 40;
	}
}
