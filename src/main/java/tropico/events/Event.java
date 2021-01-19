package tropico.events;

import tropico.Season;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class Event implements Iterable<Choice>, Serializable {

    /**
     * Necessary field to avoid warning while implementing Serializable
     */
    private static final long serialVersionUID = 1L;

    /**
     * The name of the Event
     */
    private final String name;

    /**
     * The seasons of the Event
     */
    private final List<Season> seasons;

    /**
     * The choices of the Event
     */
    private final List<Choice> choices;

    /**
     * <b>Event's constructor</b>
     *
     * Creates an event object, requiring the name, seasons and choices.
     *
     * @param name          The name of the Event
     * @param seasons       The seasons of the Event
     * @param choices       The choices of the Event
     */
    public Event(String name, List<Season> seasons, List<Choice> choices) {
        this.name = name;
        this.seasons = Objects.requireNonNull(seasons);
        this.choices = Objects.requireNonNull(choices);
    }

    /**
     * Getter for the field seasons.
     *
     * @return The event's seasons.
     */
    public List<Season> getSeasons() {
        return List.copyOf(seasons);
    }

    /**
     * Getter for the field choices.
     *
     * @return The event's choices.
     */
    public List<Choice> getChoices() {
        return List.copyOf(choices);
    }

    /**
     * Getter for the field name.
     *
     * @return The event's name.
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
    	StringBuilder str = new StringBuilder(name);
    	for (int i = 0; i < choices.size(); i++) {
			str.append("\n").append(i + 1).append(") ").append(choices.get(i));
		}
        return str.toString();
    }

    /**
     * Make Event iterable on these choices
     *
     * @return Iterator<Choice>
     */
    @Override
    public Iterator<Choice> iterator() {
        return choices.iterator();
    }
}
