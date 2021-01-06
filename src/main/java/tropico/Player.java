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

    private static List<Faction> loadFactions(String path) throws FileNotFoundException {
        Type eventType = new TypeToken<List<Faction>>(){}.getType();

        Gson gson = new Gson();
        return gson.fromJson(new JsonReader(new FileReader(path)), eventType);
    }
}
