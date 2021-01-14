package tropico.main;

import tropico.Faction;
import tropico.GameState;
import tropico.events.Choice;
import tropico.events.Event;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;

public class Main {

	private static List<Callable<GameState>> MENU_CHOICES = List.of(Main::newGame, Main::loadGame);

	public static void main(String[] args) throws Exception {

		try (Scanner sc = new Scanner(System.in)) {
			GameState game = menu(sc);
			mainLoop(sc, game);
		}
	}

	/**
	 * Menu of the game
	 * 
	 * @param sc the scanner used to interact with the user
	 * @return GameState a new GameState
	 * @throws Exception
	 */
	public static GameState menu(Scanner sc) throws Exception {
		System.out.println("-1) Quitter \n0) Nouvelle partie \n");

		int input = getInt(sc, -1, MENU_CHOICES.size());
		if (input == -1)
			System.exit(0);

		return MENU_CHOICES.get(input).call();
	}

	/**
	 * main loop for the game
	 * 
	 * @param sc the scanner used to interact with the user
	 * @param game the GameState contains all the informations on the game
	 */
	public static void mainLoop(Scanner sc, GameState game) {

		Event event;

		StringBuilder choices = new StringBuilder();
		choices.append("0) Sauvegarder la partie\n");
		choices.append("1) Voir les détails des factions\n");
		choices.append("2) Voir les ressources\n");
		choices.append("3) Voir l'évennement\n");
		choices.append("4) Choisir une action");

		List<Choice> eventChoices;

		while (true) {
			// TODO only doable events
			event = game.getNewEvent();
			eventChoices = event.getChoices();

			if (actionChoice(game, sc, event, choices.toString())) {
				return;
			}

			int input = getInt(sc, 1, eventChoices.size()) - 1;
			Choice choice = eventChoices.get(input);
			choice.forEach(effect -> System.out.println(effect));
			choice.choose(game.getPlayer());

			// check game over
			game.nextTurn();

		}
	}

	/**
	 * get an integer between min and max
	 * 
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
	 * 
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
	 * 
	 * @return new game
	 * @throws FileNotFoundException
	 */
	private static GameState newGame() throws FileNotFoundException {
		return new GameState();
	}

	/**
	 * load a Game from a save
	 * 
	 * @return game loaded
	 */
	private static GameState loadGame() {
		// TODO
		return null;
	}

	/**
	 * save the game
	 * 
	 * @param game
	 */
	private static void saveGame(GameState game) {
		// TODO
	}

	private static boolean actionChoice(GameState game, Scanner sc, Event event, String choices) {
		int input;

		System.out.println("\n" + choices);
		input = getInt(sc, 0, 4);
		switch (input) {
		case 0: {
			saveGame(game);
			return true;
		}
		case 1: {
			List<Faction> factions = game.getPlayer().getFactions();
			for (Faction faction : factions) {
				System.out.println(faction);
			}
			actionChoice(game, sc, event, choices);
			break;
		}
		case 2: {
			System.out.println(game.getPlayer().getResourcesAsString());
			actionChoice(game, sc, event, choices);
			break;
		}
		case 3: {
			System.out.println(event);
			actionChoice(game, sc, event, choices);
			break;
		}
		case 4: {
			System.out.println(event);
			break;
		}
		}
		
		return false;
	}

}
