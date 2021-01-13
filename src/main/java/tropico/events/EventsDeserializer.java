package tropico.events;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import tropico.Season;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventsDeserializer implements JsonDeserializer<Map<Season, List<Event>>> {

    /**
     * Custom deserialize for events
     * @param jsonElement
     * @param type
     * @param jsonDeserializationContext
     * @return Map with events for each season
     * @throws JsonParseException
     */
    @Override
    public Map<Season, List<Event>> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        Map<Season, List<Event>> events = new HashMap<>();

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

    /**
     * custom deserialize for an Event
     * @param obj
     * @param context
     * @return Event deserialized
     */
    private Event deserializeEvent(JsonObject obj, JsonDeserializationContext context) {
        String name = obj.get("name").getAsString();
        List<Season> seasons = deserializeSeasons(obj.get("seasons").getAsJsonArray(), context);
        List<Choice> choices = new ArrayList<>();
        obj.get("choices").getAsJsonArray().forEach(choice -> choices.add(deserializeChoice(choice.getAsJsonObject(), context)));

        return new Event(name, seasons, choices);
    }

    /**
     * custom deserialize for a seasons
     * @param array
     * @param context
     * @return List of seasons
     */
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

    /**
     * custom deserialize for a choice
     * @param obj
     * @param context
     * @return Choice deserialized
     */
    private Choice deserializeChoice(JsonObject obj, JsonDeserializationContext context) {
        String label = obj.get("label").getAsString();
        JsonObject effects = obj.getAsJsonObject("effects");
        JsonElement nextElement = obj.get("next");
        Event next = null;

        if (!nextElement.isJsonNull()) {
            next = deserializeEvent(nextElement.getAsJsonObject(), context);
        }
        return new Choice(label, deserializeEffects(effects, context), next);
    }

    /**
     * custom deserialize for effects of a choice
     * @param obj
     * @param context
     * @return Effects deserialized
     */
    private Effects deserializeEffects(JsonObject obj, JsonDeserializationContext context) {
        Effects effects = new Effects();

        Type factionType = new TypeToken<Map<String, Integer>>(){}.getType();
        Type resourceType = new TypeToken<Map<String, Integer>>(){}.getType();

        Map<String, Integer> factions = context.deserialize(obj.get("factions"), factionType);
        Map<String, Integer> resource = context.deserialize(obj.get("resources"), resourceType);

        if (factions == null) factions = new HashMap<>();
        if (resource == null) resource = new HashMap<>();

        factions.forEach(effects::addFaction);
        resource.forEach(effects::addResource);

        return effects;
    }

}
