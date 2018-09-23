package com.carlolonghi.todo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {

    private String listTitle;
    private Map<String,HashMap<String,Boolean>> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume(){
        super.onResume();

        //Get the title of the list
        Intent intent = getIntent();
        String title = intent.getStringExtra("com.carlolonghi.todo.TITLE");
        this.listTitle=title;

        //Fill the activity with the correct items
        this.populateItems();
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
        if(newItem.equals("")){
            Context context = getApplicationContext();
            CharSequence text = "You can't add an empty item";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
        else {
            newItemField.setText("");
            radioButton.setText(newItem);

            // insert into main view
            ViewGroup insertPoint = (ViewGroup) findViewById(R.id.itemsList);
            insertPoint.addView(radioButton, layoutParams);

            //Save the new item
            this.updateItemsOnFile(newItem,false);
        }
    }

    @Override
    protected void onPause(){
        super.onPause();

        this.changeItemsStatus();
    }

    private void updateItemsOnFile(String item, Boolean isChecked){
        try{
            FileOutputStream outputStream = this.getBaseContext().openFileOutput("items.dat", MODE_PRIVATE);
            HashMap<String,Boolean> tmp = items.get(listTitle);
            tmp.put(item,isChecked);
            items.put(listTitle, tmp);
            ObjectOutputStream writer = new ObjectOutputStream(outputStream);
            writer.writeObject(items);
            outputStream.close();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readItemsFromFile(){
        items=new HashMap<>();
        try{
            FileInputStream inputStream = this.openFileInput("items.dat");
            ObjectInputStream reader = new ObjectInputStream(inputStream);
            items = (Map<String, HashMap<String,Boolean>>) reader.readObject();
            inputStream.close();
            reader.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void populateItems(){
        this.readItemsFromFile();
        try{
            for (String item : this.items.get(this.listTitle).keySet()) {
                RadioButton newItemAdded = new RadioButton(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                layoutParams.setMargins(16, 16, 16, 0);
                newItemAdded.setText(item);
                if(items.get(listTitle).get(item)==true)
                    newItemAdded.setChecked(true) ;
                ViewGroup insertPoint = findViewById(R.id.itemsList);
                insertPoint.addView(newItemAdded,layoutParams);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    private void changeItemsStatus(){
        LinearLayout linearLayout=(LinearLayout)findViewById(R.id.itemsList);
        int numOfItems=linearLayout.getChildCount();
        for(int i=0;i<numOfItems;i++){
            RadioButton radioButton=(RadioButton)linearLayout.getChildAt(i);
            if(radioButton.isChecked()){
                HashMap<String, Boolean> tmp=items.get(this.listTitle);
                tmp.put(radioButton.getText().toString(),true);
                items.put(this.listTitle,tmp);
                this.updateItemsOnFile(radioButton.getText().toString(),true);
            }
            else{
                HashMap<String, Boolean> tmp=items.get(this.listTitle);
                tmp.put(radioButton.getText().toString(),false);
                items.put(this.listTitle,tmp);
            }
        }
    }
}


