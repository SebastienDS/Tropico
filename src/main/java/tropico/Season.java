package tropico;

public enum Season {
    SPRING, SUMMER, AUTUMN, WINTER;

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
    
    public static Season nextSeason(Season season) {
    	switch (season) {
		case SPRING:
			return SUMMER;
		case SUMMER:
			return AUTUMN;
		case AUTUMN:
			return WINTER;
		case WINTER:
			return SPRING;
		default:
			throw new IllegalArgumentException("Le paramètre n'est pas une saison existante.");
		}
    }
}
