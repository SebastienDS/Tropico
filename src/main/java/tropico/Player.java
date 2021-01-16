package tropico;

import tropico.events.FactionSatisfactionEffect;
import tropico.events.OtherEffect;
import tropico.events.OtherEffect.types;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Player implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Resources resources = new Resources(15, 15, 200);
	private final List<Faction> factions;

	public Player(List<Faction> factions) throws FileNotFoundException {
		this.factions = factions;
	}

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
		int thresholdOfDefeat = 10;
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

		// TODO better way ?
		FactionSatisfactionEffect effect2 = new FactionSatisfactionEffect("loyalistes", bribeCost / 10);
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
		str.append("Vous avez générer " + resources.generateFood() + " de nourriture.\n");
		str.append("Vous avez générer " + resources.generateMoney() + "$.\n");

		int pop = getSupporterTotal();
		int overflow = resources.consumeFood(pop);

		if (overflow > 0) {
			killSupporters(overflow, pop);
			str.append("Par manque de nourriture, " + overflow + " partisans sont morts.");
		} else if (resources.hasEnoughFarming(pop)) {
			str.append("La nourriture coule à flots ! Il y a " + generateNewSupporters(pop) + " nouveaux partisans.");
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
					factions.forEach(f->f.addSatisfaction(-2));
					break;
				}
			}
		}
	}

	private int generateNewSupporters(int pop) {
		Random rd = new Random();
		int addedPop = (int) (pop * (rd.nextFloat() * 9 + 1));
		float count, rdfloat;

		for (int i = 0; i < addedPop; i++) {
			rdfloat = rd.nextFloat();
			count = 0;

			HashMap<Faction, Float> factionChances = calculateFactionsChances(pop);

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

	private HashMap<Faction, Float> calculateFactionsChances(int pop) {
		HashMap<Faction, Float> chances = new HashMap<Faction, Float>();
		float factor;

		for (Faction faction : factions) {
			factor = (float) Math.max(faction.getSatisfaction() / 100 * 0.9 + 0.1, 0.2);
			chances.put(faction, faction.getSupporter() / pop * factor);
		}

		float total = 0;
		for (float f : chances.values()) {
			total += f;
		}

		for (Faction faction : factions) {
			chances.replace(faction, chances.get(faction) / total);
		}

		return chances;
	}

}
