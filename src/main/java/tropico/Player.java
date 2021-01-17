package tropico;

import tropico.events.FactionSatisfactionEffect;
import tropico.events.OtherEffect;
import tropico.events.OtherEffect.types;

import java.io.Serializable;
import java.util.*;

/**
 * This class contains informations about a player, including:
 * <ul>
 * <li></li>
 * <li>The name of the player</li>
 * <li>The resources of the player</li>
 * <li>A list of the factions</li>
 * </ul>
 * 
 * @author Corentin OGER & Sébastien DOS SANTOS
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
	 * Creates a player using a name, the list of the different factions of this scenario and the base resources for this scenario.
	 * 
	 * @param name A String which represents the name of the player.
	 * @param factions A list of the factions.
	 * @param resources The base resources.
	 */
	public Player(String name, List<Faction> factions, Resources resources) {
		this.name = Objects.requireNonNull(name);
		this.factions = Objects.requireNonNull(factions);
		this.resources = Objects.requireNonNull(resources);
	}

	/**
	 * 
	 * @return
	 */
	public List<Faction> getFactions() {
		return List.copyOf(factions);
	}

	public List<Faction> getSatisfiedFactions() {
		ArrayList<Faction> fList = new ArrayList<Faction>();
		for (Faction faction : factions) {
			if (!faction.hasZeroSatisfaction()) {
				fList.add(faction);
			}
		}
		return List.copyOf(fList);
	}

	public String getName() {
		return name;
	}

	public int getIndustry() {
		return resources.getIndustry();
	}

	public int getFarming() {
		return resources.getFarming();
	}

	public int getTreasury() {
		return resources.getTreasury();
	}

	public int getFoodUnit() {
		return resources.getFoodUnit();
	}

	public Faction getFactionFromName(String factionName) {
		for (Faction faction : factions) {
			if (faction.isName(factionName)) {
				return faction;
			}
		}

		throw new IllegalArgumentException("Le nom n'est pas dans les factions existantes.");
	}

	public String getResourcesAsString() {
		return resources.toString();
	}

	/**
	 * get total supporters of every factions
	 *
	 * @return total supporter
	 */
	public int getSupporterTotal() {
		return factions.stream().mapToInt(Faction::getSupporter).sum();
	}

	public int getBribeCost(Faction f) {
		if (!factions.contains(f)) {
			throw new IllegalArgumentException("La faction n'existe pas.");
		}
		return f.getBribeCost();
	}

	/**
	 * Adds industry value
	 *
	 * @param value
	 */
	public void addIndustry(int value) {
		resources.addIndustry(value);
	}

	/**
	 * Adds farming value
	 *
	 * @param value
	 */
	public void addFarming(int value) {
		resources.addFarming(value);
	}

	/**
	 * Adds money to the treasury
	 *
	 * @param value
	 */
	public void addMoney(int value) {
		resources.addMoney(value);
	}

	/**
	 * Adds food to the foofUnit field
	 *
	 * @param value
	 */
	public void addFood(int value) {
		resources.addFood(value);
	}

	/**
	 * get if the player is dead
	 * 
	 * @return true is player is dead
	 */
	public boolean isDead() {
		int thresholdOfDefeat;
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
			throw new IllegalStateException("La difficulté n'existe pas.");
		}

		int sum = 0;
		int totalSupporter = 0;

		for (Faction faction : factions) {
			sum += faction.getSatisfaction() * faction.getSupporter();
			totalSupporter += faction.getSupporter();
		}
		return totalSupporter == 0 || (double) sum / totalSupporter < thresholdOfDefeat;
	}

	public boolean bribe(Faction f) {
		if (!factions.contains(f)) {
			throw new IllegalArgumentException("La faction n'existe pas.");
		}
		int bribeCost = f.getBribeCost();

		if (bribeCost > resources.getTreasury()) {
			return false;
		}

		FactionSatisfactionEffect effect1 = new FactionSatisfactionEffect(f.getName(), 10);
		System.out.println(effect1);
		effect1.applyEffect(this);

		FactionSatisfactionEffect effect2 = new FactionSatisfactionEffect("loyalistes", -bribeCost / 10);
		System.out.println(effect2);
		effect2.applyEffect(this);

		resources.addMoney(-bribeCost);

		return true;
	}

	public void buyFood(int unit) {
		int cost = unit * 8;
		if (cost > getTreasury()) {
			throw new IllegalArgumentException("Pas assez d'argent pour acheter autant.");
		}
		if (unit <= 0) {
			throw new IllegalArgumentException("Le nombre d'unité doit être supérieur à 0.");
		}

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
	 * generate Resources
	 * 
	 * @return Returns a String representing the resources lost or acquired
	 */
	public String generateResources() {
		StringBuilder str = new StringBuilder();
		str.append("Vous avez générer ").append(resources.generateFood()).append(" de nourriture.\n");
		str.append("Vous avez générer ").append(resources.generateMoney()).append("$.\n");

		int pop = getSupporterTotal();
		int overflow = resources.consumeFood(pop);

		if (overflow > 0) {
			killSupporters(overflow, pop);
			str.append("Par manque de nourriture, ").append(overflow).append(" partisans sont morts.");
		} else if (resources.hasEnoughFarming(pop)) {
			str.append("La nourriture coule à flots ! Il y a ").append(generateNewSupporters(pop))
					.append(" nouveaux partisans.");
		}

		return str.toString();
	}

	private void killSupporters(int overflow, int pop) {
		Random rd = new Random();
		float count, rdfloat;
		for (int i = 0; i < overflow; i++) {
			rdfloat = rd.nextFloat();
			count = 0;

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

	private int generateNewSupporters(int pop) {
		Random rd = new Random();
		int addedPop = (int) (pop * (rd.nextFloat() * 9 + 1) / 100);
		double rdfloat;
		double count;

		for (int i = 0; i < addedPop; i++) {
			rdfloat = rd.nextDouble();
			count = 0;

			HashMap<Faction, Double> factionChances = calculateFactionsChances(pop);

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

	private HashMap<Faction, Double> calculateFactionsChances(int pop) {
		HashMap<Faction, Double> chances = new HashMap<Faction, Double>();
		float factor;

		for (Faction faction : factions) {
			factor = (float) Math.max(faction.getSatisfaction() * 0.9 / 100  + 0.1, 0.2);
			chances.put(faction, faction.getSupporter() * 1.0 / pop * factor);
		}

		double total = 0.0;
		for (double f : chances.values()) {
			total += f;
		}

		for (Faction faction : factions) {
			chances.replace(faction, chances.get(faction) / total);
		}

		return chances;
	}

}
