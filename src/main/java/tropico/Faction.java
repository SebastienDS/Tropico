package tropico;

public enum Faction {
    CAPITALISTES,
    COMMUNISTES,
    LIBERAUX,
    RELIGIEUX,
    MILITARISTES,
    ECOLOGISTES,
    NATIONALISTES,
    LOYALISTES;

    private double satisfaction;
    private int supporter;

    public double getSatisfaction() {
        return satisfaction;
    }

    public void addSatisfaction(double value) {
        satisfaction += value;
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
