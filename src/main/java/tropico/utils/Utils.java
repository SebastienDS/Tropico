package tropico.utils;

/**
 * This class contains some useful, general methods.
 * 
 * @author Corentin OGER and Sébastien DOS SANTOS
 *
 */
public class Utils {

	/**
	 * Limits the value between min and max.
	 * 
	 * @param value The value you want to limit.
	 * @param min   The minimum value.
	 * @param max   The maximum value.
	 * @return Returns min if value is inferior to min, max if value is superior to
	 *         max, value otherwise.
	 */
	public static int limit(int value, int min, int max) {
		if (value < min) {
			return min;
		}
		return Math.min(value, max);
	}

}
