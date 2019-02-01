package com.carlolonghi.todo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TodayItems implements Serializable {
    private List<ItemWithDate> nonCheckedItems;
    private List<ItemWithDate> checkedItems;

    public TodayItems(){
        checkedItems=new ArrayList<>();
        nonCheckedItems=new ArrayList<>();
    }

    public List<ItemWithDate> getNonCheckedItems(){
        return this.nonCheckedItems;
    }

    public List<ItemWithDate> getCheckedItems(){
        return checkedItems;
    }

    public int getTotalSize(){
        return checkedItems.size()+nonCheckedItems.size();
    }

    public void remove(int position){
        if(position>=nonCheckedItems.size()){
            checkedItems.remove(position-nonCheckedItems.size()-1);
        }
        else{
            nonCheckedItems.remove(position);
        }
    }

    public void addCheckedItem(ItemWithDate item){
        checkedItems.add(item);
    }

    public void addNonCheckedItem(ItemWithDate item){
        nonCheckedItems.add(item);
    }

    public void updateCheckedItems(){
        for(ItemWithDate item : checkedItems){
            if(item.isOutdated())
                checkedItems.remove(item);
        }
    }

    public void updateNonCheckedItems(){
        for (ItemWithDate item : nonCheckedItems){
            if(item.isOutdated())
                item.updateDay();
        }
    }
}
