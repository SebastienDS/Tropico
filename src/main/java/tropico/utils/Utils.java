package tropico.utils;


public class Utils {

    public static int limit(int value, int min, int max) {
        return Math.max(Math.min(value, min), max);
    }

}
