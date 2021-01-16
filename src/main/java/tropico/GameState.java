package tropico;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import tropico.events.Event;
import tropico.events.EventsDeserializer;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GameState implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Map<Season, List<Event>> events;
    private final Difficulty difficulty;
    private final PlayerManagement players;
    private Season season;
    private int turn;

    public GameState(Season season, Difficulty difficulty) throws FileNotFoundException {
        this.season = season;
        this.difficulty = difficulty;
        events = loadEvents("src/test.json");
        players = new PlayerManagement();
        turn = 1;
    }

    public GameState(Difficulty difficulty) throws FileNotFoundException {
        this(Season.SPRING, difficulty);
    }

    public GameState() throws FileNotFoundException {
        this(Season.SPRING, Difficulty.MEDIUM);
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public Player getPlayer() {
        return players.getPlayer();
    }

    public int getTurn() {
		return turn;
	}

    /**
     * get a new random event
     * @return random Event
     */
    public Event getNewEvent() {
        Random rand = new Random();
        List<Event> list = events.get(season);
        return list.get(rand.nextInt(list.size()));
    }
    
    /**
     * next turn
     */
    public void nextTurn() {
    	season = Season.nextSeason(season);
    	turn++;
    	
        players.nextTurn();
    }
    
    public boolean isEndOfYear() {
    	return season.equals(Season.WINTER);
    }

    /**
     * check if the game is over
     * @return true if a player is dead
     */
    public boolean isGameOver() {
        return players.havePlayerDead();
    }

    /**
     * load Events from json file
     * @param eventsPath
     * @return Map with events for each season
     * @throws FileNotFoundException
     */
    private static Map<Season, List<Event>> loadEvents(String eventsPath) throws FileNotFoundException {
        Type eventType = new TypeToken<Map<Season, List<Event>>>(){}.getType();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(eventType, new EventsDeserializer())
                .create();

        return gson.fromJson(new JsonReader(new FileReader(eventsPath)), eventType);
    }

    @Override
    public String toString() {
        return "season=" + season;
    }
}
