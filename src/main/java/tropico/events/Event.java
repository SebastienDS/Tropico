package tropico.events;

import tropico.Season;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

public class Event implements Iterable<Choice>, Serializable {

    private final String name;
    private final List<Season> seasons;
    private final List<Choice> choices;

    public Event(String name, List<Season> seasons, List<Choice> choices) {
        this.name = name;
        this.seasons = seasons;
        this.choices = choices;
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
			str.append("\n" + (i+1) + ") " + choices.get(i));
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
