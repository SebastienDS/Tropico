package tropico;

import java.util.ArrayList;
import java.util.List;

public class PlayerManagement {

    private final List<Object> players = new ArrayList<>();
    private int currentPlayer = 0;

    public PlayerManagement(int count) {
        if (count <= 0) throw new IllegalStateException("Must have players");
        for (int i = 0; i < count; i++) {
            this.addPlayer();
        }
    }

    public PlayerManagement() {
        this(1);
    }

    public void addPlayer() {
        players.add(null);
    }

    public void getPlayer() {
//        return players.get(currentPlayer);
    }

    public void nextPlayer() {
        currentPlayer = (currentPlayer + 1) % players.size();
    }


}
