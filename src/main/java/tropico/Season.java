package tropico;

public enum Season {
    SUMMER, AUTUMN, WINTER, SPRING;

    /**
     * get if the season if a valid season
     * @param season
     * @return true if the season is valid
     */
    public static boolean contains(Season season) {
        for (Season s: Season.values()) {
            if (s.equals(season)) return true;
        }
        return false;
    }
}
