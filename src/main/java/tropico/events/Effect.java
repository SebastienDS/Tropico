package tropico.events;

import tropico.Player;

public interface Effect {
	
	public int getValue();

	@Override
	public String toString();
	
	public void applyEffect(Player p);
}
