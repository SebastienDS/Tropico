package tropico;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import tropico.events.Event;
import tropico.utils.UtilsDeserialization;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.*;

public class GameState implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final String SCENARIO_PATH = "src/main/resources/scenarios";

	private final Map<Season, List<Event>> events;
	private final PlayerManagement players;
	private final String gamemode;
	private final List<Event> pendingEvents = new ArrayList<>();
	private Event currentEvent;
	private Season season;
	private int turn;

	public GameState(String gamemode, int playerNumbers) throws FileNotFoundException {
		this.season = Season.SPRING;
		this.gamemode = gamemode;
		String path = SCENARIO_PATH + "/" + gamemode + "/";

		players = new PlayerManagement(path, playerNumbers);
		List<Faction> factions = getPlayer().getFactions();
		events = loadEvents(factions, path + "events.json");
		currentEvent = newEvent();
		turn = 1;
	}

	public GameState() throws FileNotFoundException {
		this("bac_a_sable", 1);
	}

	public List<Event> getPendingEvents() {
		return pendingEvents;
	}

	public Player getPlayer() {
		return players.getPlayer();
	}

	public int getTurn() {
		return turn;
	}

	public Event getCurrentEvent() {
		return currentEvent;
	}

	public String getGamemode() {
		return gamemode;
	}

	/**
	 * add a Pending Event
	 * @param e
	 */
	public void addPendingEvent(Event e) {
		pendingEvents.add(Objects.requireNonNull(e));
	}

	/**
	 * get a new random event
	 * 
	 * @return random Event
	 */
	private Event newEvent() {
		Random rand = new Random();

		if (havePendingEvent() && rand.nextInt(2) == 1) {
			return pendingEvents.remove(rand.nextInt(pendingEvents.size()));
		}

		List<Event> list = events.get(season);
		return list.get(rand.nextInt(list.size()));
	}

	/**
	 * check if have Pending Event
	 * @return true if have Pending Event
	 */
	private boolean havePendingEvent() {
		return pendingEvents.size() > 0;
	}

	/**
	 * next turn
	 */
	public void nextTurn() {
		season = Season.nextSeason(season);
		currentEvent = newEvent();
		turn++;

		players.nextTurn();
	}

	public boolean isEndOfYear() {
		return season.equals(Season.WINTER);
	}

	/**
	 * check if the game is over
	 * 
	 * @return true if a player is dead
	 */
	public boolean isGameOver() {
		return players.havePlayerDead();
	}

	/**
	 * load Events from json file
	 * 
	 * @param eventsPath
	 * @return Map with events for each season
	 * @throws FileNotFoundException
	 */
	private static Map<Season, List<Event>> loadEvents(List<Faction> factions, String eventsPath)
			throws FileNotFoundException {
		Type eventType = new TypeToken<Map<Season, List<Event>>>() {
		}.getType();

		Gson gson = new GsonBuilder().registerTypeAdapter(eventType, new UtilsDeserialization(factions)).create();

		return gson.fromJson(new JsonReader(new FileReader(eventsPath)), eventType);
	}

	@Override
	public String toString() {
		return "season=" + season;
	}
}
