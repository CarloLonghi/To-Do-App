package com.carlolonghi.todo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Items implements Serializable{
    private List<String> nonCheckedItems;
    private List<String> checkedItems;

    public Items(){
        checkedItems=new ArrayList<>();
        nonCheckedItems=new ArrayList<>();
    }

    public List<String> getNonCheckedItems(){
        return this.nonCheckedItems;
    }

    public List<String> getCheckedItems(){
        return checkedItems;
    }

    public int getTotalSize(){
        return checkedItems.size()+nonCheckedItems.size();
    }

    public void addNonCheckedItem(String item){
        nonCheckedItems.add(item);
    }

    public void remove(int position){
        if(position>=nonCheckedItems.size()){
            checkedItems.remove(position-nonCheckedItems.size()-1);
        }
        else{
            nonCheckedItems.remove(position);
        }
    }

    public void addCheckedItem(String item){
        checkedItems.add(item);
    }
}
