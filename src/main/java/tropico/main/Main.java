package tropico.main;

import tropico.DifficultySingleton;
import tropico.Faction;
import tropico.GameState;
import tropico.Player;
import tropico.DifficultySingleton.Difficulty;
import tropico.events.Choice;
import tropico.events.Event;
import tropico.utils.Backup;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Main class of the project, containing the main loop, the scanner and
 * everything you need to launch the game.
 * 
 * @author Corentin OGER and Sébastien DOS SANTOS
 *
 */
public class Main {

	/**
	 * The maximum number of players for a game.
	 */
	private static final int MAX_PLAYERS = 2;

	/**
	 * The main, launching when the application is launched. Calls the mainloop
	 * after creating the gamestate.
	 * 
	 * @param args No arguments needed.
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		try (Scanner sc = new Scanner(System.in)) {
			GameState game = menu(sc);
			mainLoop(sc, game);
		}
	}

	/**
	 * Menu of the game. Allows to choice whether you want to start a new game or
	 * continue on your save.
	 * 
	 * @param sc The scanner used to interact with the user
	 * @return GameState Returns a new GameState, or recovers the one from the save.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static GameState menu(Scanner sc) throws IOException, ClassNotFoundException {
		System.out.println("1) Nouvelle partie \n2) Charger partie");

		int input = getInt(sc, -1, 2);
		if (input == 1) {
			return newGame(sc);
		}
		if (input == 2) {
			return loadGame(sc);
		}

		System.exit(0);
		return null;
	}

	/**
	 * Main loop for the game, this is how the game works, calling every method
	 * needed to make the game work.
	 * 
	 * @param sc   The scanner used to interact with the user.
	 * @param game The GameState that contains all the informations on the game.
	 */
	public static void mainLoop(Scanner sc, GameState game) throws IOException {

		Event event;

		List<Choice> eventChoices;
		Player p;

		// Every loop is a turn
		while (true) {
			p = game.getPlayer();

			System.out.println("[" + p.getName() + "]");

			// Gets the event and his choices, and the option the user sees
			event = game.getCurrentEvent();
			eventChoices = event.getChoices();
			ArrayList<String> choicesStr = getChoices();

			// Where the player chooses an action
			if (actionChoice(sc, game, event, choicesStr.get(0))) {
				return;
			}

			// Now chooses something to do for the event
			int input = getInt(sc, 1, eventChoices.size()) - 1;
			Choice choice = eventChoices.get(input);
			choice.forEach(System.out::println);

			// Does the choice and checks if the is a following event to this choice
			Event next = choice.choose(p);
			if (next != null)
				game.addPendingEvent(next);

			// Checks if the game is over
			if (game.isGameOver()) {
				System.out.println("\nDéfaite ... Vous avez tenu " + (game.getTurn() - 1) + " tours.");
				return;
			}

			// Checks if 4 seasons have past
			if (game.isEndOfYear() && endOfYear(sc, game, choicesStr.get(1))) {
				return;
			}

			// goes to next turn
			game.nextTurn();
			if (game.getCurrentPlayer() == 0)
				game.nextSeason();

		}
	}

	/**
	 * This method builds two strings so the player can choose an option.
	 * 
	 * @return Returns an ArrayList containing two strings.
	 */
	private static ArrayList<String> getChoices() {
		ArrayList<String> choicesStr = new ArrayList<>();

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
	 * Asks the player for an integer between min and max.
	 * 
	 * @param sc  The scanner used to interact with the user.
	 * @param min The minimum value for the integer.
	 * @param max The maximum value for the integer.
	 * @return Returns an int between min an max.
	 */
	private static int getInt(Scanner sc, int min, int max) {
		int input;
		do {
			input = getInt(sc);
		} while (input < min || input > max);
		return input;
	}

	/**
	 * Asks the player for an integer. Used in the other getInt method.
	 * 
	 * @param sc The scanner used to interact with the user.
	 * @return int An int chosen by the user.
	 */
	private static int getInt(Scanner sc) {
		while (!sc.hasNextInt()) {
			sc.next();
			System.out.println("Saisie incorrect");
		}
		return sc.nextInt();
	}

	/**
	 * Creates a new game and constructs a new GameState. The player can select the
	 * difficulty, the gamemode and the number of players.
	 * 
	 * @param sc The scanner used to interact with the user.
	 * @return Returns a new GameState.
	 * @throws IOException
	 */
	private static GameState newGame(Scanner sc) throws IOException {
		String gamemode = gamemodeChoice(sc);
		System.out.println("Dans quelle difficulté souhaitez-vous jouer ?\n1) Facile\n2) Moyen\n3) Difficile");
		List<Difficulty> lst = List.of(Difficulty.EASY, Difficulty.MEDIUM, Difficulty.HARD);

		int difficulty = getInt(sc, 1, 3);

		System.out.println("A combien de joueur voulez-vous jouer ? ");
		int playerNumbers = getInt(sc, 1, MAX_PLAYERS);

		DifficultySingleton.getDifficulty(lst.get(difficulty - 1));
		return new GameState(gamemode, playerNumbers);
	}

	/**
	 * Loads a game from a save file and returns the gamestate of the save. Throws
	 * an exception if the file couldn't be read or it was corrupted.
	 * 
	 * @param sc The scanner used to interact with the user.
	 * @return Returns a GameState from the save file.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private static GameState loadGame(Scanner sc) throws IOException, ClassNotFoundException {
		String gamemode = gamemodeChoice(sc);
		String path = "src/main/resources/backup/" + gamemode + "_save";

		// If there is no save file
		if (!(new File(path)).isFile()) {
			System.out.println("Aucune sauvegarde trouvée pour ce mode de jeu.");
			System.out.println("Retour au menu principal.");
			return menu(sc);
		}
		return (GameState) Backup.loadObject(path);
	}

	/**
	 * This method allows the user to choose the gamemode he wants. The name of the
	 * scenarios and the name of the scenario's directory is written in a txt file.
	 * 
	 * @param sc The scanner used to interact with the user.
	 * @return Returns a String representing the chosen scenario's path.
	 * @throws IOException
	 */
	private static String gamemodeChoice(Scanner sc) throws IOException {
		System.out.println("Choisissez un mode de jeu.");
		ArrayList<String> modes = new ArrayList<String>();

		// Opening the file containing the names of the different scenarios
		InputStream file = Files.newInputStream(Path.of("src/main/resources/scenarios/scenarios.txt"));
		BufferedReader reader = new BufferedReader(new InputStreamReader(file));

		String line;
		String[] splitedLine;
		int count = 0;

		// Reading all the lines
		while ((line = reader.readLine()) != null) {
			count++;
			splitedLine = line.split(":");

			// Prints the gamemodes
			System.out.println(count + ") " + splitedLine[0]);
			modes.add(splitedLine[1]);
		}

		file.close();

		int input = getInt(sc, 1, count);

		return modes.get(input - 1);
	}

	/**
	 * Saves the game by calling the method saveObject from Backup. A different save
	 * file for each mode.
	 * 
	 * @param game The GameState that has to be savec in the file.
	 */
	private static void saveGame(GameState game) throws IOException {
		Backup.saveObject("src/main/resources/backup/" + game.getGamemode() + "_save", game);
	}

	/**
	 * Private method used to ask the player the action he wants to make until he
	 * chooses to quit or make a decision for the current event.
	 * 
	 * @param sc      The scanner to get the player's choice.
	 * @param game    The GameState containing all the informations.
	 * @param event   The current event the player has to deal with.
	 * @param choices The String containing the description of the possible choices.
	 * @return Returns a boolean telling if whether or not the game has to stop
	 * @throws IOException
	 */
	private static boolean actionChoice(Scanner sc, GameState game, Event event, String choices) throws IOException {
		int input;

		System.out.println("\n" + choices);
		input = getInt(sc, -1, 4);
		switch (input) {
		case -1: {
			// Quits the game
			return true;
		}
		case 0: {
			// Saves the game
			saveGame(game);
			System.out.println("Jeu sauvegardé avec succès.");
			return actionChoice(sc, game, event, choices);
		}
		case 1: {
			// Prints the factions of the current player
			List<Faction> factions = game.getPlayer().getFactions();

			factions.forEach(System.out::println);
			return actionChoice(sc, game, event, choices);
		}
		case 2: {
			// Prints the resources of the current player
			System.out.println(game.getPlayer().getResourcesAsString());
			return actionChoice(sc, game, event, choices);
		}
		case 3: {
			// Prints the event
			System.out.println(event);
			return actionChoice(sc, game, event, choices);
		}
		case 4: {
			// Forces the player to make a choice
			System.out.println(event);
			break;
		}
		}

		return false;
	}

	/**
	 * This private method is only called during winter, and triggers the end of the
	 * year, so the player has yet another choice to make.
	 * 
	 * @param sc      The scanner used to interact with the user.
	 * @param game    The GameState containing all the informations.
	 * @param choices The possible choices for the player.
	 * @return Returns true if the player decided to quit or if he lost, false
	 *         otherwise.
	 * @throws IOException
	 */
	private static boolean endOfYear(Scanner sc, GameState game, String choices) throws IOException {
		if (!game.isEndOfYear()) {
			throw new IllegalStateException("Not expected end of year.");
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

	/**
	 * This method allows the player to choice an action. He can make a bribe, buy
	 * food, and when he has finished, he can skip to the next year and see the
	 * summary of the year.
	 * 
	 * @param sc      The scanner used to interact with the user.
	 * @param game    The GameState containing all the informations.
	 * @param choices The possible choices for the player.
	 * @return Returns true if the player decided to quit, false otherwise.
	 * @throws IOException
	 */
	private static boolean endOfYearChoice(Scanner sc, GameState game, String choices) throws IOException {
		int input;
		Player p = game.getPlayer();

		// Asks the player to choose something
		System.out.println("\n" + choices);
		System.out.println("\nVous possédez actuellement " + p.getTreasury() + "$");
		input = getInt(sc, -1, 5);

		switch (input) {
		case -1: {
			// Quits the game
			return true;
		}
		case 0: {
			// Saves the game
			saveGame(game);
			System.out.println("Jeu sauvegardé avec succès.");
			return endOfYearChoice(sc, game, choices);
		}
		case 1: {
			// Shows factions
			List<Faction> factions = p.getFactions();

			factions.forEach(System.out::println);
			return endOfYearChoice(sc, game, choices);
		}
		case 2: {
			// Prints resources
			System.out.println(p.getResourcesAsString());
			return endOfYearChoice(sc, game, choices);
		}
		case 3: {
			// Makes a bribe (if possible)
			bribeChoice(sc, game);
			return endOfYearChoice(sc, game, choices);
		}
		case 4: {
			// Buys food
			marketChoice(sc, game);
			return endOfYearChoice(sc, game, choices);
		}
		// 5 skips to the summary
		}

		return false;
	}

	/**
	 * This private method allows the player to choose which faction we wants to
	 * make a bribe for.
	 * 
	 * @param sc   The scanner used to interact with the user.
	 * @param game The GameState containing all the informations.
	 */
	private static void bribeChoice(Scanner sc, GameState game) {
		Player p = game.getPlayer();
		List<Faction> factions = p.getSatisfiedFactions();
		StringBuilder choices = new StringBuilder("0) Retour");
		int len = factions.size();

		// Adds every faction and their cost to a StringBuilder
		for (int i = 0; i < len; i++) {
			choices.append("\n").append(i + 1).append(") ").append(factions.get(i)).append(" coût : ")
					.append(factions.get(i).getBribeCost()).append("$");
		}

		// Prints the StringBuilder
		System.out.println(choices);

		// Chooses which faction or stop
		int input;
		if ((input = getInt(sc, 0, len)) == 0) {
			return;
		}

		Faction f = factions.get(input - 1);

		// If you can't make a bribe for the faction you chose, you are still in the
		// same menu.
		if (!p.bribe(f)) {
			System.out.println("Vous n'avez pas assez d'argent pour donner un pot de vin à cette faction !");
			bribeChoice(sc, game);
		}

	}

	/**
	 * This private method allows the player to choose how many food he wants to buy
	 * to the market.
	 * 
	 * @param sc   The scanner used to interact with the user.
	 * @param game The GameState containing all the informations.
	 */
	private static void marketChoice(Scanner sc, GameState game) {
		Player p = game.getPlayer();
		int max = p.getTreasury() / 8;

		// If the player can't buy even one unit
		if (max == 0) {
			System.out.println("Vous n'avez pas assez d'argent pour acheter de la nourriture.");
			return;
		}

		// Prints the maximum amount the player can buy now
		System.out.println("Vous avez actuellement " + p.getFoodUnit() + " unités de nourriture.");
		System.out.println("Combien souhaitez-vous en acheter ? (max:" + max + ")");

		int input = getInt(sc, 0, max);

		// Back to the end of year menu if he decides to buy 0
		if (input == 0) {
			return;
		}

		p.buyFood(input);
	}

}
