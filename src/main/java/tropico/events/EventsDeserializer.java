package tropico.events;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import tropico.Faction;
import tropico.Season;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventsDeserializer implements JsonDeserializer<HashMap<Season, List<Event>>> {

    @Override
    public HashMap<Season, List<Event>> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        HashMap<Season, List<Event>> events = new HashMap<>();

        JsonArray jsonArray = jsonElement.getAsJsonArray();
        for (JsonElement element: jsonArray) {
            Event event = deserializeEvent(element.getAsJsonObject(), jsonDeserializationContext);

            event.getSeasons().forEach(season -> {
                events.computeIfAbsent(season, k -> new ArrayList<>());
                events.get(season).add(event);
            });
        }
        return events;
    }

    private Event deserializeEvent(JsonObject obj, JsonDeserializationContext context) {
        String name = obj.get("name").getAsString();
        List<Season> seasons = deserializeSeasons(obj.get("seasons").getAsJsonArray(), context);
        List<Choice> choices = new ArrayList<>();
        obj.get("choices").getAsJsonArray().forEach(choice -> choices.add(deserializeChoice(choice.getAsJsonObject(), context)));
        JsonElement nextElement = obj.get("next");
        Event next = null;

        if (!nextElement.isJsonNull()) {
            next = deserializeEvent(nextElement.getAsJsonObject(), context);
        }

        return new Event(name, seasons, choices, next);
    }

    private List<Season> deserializeSeasons(JsonArray array, JsonDeserializationContext context) {
        List<Season> seasons = new ArrayList<>();

        Type seasonType = new TypeToken<Season>(){}.getType();

        array.forEach(elem -> {
            Season season = context.deserialize(elem, seasonType);
            if (!Season.contains(season)) throw new IllegalStateException("Season " + season + " is not available");
            seasons.add(season);
        });
        return seasons;
    }

    private Choice deserializeChoice(JsonObject obj, JsonDeserializationContext context) {
        String label = obj.get("label").getAsString();
        JsonObject effects = obj.getAsJsonObject("effects");
        return new Choice(label, deserializeEffects(effects, context));
    }

    private Effects deserializeEffects(JsonObject obj, JsonDeserializationContext context) {
        Effects effects = new Effects();

        Type factionType = new TypeToken<Map<Faction, Integer>>(){}.getType();
        Type resourceType = new TypeToken<Map<String, Integer>>(){}.getType();

        Map<Faction, Integer> factions = context.deserialize(obj.get("factions"), factionType);
        Map<String, Integer> resource = context.deserialize(obj.get("resources"), resourceType);

        if (factions == null) factions = new HashMap<>();
        if (resource == null) resource = new HashMap<>();

        factions.forEach(effects::add);
        resource.forEach(effects::add);

        return effects;
    }

}
