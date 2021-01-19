package tropico.events;

import tropico.Player;

import java.io.Serializable;

public interface Effect extends Serializable {

	/**
	 * Getter for effect's value
	 *
	 * @return The value of the effect
	 */
	int getValue();

	@Override
	String toString();

	/**
	 * Apply effect to the Player p
	 *
	 * @param p The player to apply the effect
	 */
	void applyEffect(Player p);
}
