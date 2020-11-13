package tropico.events;

import java.util.List;

public class Event {

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

    public List<Season> getSeasons() { return seasons; }

    @Override
    public String toString() {
        return "Event{" +
                "name='" + name + '\'' +
                ", seasons=" + seasons +
                ", choices=" + choices +
                ", next=" + next +
                '}';
    }
}
