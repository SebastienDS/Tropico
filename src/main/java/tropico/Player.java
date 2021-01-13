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
     * load factions from json file
     * @param path
     * @return List of faction
     * @throws FileNotFoundException
     */
    private static List<Faction> loadFactions(String path) throws FileNotFoundException {
        Type eventType = new TypeToken<List<Faction>>(){}.getType();

        Gson gson = new Gson();
        return gson.fromJson(new JsonReader(new FileReader(path)), eventType);
    }

    @Override
    public String toString() {
        return "Player{" +
                "resources=" + resources +
                ", factions=" + factions +
                '}';
    }
}
