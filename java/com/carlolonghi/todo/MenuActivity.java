package com.carlolonghi.todo;

import android.app.ActionBar;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.app.Activity;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

    private LinkedHashMap<String,Items> items;
    private MyViewModel model;
    private RecyclerView myRecyclerView;
    private RecyclerView.Adapter myAdapter;
    private RecyclerView.LayoutManager myLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        myRecyclerView=(RecyclerView) findViewById(R.id.listsView);
        registerForContextMenu(myRecyclerView);

        // use this setting to improve performance if you know that changes in content do not change the layout size of the RecyclerView
        myRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        myLayoutManager = new LinearLayoutManager(this);
        myRecyclerView.setLayoutManager(myLayoutManager);

        //Gets the ViewModel that reads and holds the application data and read the Map of items
        model = ViewModelProviders.of(this).get(MyViewModel.class);
        this.items=model.getItems();

        checkIfListIsEmpty();

        // specify an adapter (see also next example)
        myAdapter = new ListsAdapter(model,myLayoutManager);
        myRecyclerView.setAdapter(myAdapter);

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
        if(((ListsAdapter)myAdapter).isAddNewPresent()){
            EditText editText=(EditText)myLayoutManager.getChildAt(myAdapter.getItemCount()-1);
            savedInstanceState.putString("EDITING_TEXT",editText.getText().toString());
        }

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    //Restores the activity from the Instance State
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        ((ListsAdapter)myAdapter).setEditingText(savedInstanceState.getString("EDITING_TEXT"));
    }

    public void onClick(final View addButton) {
        disableAddButton();
        myRecyclerView.setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.emptyRecyclerViewText)).setVisibility(View.GONE);
        //Add the edittext where the user has to type the title of the new list
        ((ListsAdapter)myAdapter).setAddNewPresent(true);
        model.addList("addnew");
        myAdapter.notifyDataSetChanged();
    }

    public void enableAddButton(){
        Button addButton=(Button)findViewById(R.id.newListButton);
        addButton.setClickable(true);
    }

    public void disableAddButton(){
        Button addButton=(Button)findViewById(R.id.newListButton);
        addButton.setClickable(false);
    }

    //Manages the context menu choices
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Button contextMenuList=((ListsAdapter)myAdapter).getContextMenuList();
        if(item.getTitle().equals("Delete")){
            model.removeList(contextMenuList.getText().toString().toUpperCase());
            myAdapter.notifyDataSetChanged();
            checkIfListIsEmpty();
        }
        else if(item.getTitle().equals("Bookmark")){
            //LA LISTA EVIDENZIATA DEVE AVERE UN COLORE DIVERSO, STILE DIVERSO ECC.
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if(((ListsAdapter)myAdapter).isAddNewPresent()){


            //Delete the add editText and Button from layout
            ((ListsAdapter)myAdapter).removeAddNew();
            ((ListsAdapter)myAdapter).setAddNewPresent(false);

            //Reactivate the newListButton
            enableAddButton();

            checkIfListIsEmpty();
        }
        else
            super.onBackPressed();
    }

    //If the list is empty it shows the emptylist message
    private void checkIfListIsEmpty(){
        if(model.isEmpty()){
            myRecyclerView.setVisibility(View.GONE);
            ((TextView)findViewById(R.id.emptyRecyclerViewText)).setVisibility(View.VISIBLE);
        }
        else{
            ((TextView)findViewById(R.id.emptyRecyclerViewText)).setVisibility(View.GONE);
            myRecyclerView.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen

    }

    @Override
    protected void onPause(){
        super.onPause();


        ((ListsAdapter)myAdapter).removeAddNew();
        ((ListsAdapter)myAdapter).setAddNewPresent(false);
        enableAddButton();

        //Save the items state on file using the ViewModel whenever the activity is paused
        model.updateItemsOnFile(this.getBaseContext());
    }
}