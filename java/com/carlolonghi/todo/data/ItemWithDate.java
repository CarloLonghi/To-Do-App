package com.carlolonghi.todo.data;

import java.io.Serializable;
import java.util.Calendar;

public class ItemWithDate implements Serializable {
    private final String name;
    private int dayOfYear;
    private int year;

    public ItemWithDate(String name, int day, int year){
        this.name=name;
        this.dayOfYear=day;
        this.year=year;
    }

    public String getName() {
        return name;
    }

    public int getDay() {
        return dayOfYear;
    }

    public int getYear(){
        return this.year;
    }

    public boolean isOutdated() {
        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_YEAR);
        return currentDay > dayOfYear;
    }

    public void updateDay(){
        if((year%4==0 && dayOfYear!=366) || (year%4!=0 && dayOfYear!=365))
            dayOfYear++;
        else{
            year++;
            dayOfYear=1;
        }
    }
}
