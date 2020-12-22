package tropico;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import tropico.events.Event;
import tropico.events.EventsDeserializer;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;

public class Test {

    public static void main(String[] args) throws FileNotFoundException {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(HashMap.class, new EventsDeserializer())
                .create();

        HashMap<Season, List<Event>> eventsMap = gson.fromJson(new JsonReader(new FileReader("src/test.json")), HashMap.class);

        eventsMap.forEach((k, v) -> System.out.printf("%s : %s | ", k, v));

        System.out.println();
        System.out.println(eventsMap.get(Season.AUTUMN));
    }
}
