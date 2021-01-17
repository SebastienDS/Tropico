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
import java.util.stream.Collectors;

public class GameState implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final String SCENARIO_PATH = "src/main/resources/scenarios";

	private final List<Event> events;
	private final List<Event> usedEvents = new ArrayList<>();
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

	public int getCurrentPlayer() {
		return players.getCurrentPlayer();
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

		if (rand.nextInt(2) == 1) {
			Optional<Event> e = this.getPendingEvent();
			if (e.isPresent()) {
				Event event = e.get();
				pendingEvents.remove(event);
				return event;
			}
			return pendingEvents.remove(rand.nextInt(pendingEvents.size()));
		}

		List<Event> list = this.getEvents(season);
		if (list.isEmpty()) {
			this.repopulateEventsWithUsed(season);
			list = this.getEvents(season);
		}
		Event event = list.get(rand.nextInt(list.size()));
		events.remove(event);
		usedEvents.add(event);
		return event;
	}

	/**
	 * repopulate events not used with used events for a specific season
	 * @param season
	 */
	private void repopulateEventsWithUsed(Season season) {
		List<Event> eventsForThisSeason = usedEvents.stream().filter(e -> e.getSeasons().contains(season)).collect(Collectors.toList());
		events.addAll(eventsForThisSeason);
		usedEvents.removeAll(eventsForThisSeason);
	}

	/**
	 * get an event form pending event
	 * @return Optional Event
	 */
	private Optional<Event> getPendingEvent() {
		return pendingEvents.stream().filter(e -> e.getSeasons().contains(season)).findAny();
	}

	/**
	 * get Events of a season
	 * @param season
	 * @return List of Event for the giving season
	 */
	private List<Event> getEvents(Season season) {
		return events.stream().filter(e -> e.getSeasons().contains(season)).collect(Collectors.toList());
	}
	/**
	 * next turn
	 */
	public void nextTurn() {
		currentEvent = newEvent();
		turn++;

		players.nextTurn();
	}

	/**
	 * next season
	 */
	public void nextSeason() {
		season = Season.nextSeason(season);
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
	private static List<Event> loadEvents(List<Faction> factions, String eventsPath)
			throws FileNotFoundException {
		Type eventType = new TypeToken<List<Event>>() {
		}.getType();

		Gson gson = new GsonBuilder().registerTypeAdapter(eventType, new UtilsDeserialization(factions)).create();

		return gson.fromJson(new JsonReader(new FileReader(eventsPath)), eventType);
	}

	@Override
	public String toString() {
		return "season=" + season;
	}
}
