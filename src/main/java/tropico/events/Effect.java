package tropico.events;

import tropico.Player;

import java.io.Serializable;

public interface Effect extends Serializable {
	
	public int getValue();

	@Override
	public String toString();
	
	public void applyEffect(Player p);
}
