package tropico;

public final class DifficultySingleton {
	public static enum Difficulty {EASY, MEDIUM, HARD};

    private static Difficulty difficulty;

    private DifficultySingleton(Difficulty difficulty) {
    	DifficultySingleton.difficulty = difficulty;
    }
    
    public static Difficulty getDifficulty(Difficulty d) {
    	if (difficulty == null) {
    		new DifficultySingleton(d);
		}
    	return difficulty;
    }
    
    public static Difficulty getDifficulty() {
    	return getDifficulty(Difficulty.MEDIUM);
    }
}
