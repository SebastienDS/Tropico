package tropico;

import tropico.utils.Utils;

public enum Faction {
    CAPITALISTES,
    COMMUNISTES,
    LIBERAUX,
    RELIGIEUX,
    MILITARISTES,
    ECOLOGISTES,
    NATIONALISTES,
    LOYALISTES;

    private static final int SATISFACTION_MIN = 0;
    private static final int SATISFACTION_MAX = 100;

    private int satisfaction;
    private int supporter;

    public int getSatisfaction() {
        return satisfaction;
    }

    public void addSatisfaction(int value) {
        satisfaction = Utils.limit(satisfaction + value, SATISFACTION_MIN, SATISFACTION_MAX);
    }

    public int getSupporter() {
        return supporter;
    }

    public void addSupporter(int count) {
        supporter += count;
    }

    public static boolean contains(Faction faction) {
        for (Faction f: Faction.values()) {
            if (f.equals(faction)) return true;
        }
        return false;
    }
}
