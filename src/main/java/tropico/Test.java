package tropico;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import tropico.events.Event;
import tropico.utils.UtilsDeserialization;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class Test {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
    	Type eventType = new TypeToken<List<Event>>(){}.getType();

    	Gson gson = new GsonBuilder()
    			.registerTypeAdapter(eventType, new UtilsDeserialization(UtilsDeserialization.loadFactions("src/main/resources/factions.json")))
    			.create();

    	List<Event> events = gson.fromJson(new JsonReader(new FileReader("src/main/resources/scenario/test.json")), eventType);

		System.out.println(events);

    }
}
