package tropico;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Player implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Resources resources = new Resources(0, 0, 0);
	private final List<Faction> factions;

	public Player() throws FileNotFoundException {
		factions = loadFactions("src/factions.json");
	}

	public List<Faction> getFactions() {
		return List.copyOf(factions);
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

	@Override
	public String toString() {
		return "Player{" + "resources=" + resources + ", factions=" + factions + '}';
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

	/**
	 * load factions from json file
	 *
	 * @param path The path where the json file is located
	 * @return List of faction
	 * @throws FileNotFoundException
	 */
	private static List<Faction> loadFactions(String path) throws FileNotFoundException {
		Type eventType = new TypeToken<List<Faction>>() {
		}.getType();

		Gson gson = new Gson();
		return gson.fromJson(new JsonReader(new FileReader(path)), eventType);
	}
}
