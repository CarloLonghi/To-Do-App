package com.carlolonghi.todo.activities;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.widget.EditText;

import com.carlolonghi.todo.adapters.ItemsAdapter;
import com.carlolonghi.todo.others.MyItemTouchHelper;
import com.carlolonghi.todo.data.ItemsViewModel;
import com.carlolonghi.todo.R;


public class ItemsActivity extends AppCompatActivity {

    private String listTitle;
    private ItemsViewModel model;
    private RecyclerView.Adapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);
        RecyclerView myRecyclerView= findViewById(R.id.myRecyclerView);

        // use this setting to improve performance if you know that changes in content do not change the layout size of the RecyclerView
        myRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager myLayoutManager = new LinearLayoutManager(this);
        myRecyclerView.setLayoutManager(myLayoutManager);

        Intent intent = getIntent();

        //Gets the ViewModel that reads and holds the application data and read the Map of items
        this.model = ViewModelProviders.of(this).get(ItemsViewModel.class);

        //Gets the title of the list and set it to the Activity
        String title = intent.getStringExtra("com.carlolonghi.todo.TITLE");
        this.listTitle=title.toUpperCase();
        setTitle(listTitle.toUpperCase());

        // specify an adapter
        myAdapter = new ItemsAdapter(listTitle,model);
        myRecyclerView.setAdapter(myAdapter);

        ItemTouchHelper.Callback callback = new MyItemTouchHelper((MyItemTouchHelper.ItemTouchHelperAdapter)myAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(myRecyclerView);

        //Set the toolbar visible and sets its text as the title of the list
        getSupportActionBar().setTitle(this.listTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.drawable.actionbar_background));
    }


    //The function that regulates the behaviour of the back button that is on top-left of the screen
    public boolean onOptionsItemSelected(MenuItem item){
        //The button make the app go back to ListsFragment
        finish();
        return true;
    }

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

    }

    @Override
    protected void onPause(){
        super.onPause();

        //Save the items state on file using the ViewModel whenever the activity is paused
        model.updateItems(listTitle,((ItemsAdapter)myAdapter).getItems());
        model.updateItemsOnFile(this.getBaseContext());
    }
}


