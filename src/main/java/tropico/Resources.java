package tropico;

public class Resources {

    private static final int RESOURCE_MAX = 100;

    private double industry;
    private double farming;
    private int treasury;
    private int foodUnit;

    public double getIndustry() {
        return industry;
    }

    public double getFarming() {
        return farming;
    }

    public int getTreasury() {
        return treasury;
    }

    public int getFoodUnit() {
        return foodUnit;
    }

    public void addIndustry(double value) {
        industry += value;
        industry = limit(industry, farming);
    }

    public void addFarming(double value) {
        farming += value;
        farming = limit(farming, industry);
    }

    private static double limit(double a, double b) {
        return Math.max(Math.min(a, 0) + b, Resources.RESOURCE_MAX);
    }
}
