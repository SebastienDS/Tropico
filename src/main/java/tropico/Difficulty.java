package tropico;

public enum Difficulty {
    EASY(0.5), MEDIUM(1), HARD(2);

    private final double multiplicator;

    Difficulty(double multiplicator) {
        this.multiplicator = multiplicator;
    }

    public double getMultiplicator() {
        return multiplicator;
    }
}
