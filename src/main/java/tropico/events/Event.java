package tropico.events;

import tropico.Season;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class Event implements Iterable<Choice>, Serializable {

    private static final long serialVersionUID = 1L;

    private final String name;
    private final List<Season> seasons;
    private final List<Choice> choices;

    public Event(String name, List<Season> seasons, List<Choice> choices) {
        this.name = name;
        this.seasons = Objects.requireNonNull(seasons);
        this.choices = Objects.requireNonNull(choices);
    }

    public List<Season> getSeasons() {
        return List.copyOf(seasons);
    }

    public List<Choice> getChoices() {
        return List.copyOf(choices);
    }

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
     * make Event iterable on these choices
     * @return Iterator<Choice>
     */
    @Override
    public Iterator<Choice> iterator() {
        return choices.iterator();
    }
}
