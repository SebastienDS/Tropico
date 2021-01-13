package tropico;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.List;

public class Player {

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
	
	public int getSupporterTotal() {
		int total = 0;
		for (Faction faction : factions) {
			total += faction.getSupporter();
		}
		
		return total;
	}
	
	public void generateResources() {
		resources.generateFood();
		resources.generateMoney();
		int pop = getSupporterTotal();
		int overflow = resources.consumeFood(pop);
		if (overflow > 0) {
			// TODO
		} else if (resources.hasEnoughFarming(pop)) {
			// TODO
		}
	}
}
