package com.carlolonghi.todo.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TodayItems implements Serializable {
    private final List<ItemWithDate> nonCheckedItems;
    private final List<ItemWithDate> checkedItems;

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
        Iterator<ItemWithDate> iterator=checkedItems.iterator();
        while(iterator.hasNext()){
            ItemWithDate item=iterator.next();
            if(item.isOutdated())
                iterator.remove();
        }
    }

    public void updateNonCheckedItems(){
        for (ItemWithDate item : nonCheckedItems){
            if(item.isOutdated())
                item.updateDay();
        }
    }

    public boolean contains(String string){
        for(ItemWithDate item : nonCheckedItems){
            if(item.getName().equals(string))
                return true;
        }
        return false;
    }
}
