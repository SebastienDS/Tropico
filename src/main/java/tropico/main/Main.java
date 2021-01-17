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

public class Main {

	private static final int MAX_PLAYERS = 2;

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
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws Exception
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
	 * main loop for the game
	 * 
	 * @param sc   the scanner used to interact with the user
	 * @param game the GameState contains all the informations on the game
	 */
	public static void mainLoop(Scanner sc, GameState game) throws IOException {

		Event event;

		List<Choice> eventChoices;
		Player p;

		while (true) {
			p = game.getPlayer();

			// TODO only doable events
			System.out.println("[" + p.getName() + "]");

			event = game.getCurrentEvent();
			eventChoices = event.getChoices();
			ArrayList<String> choicesStr = getChoices();

			if (actionChoice(sc, game, event, choicesStr.get(0))) {
				return;
			}

			int input = getInt(sc, 1, eventChoices.size()) - 1;
			Choice choice = eventChoices.get(input);
			choice.forEach(System.out::println);

			Event next = choice.choose(p);
			if (next != null) game.addPendingEvent(next);

			if (game.isGameOver()) {
				System.out.println("\nDéfaite ... Vous avez tenu " + (game.getTurn() - 1) + " tours.");
				return;
			}

			if (game.isEndOfYear() && endOfYear(sc, game, choicesStr.get(1))) {
				return;
			}

			game.nextTurn();
			if (game.getCurrentPlayer() == 0) game.nextSeason();

		}
	}

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
	 * @throws IOException 
	 */
	private static GameState newGame(Scanner sc) throws IOException {
		String gamemode = gamemodeChoice(sc);
		System.out.println("Dans quelle difficulté souhaitez-vous jouer ?\n1) Facile\n2) Moyen\n3) Difficile");
		List<Difficulty> lst = List.of(Difficulty.EASY, Difficulty.MEDIUM, Difficulty.HARD);
		
		int difficulty = getInt(sc, 1, 3);

		System.out.println("A combien de joueur voulez-vous jouer ? ");
		int playerNumbers = getInt(sc, 1, MAX_PLAYERS);

		DifficultySingleton.getDifficulty(lst.get(difficulty-1));
		return new GameState(gamemode, playerNumbers);
	}

	/**
	 * load a Game from a save
	 * 
	 * @return game loaded
	 */
	private static GameState loadGame(Scanner sc) throws IOException, ClassNotFoundException {
		String gamemode = gamemodeChoice(sc);
		String path = "src/main/resources/backup/" + gamemode + "_save";
		if (!(new File(path)).isFile()) {
			System.out.println("Aucune sauvegarde trouvée pour ce mode de jeu.");
			System.out.println("Retour au menu principal.");
			return menu(sc);
		}
		return (GameState) Backup.loadObject(path);
	}
	
	private static String gamemodeChoice(Scanner sc) throws IOException {
		System.out.println("Choisissez un mode de jeu.");
		ArrayList<String> modes = new ArrayList<String>();

		// Opening the file containing the names of the different scenarios
        InputStream file = Files.newInputStream(Path.of("src/main/resources/scenarios/scenarios.txt"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(file));
        
        String line;
        String[] splitedLine;
        int count = 0;
        
        while((line = reader.readLine()) != null){
        	count++;
        	splitedLine = line.split(":");
            System.out.println(count + ") " + splitedLine[0]);
            modes.add(splitedLine[1]);
        }
        
        file.close();
		
		int input = getInt(sc, 1, count);
		
		return modes.get(input-1);
	}

	/**
	 * save the game
	 * 
	 * @param game
	 */
	private static void saveGame(GameState game) throws IOException {
		Backup.saveObject("src/main/resources/backup/" + game.getGamemode() + "_save", game);
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
