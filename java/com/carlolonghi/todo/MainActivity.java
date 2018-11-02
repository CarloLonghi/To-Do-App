package com.carlolonghi.todo;


import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.LineNumberInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import android.arch.lifecycle.ViewModelProviders;



public class MainActivity extends FragmentActivity {

    private String listTitle;
    private Map<String,Items> items;
    private MyViewModel model;
    private RecyclerView myRecyclerView;
    private RecyclerView.Adapter myAdapter;
    private RecyclerView.LayoutManager myLayoutManager;
    private String editingText="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myRecyclerView=(RecyclerView) findViewById(R.id.myRecyclerView);

        // use this setting to improve performance if you know that changes in content do not change the layout size of the RecyclerView
        myRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        myLayoutManager = new LinearLayoutManager(this);
        myRecyclerView.setLayoutManager(myLayoutManager);

        //Gets the ViewModel that reads and holds the application data and read the Map of items
        this.model = ViewModelProviders.of(this).get(MyViewModel.class);
        this.items=model.loadItems();

        //Gets the title of the list and set it to the Activity
        Intent intent = getIntent();
        String title = intent.getStringExtra("com.carlolonghi.todo.TITLE");
        this.listTitle=title.toUpperCase();
        setTitle(listTitle.toUpperCase());

        // specify an adapter
        myAdapter = new ItemsAdapter(listTitle,model);
        myRecyclerView.setAdapter(myAdapter);

        ItemTouchHelper.Callback callback = new MyItemTouchHelper((MyItemTouchHelper.ItemTouchHelperAdapter)myAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(myRecyclerView);
    }

    //The function that regulates the behaviour of the back button that is on top-left of the screen
    public boolean onOptionsItemSelected(MenuItem item){
        //The button make the app go back to MenuActivity
        Intent myIntent = new Intent(getApplicationContext(), MenuActivity.class);
        startActivityForResult(myIntent, 0);
        return true;
    }

/*    //This method saves an InstanceState when the activity is destroyed
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);

        // Save the user's current state
        EditText editText=(EditText)((LinearLayout)myRecyclerView.getChildAt(items.get(listTitle).getNonCheckedItems().size())).getChildAt(0);
        myLayoutManager.onSaveInstanceState();
        savedInstanceState.putString("ENTERING_TEXT", editText.getText().toString());
    }

    //Restores the activity from the Instance State
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);
        //Restores the text that the user was entering in the edittext
        String text=savedInstanceState.getString("ENTERING_TEXT");

    }*/

    //This method saves an InstanceState when the activity is destroyed
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current state
        savedInstanceState.putString("LIST_TITLE", this.listTitle);
        EditText editText=(EditText)findViewById(R.id.addNewText);
        savedInstanceState.putString("EDITING_TEXT",editText.getText().toString());

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    //Restores the activity from the Instance State
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        // Restore views' state from saved instance
        this.listTitle = savedInstanceState.getString("LIST_TITLE");
        ((ItemsAdapter)myAdapter).setEditingText(savedInstanceState.getString("EDITING_TEXT"));

        // EditText editText=(EditText)findViewById(R.id.addNewText);
        //String txt=savedInstanceState.getString("ENTERING_TEXT");
        //editText.setText(txt);
        //editText.setSelection(txt.length());
    }

    @Override
    protected void onPause(){
        super.onPause();

        //Save the items state on file using the ViewModel whenever the activity is paused
        model.updateItemsOnFile(this.getBaseContext());
    }
}


