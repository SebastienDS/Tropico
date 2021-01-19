package tropico.utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import tropico.events.*;
import tropico.DifficultySingleton;
import tropico.Faction;
import tropico.Resources;
import tropico.Season;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.*;

/**
 * This class contains methods to deserialize a json.
 * 
 * @author Corentin OGER and Sébastien DOS SANTOS
 *
 */
public class UtilsDeserialization implements JsonDeserializer<List<Event>> {

	/**
	 * The list of factions, so you can check there isn't a faction that will cause
	 * an error later.
	 */
	private List<Faction> factions;

	/**
	 * <b>UtilsDeserialization's constructor</b>
	 * 
	 * Takes a list of factions that shouldn't be null.
	 * 
	 * @param factions The list containing all of the factions of the current
	 *                 scenario.
	 */
	public UtilsDeserialization(List<Faction> factions) {
		this.factions = Objects.requireNonNull(factions);
	}

	/**
	 * Custom deserializer for events. Reads a json file and extracts a list of
	 * Events.
	 * 
	 * @param jsonElement
	 * @param type
	 * @param jsonDeserializationContext
	 * @return Returns a List of all the events.
	 * @throws JsonParseException
	 */
	@Override
	public List<Event> deserialize(JsonElement jsonElement, Type type,
			JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		List<Event> events = new ArrayList<>();

		JsonArray jsonArray = jsonElement.getAsJsonArray();
		for (JsonElement element : jsonArray) {
			events.add(deserializeEvent(element.getAsJsonObject(), jsonDeserializationContext));
		}
		return events;
	}

	/**
	 * Deserializes a JsonObject as an event. Checks if optionnal fields are
	 * missing, and creates default variables if so.
	 * 
	 * @param obj The JsonObject supposed to be an Event.
	 * @param context
	 * @return Returns an Event.
	 */
	private Event deserializeEvent(JsonObject obj, JsonDeserializationContext context) {
		String name = obj.get("name").getAsString();
		List<Season> seasons;

		// If season isn't here, the event can occur during any of them
		if (obj.has("seasons")) {
			seasons = deserializeSeasons(obj.get("seasons").getAsJsonArray(), context);
		} else {
			seasons = Arrays.asList(Season.values());
		}

		// Calls the method to deserialize the choices
		List<Choice> choices = new ArrayList<>();
		obj.get("choices").getAsJsonArray()
				.forEach(choice -> choices.add(deserializeChoice(choice.getAsJsonObject(), context)));

		return new Event(name, seasons, choices);
	}

	/**
	 * Deserializes a JsonArray as a list of seasons. The array is supposed to be a list of seasons.
	 * 
	 * @param array The JsonArray supposed to be a list of seasons.
	 * @param context
	 * @return Returns a List of Seasons.
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
	 * Deserializes a JsonObject as a Choice. Replaces optional field by default inputs.
	 * 
	 * @param obj The JsonObject supposed to be a Choice.
	 * @param context
	 * @return Returns a Choice object.
	 */
	private Choice deserializeChoice(JsonObject obj, JsonDeserializationContext context) {
		String label = obj.get("label").getAsString();
		JsonArray effects = obj.getAsJsonArray("effects");
		Event next = null;

		// Verifying that next event exists
		if (obj.has("next") && !obj.get("next").isJsonNull()) {
			next = deserializeEvent(obj.get("next").getAsJsonObject(), context);
		}

		return new Choice(label, deserializeEffects(effects, context), next);
	}

	/**
	 * Deserializes a JsonArray as an ArrayList of Effects.
	 * 
	 * @param array The JsonArray supposed to be a list of effects.
	 * @param context
	 * @return Returns an ArrayList of Effects.
	 */
	private ArrayList<Effect> deserializeEffects(JsonArray array, JsonDeserializationContext context) {
		ArrayList<Effect> effects = new ArrayList<>();

		// Calls another method to deserialize one event
		array.forEach(effect -> effects.addAll(deserializeEffect(effect.getAsJsonObject(), context)));
		return effects;
	}

	/**
	 * Deserializes a JsonObject as a Collection of Effects.
	 * 
	 * @param effect The JsonObject supposed to be an effect.
	 * @param context
	 * @return Returns an Effect.
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
	 * Deserializes resource effect.
	 * 
	 * @param effect
	 * @param context
	 * @return Returns an OtherEffect.
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
	 * Deserializes Supporter effect. Can return multiple if "faction" equals "all".
	 * 
	 * @param effect
	 * @param context
	 * @return Returns a SupporterNumberEffect.
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
	 * Deserializes satisfaction effect. Can return multiple if "faction" equals "all".
	 * 
	 * @param effect
	 * @param context
	 * @return Returns a lis FactionSatisfactionEffect.
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
	 * Loads factions from a json file.
	 *
	 * @param path The path where the json file is located.
	 * @return Returns a List of faction.
	 * @throws FileNotFoundException
	 */
	public static List<Faction> loadFactions(String path) throws FileNotFoundException {
		Type eventType = new TypeToken<List<Faction>>() {
		}.getType();

		Gson gson = new Gson();
		return gson.fromJson(new JsonReader(new FileReader(path)), eventType);
	}

	/**
	 * Loads resources from a json file.
	 *
	 * @param path The path where the json file is located.
	 * @return Returns a Resources object.
	 * @throws FileNotFoundException
	 */
	public static Resources loadResources(String path) throws FileNotFoundException {
		Type eventType = new TypeToken<Resources>() {
		}.getType();

		Gson gson = new Gson();
		return gson.fromJson(new JsonReader(new FileReader(path)), eventType);
	}
}
