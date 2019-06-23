package com.example.codigger.vana;

public class Tekrar {
    String startTime;
    String endTime;
    String days;
    boolean tg;
    int count;




    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isTg() {
        return tg;
    }

    public void setTg(boolean tg) {
        this.tg = tg;
    }

    public Tekrar(String startTime, String endTime, String days, boolean toggle, int counter) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.days = days;
        this.tg = toggle;
        this.count = counter;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }
}
