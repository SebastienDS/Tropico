package tropico;

public enum Season {
    SUMMER, AUTUMN, WINTER, SPRING;

    public static boolean contains(Season season) {
        for (Season s: Season.values()) {
            if (s.equals(season)) return true;
        }
        return false;
    }
}
