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
        this.items=model.getItems();

        //Gets the title of the list and set it to the Activity
        Intent intent = getIntent();
        String title = intent.getStringExtra("com.carlolonghi.todo.TITLE");
        this.listTitle=title.toUpperCase();
        setTitle(listTitle.toUpperCase());

        // specify an adapter (see also next example)
        myAdapter = new ItemsAdapter(items,listTitle);
        myRecyclerView.setAdapter(myAdapter);

       /* //This block of instructions regulates the correct behaviour of the EditText used to add the new items
        //the text goes newline automatically when gets to the end of it and when the newline button on the keyboard is pressed
        //the item is added to the list as the Add button has been pressed
        EditText newItem=(EditText)findViewById(R.id.addNewText);
        newItem.setHorizontallyScrolling(false);
        newItem.setMaxLines(Integer.MAX_VALUE);
        newItem.setRawInputType(InputType.TYPE_CLASS_TEXT);
        newItem.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch(actionId){
                    case EditorInfo.IME_ACTION_DONE:
                        //Calls the onClick of the Add button if the newLine is pressed
                        onClick(findViewById(R.id.addNewButton));
                        return true;
                    default:
                        return false;
                }
            }
        });*/
    }

    //The function that regulates the behaviour of the back button that is on top-left of the screen
    public boolean onOptionsItemSelected(MenuItem item){
        //The button make the app go back to MenuActivity
        Intent myIntent = new Intent(getApplicationContext(), MenuActivity.class);
        startActivityForResult(myIntent, 0);
        return true;
    }

    @Override
    public void onResume(){
        super.onResume();

        //Whenever the activity is resumed we need to update the NonChekedItemsFragment's height to make sure every item is visible
        //nonCheckedItemsFragment.updateFragmentHeight(nonCheckedItemsFragment.getListView());
    }

   /* //This method saves an InstanceState when the activity is destroyed
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current state
        savedInstanceState.putString("LIST_TITLE", this.listTitle);
        EditText editText=(EditText)findViewById(R.id.addNewText);
        savedInstanceState.putString("ENTERING_TEXT", editText.getText().toString());

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    //Restores the activity from the Instance State
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        // Restore views' state from saved instance
        this.listTitle = savedInstanceState.getString("LIST_TITLE");
        EditText editText=(EditText)findViewById(R.id.addNewText);
        String txt=savedInstanceState.getString("ENTERING_TEXT");
        editText.setText(txt);
        editText.setSelection(txt.length());
    }*/

    /*//Handles the click of the button that adds the new item
    public void onClick(View view){
        //Gets the newItem name from the EditText
        EditText newItemField=(EditText) findViewById(R.id.addNewText);
        String newItem=newItemField.getText().toString();

        //Checks if the newItem has an empty name
        if(newItem.equals("")){
            Context context = getApplicationContext();
            CharSequence text = "You can't add an empty item";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
        //Checks if the new newItem name already exists
        else if(items.get(listTitle).getNonCheckedItems().contains(newItem)){
            Context context = getApplicationContext();
            CharSequence text = "Item already exists";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
        //Add the new item if everything is ok
        else {
            //Save the new item
            items.get(listTitle).addNonCheckedItem(newItem);
            model.updateItemsOnFile(this.getBaseContext());

            //Add the new item to the fragment here
            FrameLayout frameLayout=(FrameLayout)findViewById(R.id.fragmentContainer);
            NonCheckedItemsFragment nonCheckedItemsFragment=(NonCheckedItemsFragment)getSupportFragmentManager().findFragmentById(frameLayout.getId());
            nonCheckedItemsFragment.addItem(newItem);

            //Cancel the just added item name from the editText
            newItemField.setText("");
        }
    }*/

    @Override
    protected void onPause(){
        super.onPause();

        //Save the items state on file using the ViewModel whenever the activity is paused
        model.updateItemsOnFile(this.getBaseContext());
    }
}


