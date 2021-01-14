package tropico.utils;


public class Utils {

    /**
     * limit the value between min and max
     * @param value
     * @param min
     * @param max
     * @return min <= value <= max
     */
    public static int limit(int value, int min, int max) {
    	if (value < min) {
			return min;
		}
		return Math.min(value, max);
    }

}
