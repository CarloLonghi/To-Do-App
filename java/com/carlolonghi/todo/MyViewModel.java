package com.carlolonghi.todo;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.widget.LinearLayout;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class MyViewModel extends AndroidViewModel {

    private LinkedHashMap<String,Items> items;

    public MyViewModel(Application application){
        super(application);

        items=loadItems();
    }

    public LinkedHashMap<String,Items> getItems() {
        return items;
    }

    public ArrayList<String> getKeySet(){
        return new ArrayList<>(items.keySet());
    }

    public LinkedHashMap<String,Items> loadItems(){
        items=new LinkedHashMap<>();
        try{
            FileInputStream inputStream = getApplication().getApplicationContext().openFileInput("items.dat");
            ObjectInputStream reader = new ObjectInputStream(inputStream);
            items = (LinkedHashMap<String,Items>) reader.readObject();
            inputStream.close();
            reader.close();
        } catch (Exception e){
            e.printStackTrace();
        }
        return items;
    }

    public void addList(String listTitle){
        items.put(listTitle.toUpperCase(),new Items());
    }

    public void removeList(String listTitle) {items.remove(listTitle);}

    public void updateItemsOnFile(Context context){
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

    public boolean isEmpty(){
        return items.isEmpty();
    }
}
