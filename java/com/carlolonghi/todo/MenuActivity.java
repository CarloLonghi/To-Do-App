package com.carlolonghi.todo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.text.Layout;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MenuActivity extends Activity {

    SharedPreferences prefs = null;
    private Map<String,LinkedHashMap<String,Boolean>> items;
    private Button contextMenuList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        populateLists();
    }

    private void populateLists(){
        items=new LinkedHashMap<>();
        try {
            readItemsFromFile();
            ((LinearLayout)findViewById(R.id.ListTitles)).removeAllViews();
            for (String list : items.keySet()) {
                ViewGroup insertPoint = (LinearLayout) findViewById(R.id.ListTitles);
                Button newItemAddedButton = new Button(this);
                newItemAddedButton.setText(list);
                registerForContextMenu(newItemAddedButton);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                layoutParams.setMargins(48, 48, 48, 0);
                insertPoint.addView(newItemAddedButton, layoutParams);

                //Setting the listener for the list buttons
                newItemAddedButton.setOnClickListener(new ListButtonListener());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();

        items=new LinkedHashMap<>();
        readItemsFromFile();
    }

    private void readItemsFromFile(){
        items=new LinkedHashMap<>();
        try {
            FileInputStream inputStream = this.openFileInput("items.dat");
            ObjectInputStream reader = new ObjectInputStream(inputStream);
            items = (LinkedHashMap<String, LinkedHashMap<String,Boolean>>) reader.readObject();
            inputStream.close();
            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onClick(final View addButton) {
        addButton.setClickable(false);

        //Add the edittext where the user has to type the title of the new list
        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v1 = vi.inflate(R.layout.new_list_to_add_layout, null);
        ViewGroup insertPoint = (ViewGroup) findViewById(R.id.ListTitles);
        insertPoint.addView(v1);
        insertPoint.findViewById(R.id.addNewList).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addButton.setClickable(true);

                //Get the title and remove the edittext
                EditText editText = (EditText) findViewById(R.id.newListToAdd);
                String newListTitle = editText.getText().toString();

                //Delete the add editText and Button from layout
                ((ViewManager) editText.getParent()).removeView(editText);
                ((ViewManager) v.getParent()).removeView(v);

                //Add the new list to the page
                ViewGroup insertPoint = (LinearLayout) findViewById(R.id.ListTitles);
                Button newItemAddedButton = new Button(v.getContext());
                newItemAddedButton.setText(newListTitle);
                registerForContextMenu(newItemAddedButton);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                layoutParams.setMargins(48, 48, 48, 0);
                insertPoint.addView(newItemAddedButton, layoutParams);

                //Setting the listener for the list buttons
                newItemAddedButton.setOnClickListener(new ListButtonListener());

                items.put(newListTitle,new LinkedHashMap<String, Boolean>());
                updateItemsOnFile();

                //open the list of items
                Intent intent = new Intent(newItemAddedButton.getContext(), MainActivity.class);
                String list = ((Button) newItemAddedButton).getText().toString();
                intent.putExtra("com.carlolonghi.todo.TITLE", list);
                newItemAddedButton.getContext().startActivity(intent);
            }
        });
    }

    //Creates the context menu when the lists are long-pressed
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, v.getId(), 0, "Delete");
        menu.add(0, v.getId(), 0, "Bookmark");
        this.contextMenuList=(Button)v;
    }

    //Manages the context menu choices
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals("Delete")){
            items.remove(contextMenuList.getText());
            updateItemsOnFile();
            populateLists();
        }
        else if(item.getTitle().equals("Bookmark")){
            LinkedHashMap<String,LinkedHashMap<String,Boolean>> tmp=new LinkedHashMap<>();
            tmp.put(contextMenuList.getText().toString(),items.get(contextMenuList.getText()));
            for(String key : items.keySet()){
                if(!key.equals(contextMenuList.getText().toString()))
                    tmp.put(key,items.get(key));
            }
            items=tmp;
            updateItemsOnFile();
            populateLists();

            //LA LISTA EVIDENZIATA DEVE AVERE UN COLORE DIVERSO, STILE DIVERSO ECC.
        }
        return true;
    }

    //To prevent from double clicking the row item and so prevents overlapping fragment.
    public static void avoidDoubleClicks(final View view) {
        final long DELAY_IN_MS = 900;
        if (!view.isClickable()) {
            return;
        }
        view.setClickable(false);
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setClickable(true);
            }
        }, DELAY_IN_MS);
    }

    private void updateItemsOnFile(){
        try {
            FileOutputStream outputStream = this.openFileOutput("items.dat", MODE_PRIVATE);
            ObjectOutputStream writer = new ObjectOutputStream(outputStream);
            writer.writeObject(items);
            writer.close();
            outputStream.close();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

}