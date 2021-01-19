package tropico;

/**
 * This class is a singleton which serves to access the difficulty of the game.
 * The field difficulty can only be initialised once, after this, you'll only be
 * able to recover it.
 * 
 * @author Corentin OGER and Sébastien DOS SANTOS
 *
 */
public final class DifficultySingleton {

	/**
	 * This enum class contains the 3 different difficulties.
	 */
	public static enum Difficulty {
		EASY, MEDIUM, HARD
	};

	/**
	 * The field difficulty can only be changer once with the getDifficulty method,
	 * which also serves to get it.
	 */
	private static Difficulty difficulty;

	/**
	 * <b>DifficultySingleton's constructor</b>
	 * 
	 * Private constructor allowing to change the difficulty field. Only accessible
	 * within the class, so it can be called only once.
	 * 
	 * @param difficulty The difficulty that will be set for the rest of the game.
	 */
	private DifficultySingleton(Difficulty difficulty) {
		DifficultySingleton.difficulty = difficulty;
	}

	/**
	 * Use this method to set the difficulty, can only be done one time. It also
	 * returns the difficulty.
	 * 
	 * @param d The difficulty you try to set to the game, will only work if it's
	 *          the first time.
	 * @return Returns the difficulty choosed by the player when starting a new
	 *         game.
	 */
	public static Difficulty getDifficulty(Difficulty d) {
		if (difficulty == null) {
			new DifficultySingleton(d);
		}
		return difficulty;
	}

	/**
	 * Use this method to get the difficulty.
	 * 
	 * @return Returns the difficulty choosed by the player when starting a new
	 *         game.
	 */
	public static Difficulty getDifficulty() {
		return getDifficulty(Difficulty.MEDIUM);
	}
}
