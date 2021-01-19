package tropico;

/**
 * This enum class contains and manages the seasons.
 * 
 * @author Corentin OGER and Sébastien DOS SANTOS
 *
 */
public enum Season {
	SPRING, SUMMER, AUTUMN, WINTER;

	/**
	 * Use this method to know if the season season is a part of this enum.
	 * 
	 * @param season The season you want to know the origin.
	 * @return Returns true is season is a Season, false otherwise.
	 */
	public static boolean contains(Season season) {
		for (Season s : Season.values()) {
			if (s.equals(season))
				return true;
		}
		return false;
	}

	/**
	 * This method allows you to get the following season of a season. Throws an
	 * IllegalArgumentException if the season is incorrect.
	 * 
	 * @param season The season of which you want the following one.
	 * @return Returns the season following the one in parameter
	 */
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
			throw new IllegalArgumentException(season + " is not a Season object.");
		}
	}
}
