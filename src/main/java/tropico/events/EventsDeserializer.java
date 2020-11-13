package tropico.events;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;

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
        obj.get("choices").getAsJsonArray().forEach(choice -> choices.add(deserializeChoice(choice.getAsJsonObject())));
        JsonElement nextElement = obj.get("next");
        Event next = null;

        if (!nextElement.isJsonNull()) {
            next = deserializeEvent(nextElement.getAsJsonObject(), context);
        }

        return new Event(name, seasons, choices, next);
    }

    private List<Season> deserializeSeasons(JsonArray array, JsonDeserializationContext context) {
        List<Season> seasons = new ArrayList<>();

        array.forEach(elem -> {
            Season season = context.deserialize(elem, Season.class);
            if (!Season.contains(season)) throw new IllegalStateException("Season " + season + " is not available");
            seasons.add(season);
        });
        return seasons;
    }

    private Choice deserializeChoice(JsonObject obj) {
        String label = obj.get("label").getAsString();
        JsonObject effects = obj.get("effects").getAsJsonObject();
        return new Choice(label, deserializeEffects(effects));
    }

    private Effects deserializeEffects(JsonObject obj) {
        Effects effects = new Effects();

        for (Map.Entry<String, JsonElement> entry: obj.entrySet()) {
            String effect = entry.getKey();
            Integer value = entry.getValue().getAsInt();
            effects.put(effect, value);
        }
        return effects;
    }
}
