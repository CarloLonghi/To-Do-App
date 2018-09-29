package com.carlolonghi.todo;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.ViewModel;
import android.widget.LinearLayout;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class MyViewModel extends AndroidViewModel {

    private LinkedHashMap<String,LinkedHashMap<String,Boolean>> items;

    public MyViewModel(Application application){
        super(application);
    }

    public Map<String,LinkedHashMap<String,Boolean>> getItems() {
        items = new LinkedHashMap<>();
        loadItems();
        return items;
    }

    private void loadItems(){
        items=new LinkedHashMap<>();
        try{
            FileInputStream inputStream = getApplication().getApplicationContext().openFileInput("items.dat");
            ObjectInputStream reader = new ObjectInputStream(inputStream);
            items = (LinkedHashMap<String, LinkedHashMap<String,Boolean>>) reader.readObject();
            inputStream.close();
            reader.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
