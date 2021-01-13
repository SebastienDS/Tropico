package tropico.events;

import tropico.Season;

import java.util.Iterator;
import java.util.List;

public class Event implements Iterable<Choice> {

    private final String name;
    private final List<Season> seasons;
    private final List<Choice> choices;

    public Event(String name, List<Season> seasons, List<Choice> choices) {
        this.name = name;
        this.seasons = seasons;
        this.choices = choices;
    }

    public List<Season> getSeasons() {
        return seasons;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Event{" +
                "name='" + name + '\'' +
                ", seasons=" + seasons +
                ", choices=" + choices +
                '}';
    }

    /**
     * make Event iterable on these choices
     * @return Iterator<Choice>
     */
    @Override
    public Iterator<Choice> iterator() {
        return choices.iterator();
    }
}
