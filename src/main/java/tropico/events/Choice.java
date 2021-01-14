package tropico.events;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import tropico.Player;

public class Choice implements Iterable<Effect>, Serializable {

    private static final long serialVersionUID = 1L;

    private final String label;
    private final ArrayList<Effect> effects;
    private final Event next;


    public Choice(String label, ArrayList<Effect> effects, Event next) {
        this.label = label;
        this.effects = effects;
        this.next = next;
    }

    @Override
    public String toString() {
        return label;
    }

    public String getLabel() {
        return label;
    }
    
    public boolean hasNextEvent() {
    	return next != null;
    }
    
    public Event getNextEvent() {
    	return next;
    }

    public Event choose(Player p) {
    	for (Effect effect : effects) {
			effect.applyEffect(p);
		}
    	return next;
    }

    /**
     * Makes Choice iterable on his effects
     * @return Iterator<Effect>
     */
    @Override
    public Iterator<Effect> iterator() {
        return effects.iterator();
    }
}
