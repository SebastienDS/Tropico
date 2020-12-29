package tropico;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import tropico.events.Event;
import tropico.events.EventsDeserializer;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GameState {

    private static Season season;
    private final Difficulty difficulty;
    private final Resources resources = new Resources();
    private final List<Faction> factions;
    private final Map<Season, List<Event>> events;

    public GameState(Season season, Difficulty difficulty, List<Faction> factions) throws FileNotFoundException {
        this.season = season;
        this.difficulty = difficulty;
        this.factions = Objects.requireNonNull(factions);
        events = loadEvents("src/test.json");
    }

    private static Map<Season, List<Event>> loadEvents(String eventsPath) throws FileNotFoundException {
        Type eventType = new TypeToken<Map<Season, List<Event>>>(){}.getType();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(eventType, new EventsDeserializer())
                .create();

        return gson.fromJson(new JsonReader(new FileReader(eventsPath)), eventType);

    }
}
