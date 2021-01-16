package tropico.utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import tropico.events.*;
import tropico.DifficultySingleton;
import tropico.Faction;
import tropico.Season;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UtilsDeserialization implements JsonDeserializer<Map<Season, List<Event>>> {
	
	private List<Faction> factions;

	public UtilsDeserialization(List<Faction> factions) {
		super();
		this.factions = factions;
	}

	/**
	 * Custom deserialize for events
	 * 
	 * @param jsonElement
	 * @param type
	 * @param jsonDeserializationContext
	 * @return Map with events for each season
	 * @throws JsonParseException
	 */
	@Override
	public Map<Season, List<Event>> deserialize(JsonElement jsonElement, Type type,
			JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		Map<Season, List<Event>> events = new HashMap<>();

		JsonArray jsonArray = jsonElement.getAsJsonArray();
		for (JsonElement element : jsonArray) {
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
	 * 
	 * @param obj
	 * @param context
	 * @return Event deserialized
	 */
	private Event deserializeEvent(JsonObject obj, JsonDeserializationContext context) {
		String name = obj.get("name").getAsString();
		List<Season> seasons;

		if (obj.has("seasons")) {
			seasons = deserializeSeasons(obj.get("seasons").getAsJsonArray(), context);
		} else {
			seasons = Arrays.asList(Season.values());
		}

		List<Choice> choices = new ArrayList<>();
		obj.get("choices").getAsJsonArray()
				.forEach(choice -> choices.add(deserializeChoice(choice.getAsJsonObject(), context)));

		return new Event(name, seasons, choices);
	}

	/**
	 * custom deserialize for a seasons
	 * 
	 * @param array
	 * @param context
	 * @return List of seasons
	 */
	private List<Season> deserializeSeasons(JsonArray array, JsonDeserializationContext context) {
		List<Season> seasons = new ArrayList<>();

		Type seasonType = new TypeToken<Season>() {
		}.getType();

		array.forEach(elem -> {
			Season season = context.deserialize(elem, seasonType);
			if (!Season.contains(season))
				throw new IllegalStateException("Season " + season + " is not available");
			seasons.add(season);
		});
		return seasons;
	}

	/**
	 * custom deserialize for a choice
	 * 
	 * @param obj
	 * @param context
	 * @return Choice deserialized
	 */
	private Choice deserializeChoice(JsonObject obj, JsonDeserializationContext context) {
		String label = obj.get("label").getAsString();
		JsonArray effects = obj.getAsJsonArray("effects");
		Event next = null;

		// Verifying that next event exists
		if (obj.has("next") && !obj.get("next").isJsonNull()) {
			System.out.println(obj.get("next") + ":" + obj.has("next"));
			next = deserializeEvent(obj.get("next").getAsJsonObject(), context);
		}

		return new Choice(label, deserializeEffects(effects, context), next);
	}

	/**
	 * custom deserialize for effects of a choice
	 * 
	 * @param array
	 * @param context
	 * @return Effects deserialized
	 */
	private ArrayList<Effect> deserializeEffects(JsonArray array, JsonDeserializationContext context) {
		ArrayList<Effect> effects = new ArrayList<>();

		array.forEach(effect -> effects.addAll(deserializeEffect(effect.getAsJsonObject(), context)));
		return effects;
	}

	/**
	 * custom deserialize for effect
	 * 
	 * @param effect
	 * @param context
	 * @return Effect deserialize
	 * @throws IllegalStateException if type is invalid
	 */
	private Collection<Effect> deserializeEffect(JsonObject effect, JsonDeserializationContext context) {
		switch (effect.get("type").getAsString()) {
		case "satisfaction":
			return deserializeSatisfactionEffect(effect, context);
		case "supporter":
			return deserializeSupporterEffect(effect, context);
		case "resources":
			return List.of(deserializeResourcesEffect(effect, context));
		default:
			throw new IllegalStateException("Type invalide.");
		}
	}

	/**
	 * deserialize resource effect
	 * 
	 * @param effect
	 * @param context
	 * @return OtherEffect
	 */
	private Effect deserializeResourcesEffect(JsonObject effect, JsonDeserializationContext context) {
		Type resourceType = new TypeToken<OtherEffect.types>() {
		}.getType();
		OtherEffect.types type = context.deserialize(effect.get("resource"), resourceType);
		if (type == null)
			throw new IllegalStateException(effect.get("resource") + " is not a valid resource");
		int value = effect.get("value").getAsInt();
		float multiplier = 1;
		
		switch (DifficultySingleton.getDifficulty()) {
		case EASY:
			multiplier = 0.5f;
			break;
		case MEDIUM:
			multiplier = 1;
			break;
		case HARD:
			multiplier = 1.5f;
			break;

		}
		if (value < 0) {
			value *= multiplier;
		}

		return new OtherEffect(type, value);
	}

	/**
	 * deserialize Supporter effect
	 * 
	 * @param effect
	 * @param context
	 * @return SupporterNumberEffect
	 */
	private List<Effect> deserializeSupporterEffect(JsonObject effect, JsonDeserializationContext context) {
		String faction = effect.get("faction").getAsString();
		int value = effect.get("value").getAsInt();
		boolean percentage;
		if (effect.has("percentage")) {
			percentage = effect.get("percentage").getAsBoolean();
		} else {
			percentage = false;
		}

		if (faction.equals("all")) {
			List<Effect> effects = new ArrayList<Effect>();
			for (Faction f : factions) {
				effects.add(new SupporterNumberEffect(f.getName(), value, percentage));
			}
			
			return effects;
		}
		
		if (!existsFaction(faction)) {
			throw new IllegalArgumentException("La faction \"" + faction + "\" n'existe pas.");
		}

		return List.of(new SupporterNumberEffect(faction, value, percentage));
	}

	/**
	 * deserialize satisfaction effect
	 * 
	 * @param effect
	 * @param context
	 * @return FactionSatisfactionEffect
	 */
	private List<Effect> deserializeSatisfactionEffect(JsonObject effect, JsonDeserializationContext context) {
		String faction = effect.get("faction").getAsString();
		int value = effect.get("value").getAsInt();
		
		if (faction.equals("all")) {
			List<Effect> effects = new ArrayList<Effect>();
			for (Faction f : factions) {
				effects.add(new FactionSatisfactionEffect(f.getName(), value));
			}
			
			return effects;
		}
		
		if (!existsFaction(faction)) {
			throw new IllegalArgumentException("La faction \"" + faction + "\" n'existe pas.");
		}

		return List.of(new FactionSatisfactionEffect(faction, value));
	}
	
	private boolean existsFaction(String factionName) {
		for (Faction faction : factions) {
			if (faction.isName(factionName)) {
				return true;
			}
		}
		
		return false;
	}

	/**
	 * load factions from json file
	 *
	 * @param path The path where the json file is located
	 * @return List of faction
	 * @throws FileNotFoundException
	 */
	public static List<Faction> loadFactions(String path) throws FileNotFoundException {
		Type eventType = new TypeToken<List<Faction>>() {
		}.getType();

		Gson gson = new Gson();
		return gson.fromJson(new JsonReader(new FileReader(path)), eventType);
	}
}
