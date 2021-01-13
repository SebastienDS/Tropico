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
        JsonArray effects = obj.getAsJsonArray("effects");
        JsonElement nextElement = obj.get("next");
        Event next = null;

        if (!nextElement.isJsonNull()) {
            next = deserializeEvent(nextElement.getAsJsonObject(), context);
        }
        return new Choice(label, deserializeEffects(effects, context), next);
    }

    /**
     * custom deserialize for effects of a choice
     * @param array
     * @param context
     * @return Effects deserialized
     */
    private ArrayList<Effect> deserializeEffects(JsonArray array, JsonDeserializationContext context) {
    	ArrayList<Effect> effects = new ArrayList<>();

    	array.forEach(effect -> effects.add(deserializeEffect(effect.getAsJsonObject(), context)));
        return effects;
    }

    /**
     * custom deserialize for effect
     * @param effect
     * @param context
     * @return Effect deserialize
     * @throws IllegalStateException if type is invalid
     */
    private Effect deserializeEffect(JsonObject effect, JsonDeserializationContext context) {
        switch (effect.get("type").getAsString()) {
            case "satisfaction":
                return deserializeSatisfactionEffect(effect, context);
            case "supporter":
                return deserializeSupporterEffect(effect,context);
            case "resources":
                return deserializeResourcesEffect(effect, context);
        }
        throw new IllegalStateException("type invalid");
    }

    /**
     * deserialize resource effect
     * @param effect
     * @param context
     * @return OtherEffect
     */
    private Effect deserializeResourcesEffect(JsonObject effect, JsonDeserializationContext context) {
        Type resourceType = new TypeToken<OtherEffect.types>(){}.getType();
        OtherEffect.types type = context.deserialize(effect.get("resource"), resourceType);
        if (type == null) throw new IllegalStateException(effect.get("resource") + " is not a valid resource");
        int value = effect.get("value").getAsInt();

        return new OtherEffect(type, value);
    }

    /**
     * deserialize Supporter effect
     * @param effect
     * @param context
     * @return SupporterNumberEffect
     */
    private Effect deserializeSupporterEffect(JsonObject effect, JsonDeserializationContext context) {
        String faction = effect.get("faction").getAsString();
        int value = effect.get("value").getAsInt();
        boolean percentage = effect.get("percentage").getAsBoolean();

        return new SupporterNumberEffect(faction, value, percentage);
    }

    /**
     * deserialize satisfaction effect
     * @param effect
     * @param context
     * @return FactionSatisfactionEffect
     */
    private Effect deserializeSatisfactionEffect(JsonObject effect, JsonDeserializationContext context) {
        String faction = effect.get("faction").getAsString();
        int value = effect.get("value").getAsInt();

        return new FactionSatisfactionEffect(faction, value);
    }


}
