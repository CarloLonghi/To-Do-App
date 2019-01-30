package com.carlolonghi.todo;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import static android.content.Context.MODE_PRIVATE;

public class ItemsViewModel extends AndroidViewModel {

    private LinkedHashMap<String,Items> items;
    private Items todaysItems;

    public ItemsViewModel(Application application){
        super(application);

        loadItems();
    }

    public LinkedHashMap<String,Items> getItems() {
        return items;
    }

    public ArrayList<String> getKeySet(){
        return new ArrayList<>(items.keySet());
    }

    public void loadItems(){
        items=new LinkedHashMap<>();
        todaysItems=new Items();
        try{
            FileInputStream inputStream = getApplication().getApplicationContext().openFileInput("items.dat");
            ObjectInputStream reader = new ObjectInputStream(inputStream);
            items = (LinkedHashMap<String,Items>) reader.readObject();
            inputStream.close();
            reader.close();

            inputStream = getApplication().getApplicationContext().openFileInput("today.dat");
            reader = new ObjectInputStream(inputStream);
            todaysItems=(Items) reader.readObject();
            inputStream.close();
            reader.close();
        } catch (Exception e){
            e.printStackTrace();
        }
        if(todaysItems==null)
            todaysItems=new Items();
    }

    public void addList(String listTitle){
        items.put(listTitle.toUpperCase(),new Items());
    }

    public void removeList(String listTitle) {items.remove(listTitle);}

    public void updateItemsOnFile(Context context){
        todaysItems=new Items();
        try{
            FileOutputStream outputStream = context.openFileOutput("items.dat", MODE_PRIVATE);
            ObjectOutputStream writer = new ObjectOutputStream(outputStream);
            writer.writeObject(items);
            outputStream.close();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateTodaysItemsOnFile(Context context){
        items=new LinkedHashMap<>();
        try{
            FileOutputStream outputStream = context.openFileOutput("today.dat", MODE_PRIVATE);
            ObjectOutputStream writer = new ObjectOutputStream(outputStream);
            writer.writeObject(todaysItems);
            outputStream.close();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isEmpty(){
        return items.isEmpty();
    }

    public void loadTodaysItems(){
        todaysItems=new Items();
        try{
            FileInputStream inputStream = getApplication().getApplicationContext().openFileInput("items.dat");
            ObjectInputStream reader = new ObjectInputStream(inputStream);
            todaysItems = (Items) reader.readObject();
            inputStream.close();
            reader.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public Items getTodaysItems(){
        return todaysItems;
    }

    //A funtion to update the modelView version of the lists
    public void updateItems(String listTitle,Items items){
        this.items.put(listTitle,items);
    }

    //A funtion to update the modelView version of of todaysItems
    public void updateTodaysItems(Items items){
        this.todaysItems=items;
    }
}
