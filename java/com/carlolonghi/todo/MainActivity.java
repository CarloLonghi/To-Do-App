package com.carlolonghi.todo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//FACCIAMO UNA PROVAAAAAAAAAAAAAAAAAaa

public class MainActivity extends Activity {

    private String listTitle;
    private Map<String,List<String>> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get the title of the list
        Intent intent = getIntent();
        String title = intent.getStringExtra("com.carlolonghi.todo.TITLE");
        this.listTitle=title;

        //Read the map of items
        items=new HashMap<>();
        File data=new File(getApplicationInfo().dataDir,"items.dat");
        try{
            BufferedReader br = new BufferedReader(new FileReader(data));
            if (br.readLine() != null) {
                FileInputStream inputStream = new FileInputStream(data);
                ObjectInputStream reader = new ObjectInputStream(inputStream);
                items = (Map<String, List<String>>) reader.readObject();

                //Fill the activity with the correct items
                for (String item : this.items.get(this.listTitle)) {
                    RadioButton newItemAdded = new RadioButton(this);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
                    layoutParams.setMargins(16, 16, 16, 0);
                    newItemAdded.setText(item);
                    ViewGroup insertPoint = findViewById(R.id.itemsList);
                    insertPoint.addView(newItemAdded,layoutParams);
                }
                inputStream.close();
                reader.close();
            }
            br.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onClick(View view){
        // fill in any details dynamically here
        RadioButton radioButton=new RadioButton(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(16, 16, 16, 0);
        EditText newItemField=(EditText) findViewById(R.id.addNewText);
        String newItem=newItemField.getText().toString();
        newItemField.setText("");
        radioButton.setText(newItem);

        // insert into main view
        ViewGroup insertPoint=(ViewGroup)findViewById(R.id.itemsList);
        insertPoint.addView(radioButton,layoutParams);

        //Save the new item
        File data=new File(getApplicationInfo().dataDir,"item.dat");
        try{
            FileOutputStream outputStream=new FileOutputStream(data);
            List<String> tmp=items.get(listTitle);
            tmp.add(newItem);
            items.put(listTitle,tmp);
            data.delete();
            data.createNewFile();
            ObjectOutputStream writer=new ObjectOutputStream(outputStream);
            writer.writeObject(items);
            outputStream.close();
            writer.close();
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}
