package com.carlolonghi.todo;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
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

public class MenuActivity extends FragmentActivity {

    SharedPreferences prefs = null;
    private Map<String,LinkedHashMap<String,Boolean>> items;
    private Button contextMenuList;
    private MyViewModel model;
    private boolean isPresent=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        model = ViewModelProviders.of(this).get(MyViewModel.class);
        this.items=model.getItems();

        populateLists();
    }

    private void populateLists(){
        items=new LinkedHashMap<>();
        try {
            //readItemsFromFile();
            items=model.getItems();
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

        //readItemsFromFile();
        items=model.getItems();
    }

    //This method saves an InstanceState when the activity is destroyed
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current state
        savedInstanceState.putBoolean("isPresent",isPresent);
        if(isPresent) {
            ViewGroup insertPoint = (ViewGroup) findViewById(R.id.ListTitles);
            LinearLayout container = (LinearLayout) insertPoint.getChildAt(insertPoint.getChildCount() - 1);
            EditText editText = (EditText) container.getChildAt(0);
            savedInstanceState.putString("ENTERING_TEXT", editText.getText().toString());
        }

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    //Restores the activity from the Instance State
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        // Restore state members from saved instance
        ViewGroup insertPoint = (ViewGroup) findViewById(R.id.ListTitles);
        if((isPresent=savedInstanceState.getBoolean("isPresent"))) {
            //Add the edittext where the user has to type the title of the new list
            LinearLayout newListLayout=new LinearLayout(this);
            newListLayout.setOrientation(LinearLayout.HORIZONTAL);
            EditText newListText=new EditText(this);
            Button newListButton=new Button(this);
            LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(48,48,48,0);

            LinearLayout.LayoutParams layoutParams1=new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,13F);
            layoutParams1.setMargins(0,0,15,0);

            LinearLayout.LayoutParams layoutParams2=new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,1F);
            layoutParams2.setMargins(0,0,0,0);
            newListButton.setWidth(0); newListButton.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
            newListText.setWidth(0); newListButton.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
            newListButton.setText("ADD");
            newListLayout.addView(newListText,layoutParams1);
            newListLayout.addView(newListButton,layoutParams2);
            insertPoint.addView(newListLayout,layoutParams);
            String txt = savedInstanceState.getString("ENTERING_TEXT");
            newListText.setText(txt);
            newListText.setSelection(txt.length());
            newListButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Button addButton=(Button)findViewById(R.id.newListButton);
                    addButton.setClickable(true);

                    //Get the title and remove the edittext
                    //EditText editText = (EditText) findViewById(R.id.newListToAdd);
                    ViewGroup insertPoint = (ViewGroup) findViewById(R.id.ListTitles);
                    LinearLayout container=(LinearLayout)insertPoint.getChildAt(insertPoint.getChildCount()-1);
                    EditText editText=(EditText)container.getChildAt(0);
                    String newListTitle = editText.getText().toString();

                    //Delete the add editText and Button from layout
                    ((ViewManager) editText.getParent()).removeView(editText);
                    ((ViewManager) container.getParent()).removeView(container);
                    ((ViewManager) v.getParent()).removeView(v);
                    isPresent=false;

                    //Add the new list to the page
                    //ViewGroup insertPoint = (LinearLayout) findViewById(R.id.ListTitles);
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
    }

    @Override
    public void onPause(){
        super.onPause();

        this.updateItemsOnFile();
    }

    public void onClick(final View addButton) {
        addButton.setClickable(false);

        //Add the edittext where the user has to type the title of the new list
        LinearLayout newListLayout=new LinearLayout(this);
        newListLayout.setOrientation(LinearLayout.HORIZONTAL);
        EditText newListText=new EditText(this);
        Button newListButton=new Button(this);
        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(48,48,48,0);

        LinearLayout.LayoutParams layoutParams1=new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,13F);
        layoutParams1.setMargins(0,0,15,0);

        LinearLayout.LayoutParams layoutParams2=new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,1F);
        layoutParams2.setMargins(0,0,0,0);
        newListButton.setWidth(0); newListButton.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        newListText.setWidth(0); newListButton.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        newListButton.setText("ADD");
        newListLayout.addView(newListText,layoutParams1);
        newListLayout.addView(newListButton,layoutParams2);
        ViewGroup insertPoint = (ViewGroup) findViewById(R.id.ListTitles);
        insertPoint.addView(newListLayout,layoutParams);
        isPresent=true;

        newListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addButton.setClickable(true);

                //Get the title and remove the edittext
                //EditText editText = (EditText) findViewById(R.id.newListToAdd);
                ViewGroup insertPoint = (ViewGroup) findViewById(R.id.ListTitles);
                LinearLayout container=(LinearLayout)insertPoint.getChildAt(insertPoint.getChildCount()-1);
                EditText editText=(EditText)container.getChildAt(0);
                String newListTitle = editText.getText().toString();

                //Delete the add editText and Button from layout
                ((ViewManager) editText.getParent()).removeView(editText);
                ((ViewManager) container.getParent()).removeView(container);
                ((ViewManager) v.getParent()).removeView(v);
                isPresent=false;

                //Add the new list to the page
                //ViewGroup insertPoint = (LinearLayout) findViewById(R.id.ListTitles);
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

    private void newListInsertMode(){
        //Add the edittext where the user has to type the title of the new list
        LinearLayout newListLayout=new LinearLayout(this);
        newListLayout.setOrientation(LinearLayout.HORIZONTAL);
        EditText newListText=new EditText(this);
        Button newListButton=new Button(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(48, 48, 48, 0);
        newListLayout.addView(newListText,layoutParams);
        newListLayout.addView(newListButton,layoutParams);
        ViewGroup insertPoint = (ViewGroup) findViewById(R.id.ListTitles);
        insertPoint.addView(newListLayout);
        isPresent=true;
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