package com.pbs.tech.vo;

public class NprConfigsVo {

    private long id;

    private int minRatio;
    private int maxRatio;
    private int minKernel;
    private int maxKernel;


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

    public int getMinKernel() {
        return minKernel;
    }

    public void setMinKernel(int minKernel) {
        this.minKernel = minKernel;
    }

    public int getMaxKernel() {
        return maxKernel;
    }

    public void setMaxKernel(int maxKernel) {
        this.maxKernel = maxKernel;
    }




}
