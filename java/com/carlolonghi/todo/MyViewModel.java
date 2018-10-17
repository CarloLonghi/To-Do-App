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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class MyViewModel extends AndroidViewModel {

    private LinkedHashMap<String,Items> items;

    public MyViewModel(Application application){
        super(application);
    }

    public Map<String,Items> getItems() {
        items = new LinkedHashMap<>();
        loadItems();
        return items;
    }

    private void loadItems(){
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
    }

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
}
