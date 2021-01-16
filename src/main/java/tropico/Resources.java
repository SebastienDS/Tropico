package tropico;

import java.io.Serializable;

public class Resources implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final int RESOURCE_MIN = 0;
	private static final int RESOURCE_MAX = 100;
	private static final int FOOD_UNIT_PER_CITIZEN = 4;

	private int industry;
	private int farming;
	private int treasury;
	private int foodUnit;

	public Resources(int industry, int farming, int treasury, int foodUnit) {
		this.industry = industry;
		this.farming = farming;
		this.treasury = treasury;
		this.foodUnit = foodUnit;
	}

	public int getIndustry() {
		return industry;
	}

	public int getFarming() {
		return farming;
	}

	public int getTreasury() {
		return treasury;
	}

	public int getFoodUnit() {
		return foodUnit;
	}

	/**
	 * Adds industry value
	 * 
	 * @param value
	 */
	public void addIndustry(int value) {
		industry += value;
		industry = limit(industry, farming);
	}

	/**
	 * Adds farming value
	 * 
	 * @param value
	 */
	public void addFarming(int value) {
		farming += value;
		farming = limit(farming, industry);
	}

	/**
	 * Adds money to the treasury
	 * 
	 * @param value
	 */
	public void addMoney(int value) {
		if (treasury < -value) {
			throw new IllegalArgumentException("Le coût est trop élevé pour la trésorerie.");
		}
		treasury += value;
	}

	/**
	 * Adds food to the foofUnit field
	 * 
	 * @param value
	 */
	public void addFood(int value) {
		if (foodUnit < -value) {
			throw new IllegalArgumentException("Le coût est trop élevé pour les réserves de nourritures.");
		}
		foodUnit += value;
	}

	/**
	 * Generates money based on industrialization level (industry) and returns its
	 * value
	 * 
	 * @return money generated
	 */
	public int generateMoney() {
		int money = industry * 10;
		addMoney(money);
		return money;
	}

	/**
	 * Generates food based on farming level (farming) and returns its value
	 * 
	 * @return food unit generated
	 */
	public int generateFood() {
		int foodUnit = farming * 40;
		addFood(foodUnit);
		return foodUnit;
	}

	/**
	 * limit a with a + b between RESOURCE_MIN and RESOURCE_MAX
	 * 
	 * @param a
	 * @param b
	 * @return a limited on the interval
	 */
	private static int limit(int a, int b) {
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
	 * @param pop the number of citizen
	 * @return number of citizen that can't be fed
	 */
	public int consumeFood(int pop) {
		int totalNeeded = pop * FOOD_UNIT_PER_CITIZEN;
		
		if (totalNeeded > foodUnit) {
			int nbr = pop - foodUnit/FOOD_UNIT_PER_CITIZEN ;
			foodUnit %= FOOD_UNIT_PER_CITIZEN;
			return nbr;
		}
		
		foodUnit -= totalNeeded;
		return 0;
	}

	/**
	 * get if we can remove enough farming
	 * @param pop
	 * @return true if enough farming to pop
	 */
	public boolean hasEnoughFarming(int pop) {
		return pop * FOOD_UNIT_PER_CITIZEN < farming*40;
	}
}
