package tropico;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PlayerManagement implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<Player> players = new ArrayList<>();
    private int currentPlayer = 0;

    public PlayerManagement(int count) throws FileNotFoundException {
        if (count <= 0) throw new IllegalStateException("Must have players");
        for (int i = 0; i < count; i++) {
            this.addPlayer();
        }
    }

    public PlayerManagement() throws FileNotFoundException {
        this(1);
    }

    /**
     * add new player
     * @throws FileNotFoundException
     */
    public void addPlayer() throws FileNotFoundException {
        players.add(new Player());
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
     * get if a player is dead
     * @return true if a player is dead
     */
    public boolean havePlayerDead() {
        for (Player player: players) {
            if (player.isDead()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "PlayerManagement{" +
                "players=" + players +
                ", currentPlayer=" + currentPlayer +
                '}';
    }
}
