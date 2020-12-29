package tropico.events;

import com.google.gson.JsonSyntaxException;
import tropico.Faction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Effects {

    private static final List<String> available = List.of(
            "agriculture",
            "industrialisation",
            "tresorerie"
    );

    private final Map<Faction, Integer> factionEffects = new HashMap<>();
    private final Map<String, Integer> resourceEffects = new HashMap<>();

    public static boolean isAvailable(String effect) {
        return available.contains(effect);
    }

    public void add(Faction faction, int value) {
        if (!Faction.contains(faction)) throw new JsonSyntaxException("Faction " + faction + "is not available");
        factionEffects.put(faction, value);
    }

    public void add(String resource, int value) {
        if (!Effects.isAvailable(resource)) throw new JsonSyntaxException("Resource " + resource + "is not available");
        resourceEffects.put(resource, value);
    }

    @Override
    public String toString() {
        return "Effects{" +
                "factionEffects=" + factionEffects +
                ", resourceEffects=" + resourceEffects +
                '}';
    }
}
