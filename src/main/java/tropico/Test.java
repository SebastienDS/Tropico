package tropico;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import tropico.events.Event;
import tropico.events.EventsDeserializer;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class Test {

    public static void main(String[] args) throws FileNotFoundException {
        Type eventType = new TypeToken<Map<Season, List<Event>>>(){}.getType();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(eventType, new EventsDeserializer())
                .create();

        Map<Season, List<Event>> eventsMap = gson.fromJson(new JsonReader(new FileReader("src/test.json")), eventType);

        eventsMap.forEach((k, v) -> System.out.printf("%s : %s | ", k, v));

        System.out.println();
        System.out.println(eventsMap.get(Season.AUTUMN));
    }
}
