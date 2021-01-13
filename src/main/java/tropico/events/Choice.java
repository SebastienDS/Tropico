package tropico.events;

import java.util.ArrayList;

import tropico.Player;

public class Choice {

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
    	return !next.equals(null);
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

}
