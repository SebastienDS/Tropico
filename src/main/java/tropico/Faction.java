package tropico;

import tropico.utils.Utils;

public class Faction {

    private static final int SATISFACTION_MIN = 0;
    private static final int SATISFACTION_MAX = 100;

    private final String name;
    private int satisfaction;
    private int supporter;

    public Faction(String name, int satisfaction, int supporter) {
        this.name = name;
        this.satisfaction = satisfaction;
        this.supporter = supporter;
    }

    public String getName() {
        return name;
    }

    public int getSatisfaction() {
        return satisfaction;
    }

    /**
     * add satisfaction to the faction
     * @param value
     */
    public void addSatisfaction(int value) {
        satisfaction = Utils.limit(satisfaction + value, SATISFACTION_MIN, SATISFACTION_MAX);
    }

    public int getSupporter() {
        return supporter;
    }

    /**
     * add supporter to the faction
     * @param count
     */
    public void addSupporter(int count) {
        supporter += count;
    }

    @Override
    public String toString() {
        return name + " : " + satisfaction + "% / " + supporter;
    }
}
