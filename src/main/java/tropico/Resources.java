package tropico;

import tropico.utils.Utils;

public class Resources {

    private static final int RESOURCE_MIN = 0;
    private static final int RESOURCE_MAX = 100;
    private static final int FOOD_UNIT_PER_CITIZEN = 4;

    private int industry;
    private int farming;
    private int treasury;
    private int foodUnit;

    public Resources(int industry, int farming, int treasury) {
        this.industry = industry;
        this.farming = farming;
        this.treasury = treasury;
    }

    public int getIndustry() {
        return industry;
    }

    public int getFarming() {
        return farming;
    }

    public int getTreasury() {
        return treasury;
    }

    public int getFoodUnit() {
        return foodUnit;
    }

    public void addIndustry(int value) {
        industry += value;
        industry = limit(industry, farming);
    }

    public void addFarming(int value) {
        farming += value;
        farming = limit(farming, industry);
    }

    public int getMoneyGenerated() {
        return industry * 10;
    }

    public int getFoodGenerated() {
        return farming * 40;
    }

    private static int limit(int a, int b) {
        return Math.max(Math.min(a, RESOURCE_MIN) + b, RESOURCE_MAX);
    }
}
