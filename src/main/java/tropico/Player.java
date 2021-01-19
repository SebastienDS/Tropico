package tropico;

import tropico.events.FactionSatisfactionEffect;
import tropico.events.OtherEffect;
import tropico.events.OtherEffect.types;

import java.io.Serializable;
import java.util.*;

/**
 * This class contains informations about a player, including:
 * <ul>
 * <li>The name of the player</li>
 * <li>The resources of the player</li>
 * <li>A list of the factions</li>
 * </ul>
 * 
 * @author Corentin OGER and Sébastien DOS SANTOS
 *
 */
public class Player implements Serializable {

	/**
	 * Necessary field to avoid warning while implementing Serializable.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The name of the player.
	 */
	private final String name;

	/**
	 * The player's resources.
	 */
	private final Resources resources;

	/**
	 * The player's factions, with their satisfaction and supporters.
	 */
	private final List<Faction> factions;

	/**
	 * <b>Player's constructor</b>
	 * 
	 * Creates a player using a name, the list of the different factions of this
	 * scenario and the base resources for this scenario.
	 * 
	 * @param name      A String which represents the name of the player.
	 * @param factions  A list of the factions.
	 * @param resources The base resources.
	 */
	public Player(String name, List<Faction> factions, Resources resources) {
		this.name = Objects.requireNonNull(name);
		this.factions = Objects.requireNonNull(factions);
		this.resources = Objects.requireNonNull(resources);
	}

	/**
	 * Getter for the field factions.
	 * 
	 * @return Returns a unmodifiable copy of the factions.
	 */
	public List<Faction> getFactions() {
		return List.copyOf(factions);
	}

	/**
	 * Getter for the field name.
	 * 
	 * @return Returns the player's name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Calls the method getIndustry from resources.
	 * 
	 * @return Returns the industry percentage.
	 */
	public int getIndustry() {
		return resources.getIndustry();
	}

	/**
	 * Calls the method getFarming from resources.
	 * 
	 * @return Returns the farming percentage.
	 */
	public int getFarming() {
		return resources.getFarming();
	}

	/**
	 * Calls the method getTreasury from resources.
	 * 
	 * @return Returns the player's total money.
	 */
	public int getTreasury() {
		return resources.getTreasury();
	}

	/**
	 * Calls the method getFoodUnit from resources.
	 * 
	 * @return Returns the player's total food.
	 */
	public int getFoodUnit() {
		return resources.getFoodUnit();
	}

	/**
	 * Searches a faction from a string representing its name. If it doesn't exist,
	 * an IllegalArgumentException is thrown.
	 * 
	 * @param factionName A String which should the name of one of the factions.
	 * @return Returns a faction which name is factionName.
	 */
	public Faction getFactionFromName(String factionName) {
		String name = Objects.requireNonNull(factionName);

		for (Faction faction : factions) {
			if (faction.isName(name)) {
				return faction;
			}
		}

		throw new IllegalArgumentException('"' + name + "\" is not a faction's name.");
	}

	/**
	 * This method calls the toString method of resources, transforming it into a
	 * String.
	 * 
	 * @return Returns a String of the player's resources.
	 */
	public String getResourcesAsString() {
		return resources.toString();
	}

	/**
	 * This method returns the count of all the supporters in all the player's
	 * factions.
	 *
	 * @return Returns an int representing the the total number of supporters.
	 */
	public int getSupporterTotal() {
		return factions.stream().mapToInt(Faction::getSupporter).sum();
	}

	/**
	 * Use this method to get the cost to bribe the faction f. Throws an
	 * IllegalArgumentException if there's no such faction.
	 * 
	 * @param f The faction you want to get the bribe cost.
	 * @return Returns the cost to bribe the faction f.
	 */
	public int getBribeCost(Faction f) {
		if (!factions.contains(f)) {
			throw new IllegalArgumentException("The faction doesn't exists.");
		}
		return f.getBribeCost();
	}

	/**
	 * This method returns the list of the faction where the field satisfaction is
	 * greater than 0.
	 * 
	 * @return A list of factions.
	 */
	public List<Faction> getSatisfiedFactions() {
		ArrayList<Faction> fList = new ArrayList<Faction>();
		for (Faction faction : factions) {
			if (!faction.hasZeroSatisfaction()) {
				fList.add(faction);
			}
		}
		return List.copyOf(fList);
	}

	/**
	 * This method calls the method addIndustry from resources.
	 *
	 * @param value An int that you want to add to the industry field of the
	 *              resources.
	 */
	public void addIndustry(int value) {
		resources.addIndustry(value);
	}

	/**
	 * This method calls the method addFarming from resources.
	 *
	 * @param value An int that you want to add to the farming field of the
	 *              resources.
	 */
	public void addFarming(int value) {
		resources.addFarming(value);
	}

	/**
	 * This method calls the method addTreasury from resources.
	 *
	 * @param value An int representing the money you want to add to the resources.
	 */
	public void addMoney(int value) {
		resources.addMoney(value);
	}

	/**
	 * This method calls the method addFood from resources.
	 *
	 * @param value An int representing the food you want to add to the resources.
	 */
	public void addFood(int value) {
		resources.addFood(value);
	}

	/**
	 * This method allows you to know if the player is dead. The player is dead if
	 * he has no supporter if he has no supporters left or if the average
	 * satisfaction of the supporters is smaller than the threshold defined by the
	 * difficulty.
	 * 
	 * @return Returns true if the player is dead, false otherwise.
	 */
	public boolean isDead() {
		int thresholdOfDefeat;

		// Different according to the difficulty
		switch (DifficultySingleton.getDifficulty()) {
		case EASY:
			thresholdOfDefeat = 10;
			break;
		case MEDIUM:
			thresholdOfDefeat = 30;
			break;
		case HARD:
			thresholdOfDefeat = 50;
			break;
		default:
			throw new IllegalStateException("The difficulty doesn't exists.");
		}

		int sum = 0;
		int totalSupporter = 0;

		// Calculates the average satisfaction of a supporter
		for (Faction faction : factions) {
			sum += faction.getSatisfaction() * faction.getSupporter();
			totalSupporter += faction.getSupporter();
		}
		return totalSupporter == 0 || (double) sum / totalSupporter < thresholdOfDefeat;
	}

	/**
	 * This method serves to bribe a faction f one time, which means adding 10
	 * percent satisfaction to it, in exchange of money and satisfaction for the
	 * loyalists. It also returns a boolean that is true if this action was possible
	 * and false if not.
	 * 
	 * @param f The faction you want to bribe.
	 * @return Returns true if bribery was possible, false otherwise.
	 */
	public boolean bribe(Faction f) {
		if (!factions.contains(f)) {
			throw new IllegalArgumentException("The faction doesn't exists.");
		}
		int bribeCost = f.getBribeCost();

		if (bribeCost > resources.getTreasury()) {
			return false;
		}

		// Creating and applying effects in order to make the bribe and print the
		// result.
		FactionSatisfactionEffect effect1 = new FactionSatisfactionEffect(f.getName(), 10);
		System.out.println(effect1);
		effect1.applyEffect(this);

		FactionSatisfactionEffect effect2 = new FactionSatisfactionEffect("loyalistes", -bribeCost / 10);
		System.out.println(effect2);
		effect2.applyEffect(this);

		resources.addMoney(-bribeCost);

		return true;
	}

	/**
	 * This method is used to buy an int unit of food. Throws
	 * IllegalArgumentException if this action wasn't possible.
	 * 
	 * @param unit The number of food units the player wants to buy.
	 */
	public void buyFood(int unit) {
		int cost = unit * 8;
		if (cost > getTreasury()) {
			throw new IllegalArgumentException("Pas assez d'argent pour acheter autant.");
		}
		if (unit <= 0) {
			throw new IllegalArgumentException("Le nombre d'unité doit être supérieur à 0.");
		}

		// Creating and applying effects to apply the bribe and print the result.
		OtherEffect effect1 = new OtherEffect(types.FOODUNIT, unit);
		System.out.println(effect1);
		effect1.applyEffect(this);

		OtherEffect effect2 = new OtherEffect(types.TREASURY, -cost);
		System.out.println(effect2);
		effect2.applyEffect(this);

	}

	@Override
	public String toString() {
		return "Player{" + "resources=" + resources + ", factions=" + factions + '}';
	}

	/**
	 * This method should be used to generate resources each end of year. As its
	 * name suggests, its main goal is to generate the resources, based on the
	 * values of resources' fields, and the number of supporters.
	 * 
	 * If there is not enough food for the total supporter count, some of them will
	 * die, lowering all the factions' satisfaction. If there is enough farming, the
	 * number of supporters will grow.
	 * 
	 * @return Returns a String representing the resources lost and/or acquired.
	 */
	public String generateResources() {
		StringBuilder str = new StringBuilder();

		// Generates food and money from the resources
		str.append("Vous avez générer ").append(resources.generateFood()).append(" de nourriture.\n");
		str.append("Vous avez générer ").append(resources.generateMoney()).append("$.\n");

		int pop = getSupporterTotal();
		int overflow = resources.consumeFood(pop);

		// Kills supporters if there is not enough food, and adds some if there is
		// enough farming.
		if (overflow > 0) {
			killSupporters(overflow, pop);
			str.append("Par manque de nourriture, ").append(overflow).append(" partisans sont morts.");
		} else if (resources.hasEnoughFarming(pop)) {
			str.append("La nourriture coule à flots ! Il y a ").append(generateNewSupporters(pop))
					.append(" nouveaux partisans.");
		}

		return str.toString();
	}

	/**
	 * Calculates the number of supporters that has to die, which depends on the
	 * overflow value. The pop value could be calculated within the method. Lowers
	 * the general satisfaction when a supporter dies.
	 * 
	 * This method is private is only called once in generateResources.
	 * 
	 * @param overflow An int representing the number of supporters who couldn't be
	 *                 fed.
	 * @param pop      The total number of supporters.
	 */
	private void killSupporters(int overflow, int pop) {
		Random rd = new Random();
		float count, rdfloat;

		// For each supporters who couldn't be fed
		for (int i = 0; i < overflow; i++) {
			rdfloat = rd.nextFloat();
			count = 0;

			// Each time, randomly chooses the faction where a supporter dies. A faction has
			// a greater chance to lose a supporter if its count is higher than the others.
			for (Faction faction : factions) {
				count += faction.getSupporter() * 1.0 / pop;
				if (rdfloat <= count) {
					faction.killSupporter();
					pop--;
					factions.forEach(f -> f.addSatisfaction(-2));
					break;
				}
			}
		}
	}

	/**
	 * Calculates the number of supporters generated when there is enough farming
	 * and chooses a faction for each of them. Only called once in
	 * generateResources.
	 * 
	 * @param pop The number of total supporters.
	 * @return Returns the number of supporters generated.
	 */
	private int generateNewSupporters(int pop) {
		Random rd = new Random();

		// The number of supporters to generate
		int addedPop = (int) (pop * (rd.nextFloat() * 9 + 1) / 100);
		double rdfloat;
		double count;

		// For each of them
		for (int i = 0; i < addedPop; i++) {
			rdfloat = rd.nextDouble();
			count = 0;

			// Calculates each factions' odds to get the new supporter
			HashMap<Faction, Double> factionChances = calculateFactionsChances(pop);

			// Randomly chooses one of the faction and adds the supporter
			for (Faction faction : factions) {
				count += factionChances.get(faction);

				if (rdfloat <= count) {
					faction.addSupporter(1);
					break;
				}
			}
		}

		return addedPop;
	}

	/**
	 * This method is only used once in generateNewSupporters and only serves to
	 * calculate the odds of each factions to get a new supporter. Uses the total
	 * number of supporters, each factions' count of supporter and their
	 * satisfaction. Returns an hashmap with the faction associated with its odds.
	 * 
	 * @param pop The total number of supporters.
	 * @return Returns an HashMap containing the Factions as keys, and their chances
	 *         to get a new supporter as values.
	 */
	private HashMap<Faction, Double> calculateFactionsChances(int pop) {
		HashMap<Faction, Double> chances = new HashMap<Faction, Double>();
		float factor;

		for (Faction faction : factions) {
			// A factor calculated by getting the satisfaction between 0.1 and 1 or 0.2 if
			// the factor is too low
			factor = (float) Math.max(faction.getSatisfaction() * 0.9 / 100 + 0.1, 0.2);
			// First association : faction with its percentage of supporters multiplied by
			// the previous factor
			chances.put(faction, faction.getSupporter() * 1.0 / pop * factor);
		}

		double total = 0.0;
		for (double f : chances.values()) {
			total += f;
		}

		for (Faction faction : factions) {
			// Second association : faction with its odd between 0 and 1
			chances.replace(faction, chances.get(faction) / total);
		}

		return chances;
	}

}
