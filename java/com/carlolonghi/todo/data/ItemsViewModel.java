package com.carlolonghi.todo.data;

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
    private LinkedHashMap<String,Items> bookmarkItems;
    private TodayItems todaysItems;

    public ItemsViewModel(Application application){
        super(application);

        loadItems();
    }

    public LinkedHashMap<String,Items> getItems() {
        return items;
    }

    public LinkedHashMap<String,Items> getBookmarkItems(){return bookmarkItems;}

    public ArrayList<String> getKeySet(){
        return new ArrayList<>(items.keySet());
    }

    public ArrayList<String> getBMKeySet(){
        return new ArrayList<>(bookmarkItems.keySet());
    }

    public void loadItems(){
        items=new LinkedHashMap<>();
        todaysItems=new TodayItems();
        bookmarkItems=new LinkedHashMap<>();
        try{
            FileInputStream inputStream = getApplication().getApplicationContext().openFileInput("items.dat");
            ObjectInputStream reader = new ObjectInputStream(inputStream);
            items = (LinkedHashMap<String,Items>) reader.readObject();
            inputStream.close();
            reader.close();

            inputStream = getApplication().getApplicationContext().openFileInput("today.dat");
            reader = new ObjectInputStream(inputStream);
            todaysItems=(TodayItems) reader.readObject();
            inputStream.close();
            reader.close();

            inputStream = getApplication().getApplicationContext().openFileInput("bookmarkitems.dat");
            reader = new ObjectInputStream(inputStream);
            bookmarkItems = (LinkedHashMap<String,Items>) reader.readObject();
            inputStream.close();
            reader.close();
        } catch (Exception e){
            e.printStackTrace();
        }
        if(todaysItems==null)
            todaysItems=new TodayItems();
    }

    public void addList(String listTitle){
        items.put(listTitle.toUpperCase(),new Items());
    }

    public void removeList(String listTitle) {items.remove(listTitle);}

    public void bookmarkList(String listTitle){
        bookmarkItems.put(listTitle,items.get(listTitle));
        items.remove(listTitle);
    }

    public void unbookmarkList(String listTitle){
        items.put(listTitle,bookmarkItems.get(listTitle));
        bookmarkItems.remove(listTitle);
    }

    public void updateItemsOnFile(Context context){
        todaysItems=new TodayItems();
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

    public void updateBookmarkItemsOnFile(Context context){
        todaysItems=new TodayItems();
        try{
            FileOutputStream outputStream = context.openFileOutput("bookmarkitems.dat", MODE_PRIVATE);
            ObjectOutputStream writer = new ObjectOutputStream(outputStream);
            writer.writeObject(bookmarkItems);
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

    public boolean itemsIsEmpty(){
        return items.isEmpty();
    }
    public boolean bmItemsIsEmpty(){return bookmarkItems.isEmpty();}

    public void loadTodaysItems(){
        todaysItems=new TodayItems();
        try{
            FileInputStream inputStream = getApplication().getApplicationContext().openFileInput("items.dat");
            ObjectInputStream reader = new ObjectInputStream(inputStream);
            todaysItems = (TodayItems) reader.readObject();
            inputStream.close();
            reader.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public TodayItems getTodaysItems(){
        return todaysItems;
    }

    //A funtion to update the modelView version of the lists
    public void updateItems(String listTitle,Items items){
        this.items.put(listTitle,items);
    }

    public void updateBookmarkItems(String listTitle,Items items){ this.bookmarkItems.put(listTitle,items); }

    //A funtion to update the modelView version of of todaysItems
    public void updateTodaysItems(TodayItems items){
        this.todaysItems=items;
    }
}
