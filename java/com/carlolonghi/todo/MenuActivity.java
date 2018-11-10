package com.carlolonghi.todo;

import android.app.ActionBar;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.app.Activity;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.text.Layout;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.inputmethod.InputMethodManager;
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

    private Map<String,Items> items;
    private MyViewModel model;

    //A variable used to remember the button wich has been long-pressed to show the menu
    private Button contextMenuList;

    //The boolean variable that says if the EditText and Button for the new list are present in the activity
    private boolean isPresent=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //Gets the ViewModel that reads and holds the application data and read the Map of items
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
            //For every list of items in the file adds a new Button with its name
            for (String list : items.keySet()) {
                ViewGroup insertPoint = (LinearLayout) findViewById(R.id.ListTitles);
                Button newItemAddedButton = new Button(this);
                newItemAddedButton.setText(list);

                registerForContextMenu(newItemAddedButton);

                //Sets the layout params for the list of items
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

        //read Items From File
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

        //Restore views' state from saved instance
        ViewGroup insertPoint = (ViewGroup) findViewById(R.id.ListTitles);
        if((isPresent=savedInstanceState.getBoolean("isPresent"))) {
            //Add the edittext where the user has to type the title of the new list
            LinearLayout newListLayout=new LinearLayout(this);
            newListLayout.setOrientation(LinearLayout.HORIZONTAL);
            EditText newListText=new EditText(this);
            Button newListButton=new Button(this);

            //The layout params for the LinearLayout that contains the EditText and AddButton
            LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(48,48,48,0);

            //The layout params for the EditText
            LinearLayout.LayoutParams layoutParams1=new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,13F);
            layoutParams1.setMargins(0,0,15,0);

            //The layout params for the Add Button
            LinearLayout.LayoutParams layoutParams2=new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,1F);
            layoutParams2.setMargins(0,0,0,0);

            //Add the EditText, Add Button and their container to the activity
            newListButton.setWidth(0); newListButton.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
            newListText.setWidth(0); newListButton.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
            newListButton.setText("ADD");
            newListLayout.addView(newListText,layoutParams1);
            newListLayout.addView(newListButton,layoutParams2);
            insertPoint.addView(newListLayout,layoutParams);

            //Restores the text contained in the EditText when it was destroyed
            String txt = savedInstanceState.getString("ENTERING_TEXT");
            newListText.setText(txt);
            newListText.setSelection(txt.length());
            //Sets the listener for the Add Button
            newListButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setEnabled(false);

                    //Get the title and remove the edittext
                    ViewGroup insertPoint = (ViewGroup) findViewById(R.id.ListTitles);
                    LinearLayout container=(LinearLayout)insertPoint.getChildAt(insertPoint.getChildCount()-1);
                    EditText editText=(EditText)container.getChildAt(0);
                    String newListTitle = editText.getText().toString();

                    //Checks if the new newList name already exists
                    if(items.keySet().contains(newListTitle.toUpperCase())){
                        Context context = getApplicationContext();
                        CharSequence text = "List already exists";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                    }
                    //Add the new list if everything is ok
                    else if(!newListTitle.equals("")) {
                        //Delete the add editText and Button from layout
                        ((ViewManager) editText.getParent()).removeView(editText);
                        ((ViewManager) container.getParent()).removeView(container);
                        ((ViewManager) v.getParent()).removeView(v);
                        isPresent = false;

                        //Add the new list to the page
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

                        items.put(newListTitle.toUpperCase(), new Items());
                        updateItemsOnFile();

                        //Reactivate the newListButton
                        Button addButton = (Button) findViewById(R.id.newListButton);
                        addButton.setClickable(true);

                        //open the list of items
                        Intent intent = new Intent(newItemAddedButton.getContext(), MainActivity.class);
                        String list = ((Button) newItemAddedButton).getText().toString();
                        intent.putExtra("com.carlolonghi.todo.TITLE", list);
                        newItemAddedButton.getContext().startActivity(intent);
                    }
                    //Checks if the newList has an empty name
                    else{
                        Context context = getApplicationContext();
                        CharSequence text = "You can't add an empty list";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                    }
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
        final Button newListButton=new Button(this);

        //The layout params for the LinearLayout that contains the EditText and AddButton
        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(48,48,48,0);

        //The layout params for the EditText
        LinearLayout.LayoutParams layoutParams1=new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,13F);
        layoutParams1.setMargins(0,0,15,0);

        //The layout params for the Add Button
        LinearLayout.LayoutParams layoutParams2=new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,1F);
        layoutParams2.setMargins(0,0,0,0);

        //Add the EditText, Add Button and their container to the activity
        newListButton.setWidth(0); newListButton.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        newListText.setWidth(0); newListButton.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        newListButton.setText("ADD");
        newListLayout.addView(newListText,layoutParams1);
        newListLayout.addView(newListButton,layoutParams2);
        ViewGroup insertPoint = (ViewGroup) findViewById(R.id.ListTitles);
        insertPoint.addView(newListLayout,layoutParams);
        isPresent=true;
        //Sets the cursor on the edittext and opens the keyboard
        newListText.requestFocus();
        Activity activity = (Activity) this;
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(newListText, InputMethodManager.SHOW_IMPLICIT);
        //Sets the listener for the Add Button
        newListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);

                //Get the title and remove the edittext
                ViewGroup insertPoint = (ViewGroup) findViewById(R.id.ListTitles);
                LinearLayout container=(LinearLayout)insertPoint.getChildAt(insertPoint.getChildCount()-1);
                EditText editText=(EditText)container.getChildAt(0);
                String newListTitle = editText.getText().toString();

                if(items.keySet().contains(newListTitle.toUpperCase())){
                    Context context = getApplicationContext();
                    CharSequence text = "List already exists";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                }
                else if(!newListTitle.equals("")) {
                    //Delete the add editText and Button from layout
                    ((ViewManager) editText.getParent()).removeView(editText);
                    ((ViewManager) container.getParent()).removeView(container);
                    ((ViewManager) v.getParent()).removeView(v);
                    isPresent = false;

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

                    items.put(newListTitle.toUpperCase(), new Items());
                    updateItemsOnFile();

                    //Reactivate the newListButton
                    addButton.setClickable(true);

                    //open the list of items
                    Intent intent = new Intent(newItemAddedButton.getContext(), MainActivity.class);
                    String list = ((Button) newItemAddedButton).getText().toString();
                    intent.putExtra("com.carlolonghi.todo.TITLE", list);
                    newItemAddedButton.getContext().startActivity(intent);
                }
                else{
                    Context context = getApplicationContext();
                    CharSequence text = "You can't add an empty list";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                }
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
            items.remove(contextMenuList.getText().toString().toUpperCase());
            updateItemsOnFile();
            populateLists();
        }
        else if(item.getTitle().equals("Bookmark")){
            LinkedHashMap<String,Items> tmp=new LinkedHashMap<>();
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

    @Override
    public void onBackPressed() {
        if(isPresent){
            ViewGroup insertPoint = (ViewGroup) findViewById(R.id.ListTitles);
            LinearLayout container=(LinearLayout)insertPoint.getChildAt(insertPoint.getChildCount()-1);
            EditText editText=(EditText)container.getChildAt(0);
            Button button=(Button)container.getChildAt(1);

            //Delete the add editText and Button from layout
            ((ViewManager) editText.getParent()).removeView(editText);
            ((ViewManager) container.getParent()).removeView(container);
            ((ViewManager) button.getParent()).removeView(button);
            isPresent = false;

            //Reactivate the newListButton
            Button addButton=(Button)findViewById(R.id.newListButton);
            addButton.setClickable(true);
        }
        else
            super.onBackPressed();
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

    //To prevent from double clicking a button
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

}