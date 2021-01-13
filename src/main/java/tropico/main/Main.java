package tropico.main;

import tropico.GameState;
import tropico.Player;
import tropico.events.Choice;
import tropico.events.Event;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public class Main {

    private static List<Callable<GameState>> MENU_CHOICES = List.of(Main::newGame, Main::loadGame);


    public static void main(String[] args) throws Exception {

        try (Scanner sc = new Scanner(System.in)) {
            GameState game = menu(sc);
            mainLoop(sc, game);
        }
    }

    /**
     * menu of the game
     * @param sc
     * @return GameState
     * @throws Exception
     */
    public static GameState menu(Scanner sc) throws Exception {
        System.out.println("0) Quitter \n1) Nouvelle partie \n");

        int input = getInt(sc, -1, MENU_CHOICES.size());
        if (input == -1) System.exit(0);

        return MENU_CHOICES.get(input).call();
    }

    /**
     * main loop for the game
     * @param sc
     * @param game
     */
    public static void mainLoop(Scanner sc, GameState game) {
        System.out.println("Entrer 1 afin de sauvegarder la partie en cours.");

        int input;
        Player player;
        Event event;

        while (true) {
            player = game.getPlayer();
            // print game
            // get event filtered with choices doable
            event = game.getNewEvent();
            List<Choice> choices = event.getChoices();

            input = getInt(sc, 0, choices.size());
            if (input == 0) {
                saveGame(game);
                return;
            }

            // execute choice
//            choices.get(input).execute();

            // next SEASON
            // check end of year
            // check game over
            // next player
            game.nextTurn();
        }
    }

    /**
     * get an integer between min and max
     * @param sc
     * @param min
     * @param max
     * @return int in the interval
     */
    private static int getInt(Scanner sc, int min, int max) {
        int input;
        do {
            input = getInt(sc);
        } while (input < min || input > max);
        return input;
    }

    /**
     * get an integer
     * @param sc
     * @return int
     */
    private static int getInt(Scanner sc) {
        while (!sc.hasNextInt()) {
            sc.next();
            System.out.println("Saisie incorrect");
        }
        return sc.nextInt();
    }

    /**
     * create a new Game
     * @return new game
     * @throws FileNotFoundException
     */
    private static GameState newGame() throws FileNotFoundException {
        return new GameState();
    }

    /**
     * load a Game from a save
     * @return game loaded
     */
    private static GameState loadGame() {
        return null;
    }

    /**
     * save the game
     * @param game
     */
    private static void saveGame(GameState game) {

    }

}
