package tropico.events;

public enum Season {
    ete, automne, hiver, printemps;

    public static boolean contains(Season season) {
        for (Season s: Season.values()) {
            if (s.equals(season)) return true;
        }
        return false;
    }
}
