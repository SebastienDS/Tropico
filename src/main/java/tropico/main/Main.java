package tropico.main;

import tropico.Faction;
import tropico.GameState;
import tropico.Player;
import tropico.events.Choice;
import tropico.events.Event;
import tropico.utils.Backup;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
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
		System.out.println("1) Nouvelle partie \n2) Charger partie");

		int input = getInt(sc, -1, MENU_CHOICES.size());
		if (input == -1 || input == 0)
			System.exit(0);

		return MENU_CHOICES.get(input - 1).call();
	}

	/**
	 * main loop for the game
	 * 
	 * @param sc   the scanner used to interact with the user
	 * @param game the GameState contains all the informations on the game
	 */
	public static void mainLoop(Scanner sc, GameState game) throws IOException {

		Event event;

		List<Choice> eventChoices;

		while (true) {
			// TODO only doable events
			event = game.getNewEvent();
			eventChoices = event.getChoices();
			ArrayList<String> choicesStr = getChoices();

			if (actionChoice(sc, game, event, choicesStr.get(0))) {
				return;
			}

			int input = getInt(sc, 1, eventChoices.size()) - 1;
			Choice choice = eventChoices.get(input);
			choice.forEach(System.out::println);
			choice.choose(game.getPlayer());

			if (game.isGameOver()) {
				System.out.println("\nDéfaite ... Vous avez tenu " + (game.getTurn() - 1) + " tours.");
				return;
			}

			if (game.isEndOfYear() && endOfYear(sc, game, choicesStr.get(1))) {
				return;
			}

			game.nextTurn();

		}
	}

	private static ArrayList<String> getChoices() {
		ArrayList<String> choicesStr = new ArrayList<String>();

		StringBuilder gameChoices = new StringBuilder();
		gameChoices.append("0) Sauvegarder la partie\n");
		gameChoices.append("1) Voir les détails des factions\n");
		gameChoices.append("2) Voir les ressources\n");
		gameChoices.append("3) Voir l'évennement\n");
		gameChoices.append("4) Choisir une action");

		choicesStr.add(gameChoices.toString());

		StringBuilder endOfYearChoices = new StringBuilder();
		endOfYearChoices.append("0) Sauvegarder la partie\n");
		endOfYearChoices.append("1) Voir les détails des factions\n");
		endOfYearChoices.append("2) Voir les ressources\n");
		endOfYearChoices.append("3) Effectuer un pot de vin\n");
		endOfYearChoices.append("4) Acheter de la nourriture\n");
		endOfYearChoices.append("5) Mettre fin à l'année");

		choicesStr.add(endOfYearChoices.toString());

		return choicesStr;
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
	private static GameState loadGame() throws IOException, ClassNotFoundException {
		return (GameState) Backup.loadObject("backup/save");
	}

	/**
	 * save the game
	 * 
	 * @param game
	 */
	private static void saveGame(GameState game) throws IOException {
		Backup.saveObject("backup/save", game);
	}

	/**
	 * Private method used to ask the player the action he wants to make until he
	 * chooses to quit or make a decision for the current event
	 * 
	 * @param sc      The scanner to get the player's choice
	 * @param game    The GameState
	 * @param event   The current event
	 * @param choices The String containing the description of the possible choices
	 * @return Returns a boolean telling if whether or not the game has to stop
	 * @throws IOException
	 */
	private static boolean actionChoice(Scanner sc, GameState game, Event event, String choices) throws IOException {
		int input;

		System.out.println("\n" + choices);
		input = getInt(sc, -1, 4);
		switch (input) {
		case -1: {
			return true;
		}
		case 0: {
			saveGame(game);
			System.out.println("Jeu sauvegardé avec succès.");
			return actionChoice(sc, game, event, choices);
		}
		case 1: {
			List<Faction> factions = game.getPlayer().getFactions();

			factions.forEach(System.out::println);
			return actionChoice(sc, game, event, choices);
		}
		case 2: {
			System.out.println(game.getPlayer().getResourcesAsString());
			return actionChoice(sc, game, event, choices);
		}
		case 3: {
			System.out.println(event);
			return actionChoice(sc, game, event, choices);
		}
		case 4: {
			System.out.println(event);
			break;
		}
		}

		return false;
	}

	private static boolean endOfYear(Scanner sc, GameState game, String choices) throws IOException {
		if (!game.isEndOfYear()) {
			throw new IllegalStateException("Fin d'année au mauvais moment");
		}
		if (endOfYearChoice(sc, game, choices)) {
			return true;
		}

		String str = game.getPlayer().generateResources();
		System.out.println("C'est la fin de l'année ! Voici les conséquences :\n" + str);

		if (game.isGameOver()) {
			System.out.println("\nDéfaite ... Vous avez tenu " + (game.getTurn()) + " tours.");
			return true;
		}

		return false;
	}

	private static boolean endOfYearChoice(Scanner sc, GameState game, String choices) throws IOException {
		int input;
		Player p = game.getPlayer();

		System.out.println("\n" + choices);

		System.out.println("\nVous possédez actuellement " + p.getTreasury() + "$");
		input = getInt(sc, -1, 5);

		switch (input) {
		case -1: {
			return true;
		}
		case 0: {
			saveGame(game);
			System.out.println("Jeu sauvegardé avec succès.");
			return endOfYearChoice(sc, game, choices);
		}
		case 1: {
			List<Faction> factions = p.getFactions();

			factions.forEach(System.out::println);
			return endOfYearChoice(sc, game, choices);
		}
		case 2: {
			System.out.println(p.getResourcesAsString());
			return endOfYearChoice(sc, game, choices);
		}
		case 3: {
			bribeChoice(sc, game);
			return endOfYearChoice(sc, game, choices);
		}
		case 4: {
			marketChoice(sc, game);
			return endOfYearChoice(sc, game, choices);
		}
		}

		return false;
	}

	private static void bribeChoice(Scanner sc, GameState game) {
		Player p = game.getPlayer();
		List<Faction> factions = p.getSatisfiedFactions();
		StringBuilder choices = new StringBuilder("0) Retour");
		int len = factions.size();

		for (int i = 0; i < len; i++) {
			choices.append("\n" + (i + 1) + ") " + factions.get(i) + " coût : " + factions.get(i).getBribeCost() + "$");
		}

		System.out.println(choices);

		int input;
		if ((input = getInt(sc, 0, len)) == 0) {
			return;
		}

		Faction f = factions.get(input - 1);

		if (!p.bribe(f)) {
			System.out.println("Vous n'avez pas assez d'argent pour donner un pot de vin à cette faction !");
			bribeChoice(sc, game);
		}

	}

	private static void marketChoice(Scanner sc, GameState game) {
		Player p = game.getPlayer();
		int max = p.getTreasury() / 8;

		if (max == 0) {
			System.out.println("Vous n'avez pas assez d'argent pour acheter de la nourriture.");
			return;
		}

		System.out.println("Vous avez actuellement " + p.getFoodUnit() + " unités de nourriture.");
		System.out.println("Combien souhaitez-vous en acheter ? (max:" + max + ")");

		int input = getInt(sc, 0, max);

		if (input == 0) {
			return;
		}

		p.buyFood(input);
	}

}
