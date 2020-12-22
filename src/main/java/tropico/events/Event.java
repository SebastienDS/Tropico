package tropico.events;

import tropico.Season;

import java.util.Iterator;
import java.util.List;

public class Event implements Iterable<Choice> {

    private final String name;
    private final List<Season> seasons;
    private final List<Choice> choices;
    private final Event next;

    public Event(String name, List<Season> seasons, List<Choice> choices, Event next) {
        this.name = name;
        this.seasons = seasons;
        this.choices = choices;
        this.next = next;
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
                ", next=" + next +
                '}';
    }

    @Override
    public Iterator<Choice> iterator() {
        return choices.iterator();
    }
}
