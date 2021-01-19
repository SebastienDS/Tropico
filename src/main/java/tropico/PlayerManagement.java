package tropico;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import tropico.utils.UtilsDeserialization;

/**
 * This class serves as a way to manage the players, using :
 * <ul>
 * <li>A list of all the players</li>
 * <li>The current player</li>
 * </ul>
 * 
 * @author Corentin OGER & Sébastien DOS SANTOS
 *
 */
public class PlayerManagement implements Serializable {

	/**
	 * Necessary field to avoid warning while implementing Serializable.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The list of all the players of a game.
	 */
	private final List<Player> players = new ArrayList<>();

	/**
	 * An int to know who is the player that plays this turn. Doesn't have a lot of
	 * utility outside of this class.
	 */
	private int currentPlayer = 0;

	/**
	 * <b>PlayerManagement's first constructor</b>
	 * 
	 * Creates the players' list and the base factions and resources using
	 * UtilsDeserialization class.
	 * 
	 * @param path  A String containing the path to the json files of the factions
	 *              and the resources.
	 * @param count An int representing the number of players in this game.
	 * @throws FileNotFoundException Throws a FileNotFoundException if the path is
	 *                               wrong.
	 */
	public PlayerManagement(String path, int count) throws FileNotFoundException {
		if (count <= 0)
			throw new IllegalStateException("Must have players");

		// Deserializes the factions and resources, which is the same for all players at
		// the start
		List<Faction> factions = UtilsDeserialization.loadFactions(path + "factions.json");
		Resources resources = UtilsDeserialization.loadResources(path + "resources.json");

		for (int i = 0; i < count; i++) {
			players.add(new Player("Player " + (i + 1), List.copyOf(factions), resources.copy()));
		}
	}

	/**
	 * <b>PlayerManagement's second constructor</b>
	 * 
	 * Use this constructor if you want to create a solo game, but choose the
	 * gamemode.
	 * 
	 * @param path A String containing the path to the json files of the factions
	 *             and the resources.
	 * @throws FileNotFoundException Throws a FileNotFoundException if the path is
	 *                               wrong.
	 */
	public PlayerManagement(String path) throws FileNotFoundException {
		this(path, 1);
	}

	/**
	 * This method gives you the current player using the currentPlayer field.
	 * 
	 * @return Returns the current player.
	 */
	public Player getPlayer() {
		return players.get(currentPlayer);
	}

	/**
	 * Getter for the field currentPlayer.
	 * 
	 * @return Returns the index of the current player in the list of players.
	 */
	public int getCurrentPlayer() {
		return currentPlayer;
	}

	/**
	 * Use this method when a turn is over. In this case, the field currentPlayer is
	 * incremented or goes back to zero to change the current player.
	 */
	public void nextTurn() {
		currentPlayer = (currentPlayer + 1) % players.size();
	}

	/**
	 * Searches if a player is dead.
	 * 
	 * @return Returns true if a player lost, false otherwise.
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
