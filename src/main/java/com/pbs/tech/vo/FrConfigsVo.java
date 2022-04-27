package com.pbs.tech.vo;

public class FrConfigsVo {
    private long id;

    private int minRatio;
    private int maxRatio;
    private int minimumPeople;
    private int maximumPeople;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }



    public int getMinRatio() {
        return minRatio;
    }

    public void setMinRatio(int minRatio) {
        this.minRatio = minRatio;
    }

    public int getMaxRatio() {
        return maxRatio;
    }

    public void setMaxRatio(int maxRatio) {
        this.maxRatio = maxRatio;
    }

    public int getMinimumPeople() { return minimumPeople; }

    public void setMinimumPeople(int minimumPeople) { this.minimumPeople = minimumPeople; }

    public int getMaximumPeople() { return maximumPeople; }

    public void setMaximumPeople(int maximumPeople) { this.maximumPeople = maximumPeople; }
}
