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

/**
 * This class stocks all the information about the game, including :
 * <ul>
 * <li>A list of possible events</li>
 * <li>A list of already used events</li>
 * <li>A list of events that will be available after a certain action</li>
 * <li>A PlayerManagement object, that contains informations about the
 * players</li>
 * <li>The name of the gamemode</li>
 * <li>The current event, which changes every turn</li>
 * <li>The current season, which changes after all the players played a
 * turn</li>
 * <li>The current turn</li>
 * </ul>
 *
 * @author Corentin OGER & Sébastien DOS SANTOS
 *
 */
public class GameState implements Serializable {

	/**
	 * Necessary field to avoid warning while implementing Serializable.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Static String containing the path to the scenarios' directories.
	 */
	private static final String SCENARIO_PATH = "src/main/resources/scenarios";

	/**
	 * The list which contains the events that can occur right now, if the current
	 * season is the right one.
	 */
	private final List<Event> events;

	/**
	 * A list of already used events, the events can be re-used once there's no more
	 * event for a season.
	 */
	private final List<Event> usedEvents = new ArrayList<>();

	/**
	 * A list of events that aren't available. The events are added to events once
	 * certains choices are made.
	 */
	private final List<Event> pendingEvents = new ArrayList<>();

	/**
	 * A PlayerManagement object, that contains informations about the players.
	 */
	private final PlayerManagement players;

	/**
	 * The name of the gamemode.
	 */
	private final String gamemode;

	/**
	 * The current event, which changes every turn.
	 */
	private Event currentEvent;

	/**
	 * The current season, which changes after all the players played a turn.
	 */
	private Season season;

	/**
	 * The current turn.
	 */
	private int turn;

	/**
	 * <b>GameState's constructor</b>
	 *
	 * Creates all the game's informations, based on json files found using
	 * SCENARIO_PATH concatenated with the gamemode. Also needs the number of
	 * players.
	 *
	 * @param gamemode      A String that must contains the gamemode entered by the
	 *                      user.
	 * @param playerNumbers An int representing the number of players.
	 * @throws FileNotFoundException Throws a FileNotFoundException if the path is
	 *                               wrong.
	 */
	public GameState(String gamemode, int playerNumbers) throws FileNotFoundException {
		if (playerNumbers < 0) {
			throw new IllegalArgumentException("The number of players must be superior or equal to 0.");
		}
		this.season = Season.SPRING;
		this.gamemode = Objects.requireNonNull(gamemode);
		String path = SCENARIO_PATH + "/" + gamemode + "/";

		players = new PlayerManagement(path, playerNumbers);
		List<Faction> factions = getPlayer().getFactions();
		events = loadEvents(factions, path + "events.json");
		currentEvent = newEvent();
		turn = 1;
	}

	/**
	 * <b>GameState's second constructor</b>
	 *
	 * Calls the first constructor with one player and the default mode (sandbox).
	 *
	 * @throws FileNotFoundException Throws a FileNotFoundException if the path is
	 *                               wrong.
	 */
	public GameState() throws FileNotFoundException {
		this("bac_a_sable", 1);
	}

	/**
	 * This method returns a player, the one who's playing this turn.
	 *
	 * @return Returns a Player object.
	 */
	public Player getPlayer() {
		return players.getPlayer();
	}

	/**
	 * Getter for the turn field.
	 *
	 * @return Returns an int which represents the current turn.
	 */
	public int getTurn() {
		return turn;
	}

	/**
	 * Getter for the currentEvent field.
	 *
	 * @return Returns the Event occuring this turn.
	 */
	public Event getCurrentEvent() {
		return currentEvent;
	}

	/**
	 * Getter for the gamemode field.
	 *
	 * @return Returns a String which represents the gamemode.
	 */
	public String getGamemode() {
		return gamemode;
	}

	/**
	 * This method returns an int which represents the index of the player in the
	 * players list. It serves as a name to identify him.
	 *
	 * @return Returns an int that represents the current player's index.
	 */
	public int getCurrentPlayer() {
		return players.getCurrentPlayer();
	}

	/**
	 * Adds an event to pendingEvents.
	 *
	 * @param e The event to be added.
	 */
	public void addPendingEvent(Event e) {
		pendingEvents.add(Objects.requireNonNull(e));
	}

	/**
	 * Updates the game and makes it ready for the next turn.
	 */
	public void nextTurn() {
		currentEvent = newEvent();
		turn++;

		players.nextTurn();
	}

	/**
	 * Selects a new event to be the currentEvent.
	 *
	 * @return Returns a random Event from events list.
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

		// Checks if there is still events available for the current season
		List<Event> list = getEvents(season);
		if (list.isEmpty()) {
			repopulateEventsWithUsed(season);
			list = getEvents(season);
		}
		Event event = list.get(rand.nextInt(list.size()));
		events.remove(event);
		usedEvents.add(event);
		return event;
	}

	/**
	 * Repopulates events not used with used events for a specific season.
	 *
	 * @param season The season you need to repopulate events.
	 */
	private void repopulateEventsWithUsed(Season season) {
		List<Event> eventsForThisSeason = usedEvents.stream().filter(e -> e.getSeasons().contains(season))
				.collect(Collectors.toList());
		events.addAll(eventsForThisSeason);
		usedEvents.removeAll(eventsForThisSeason);
	}

	/**
	 * Returns an Optional Event if a pending event exists for the current season.
	 * @return Returns an Optional Event
	 */
	private Optional<Event> getPendingEvent() {
		return pendingEvents.stream().filter(e -> e.getSeasons().contains(season)).findAny();
	}

	/**
	 * This method takes a season and return all the events than can occur during
	 * it.
	 *
	 * @param season The season for which you need the events.
	 * @return Returns a list of events that can occur during the season.
	 */
	private List<Event> getEvents(Season season) {
		return events.stream().filter(e -> e.getSeasons().contains(season)).collect(Collectors.toList());
	}

	/**
	 * This method calls nextSeason from Season enum class, and changes the season
	 * field.
	 */
	public void nextSeason() {
		season = Season.nextSeason(season);
	}

	/**
	 * Checks if this is the end of the year, which is true chen the current season
	 * is winter.
	 *
	 * @return Returns true if season field equals Season.WINTER, false otherwise.
	 */
	public boolean isEndOfYear() {
		return season.equals(Season.WINTER);
	}

	/**
	 * Checks if the game is over. The game is over when there is only one player
	 * left if there where more than one initialy, or when the player loses
	 * otherwise.
	 *
	 * @return Returns true when the player is dead or all other players are dead
	 *         (multiplayer), false otherwise.
	 */
	public boolean isGameOver() {
		return players.havePlayerDead();
	}

	/**
	 * Loads Events from json file. Requires the list of all the factions and the
	 * path to find the file.
	 *
	 * @param factions   The list containing all the factions of this scenario.
	 * @param eventsPath The path where the json file is located.
	 * @return Returns the list of all the events of this scenario.
	 * @throws FileNotFoundException Throws a FileNotFoundException if the path is
	 *                               wrong.
	 */
	private static List<Event> loadEvents(List<Faction> factions, String eventsPath) throws FileNotFoundException {
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
