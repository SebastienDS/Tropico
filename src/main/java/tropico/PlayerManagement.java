package tropico;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import tropico.utils.UtilsDeserialization;

public class PlayerManagement implements Serializable {

	private static final long serialVersionUID = 1L;

	private final List<Player> players = new ArrayList<>();
	private int currentPlayer = 0;

	public PlayerManagement(String path, int count) throws FileNotFoundException {
		if (count <= 0)
			throw new IllegalStateException("Must have players");
		
		List<Faction> factions = UtilsDeserialization.loadFactions(path + "factions.json");
		Resources resources = UtilsDeserialization.loadResources(path + "resources.json");

		for (int i = 0; i < count; i++) {
			players.add(new Player(factions, resources));
		}
	}

	public PlayerManagement(String path) throws FileNotFoundException {
		this(path, 1);
	}

	public Player getPlayer() {
		return players.get(currentPlayer);
	}

	/**
	 * set the next player
	 */
	public void nextTurn() {
		currentPlayer = (currentPlayer + 1) % players.size();
	}

	/**
	 * Searches if a player is dead
	 * 
	 * @return Returns true if a player lost, false if not
	 */
	public boolean havePlayerDead() {
		for (Player player : players) {
			if (player.isDead()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "PlayerManagement{" + "players=" + players + ", currentPlayer=" + currentPlayer + '}';
	}
}
