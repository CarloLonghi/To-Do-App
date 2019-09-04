package com.carlolonghi.oneup.activities;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;

import com.carlolonghi.oneup.adapters.ItemsAdapter;
import com.carlolonghi.oneup.others.MyItemTouchHelper;
import com.carlolonghi.oneup.data.ItemsViewModel;
import com.carlolonghi.oneup.R;


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

        RecyclerView.LayoutManager myLayoutManager = new LinearLayoutManager(this);
        myRecyclerView.setLayoutManager(myLayoutManager);

        Intent intent = getIntent();

        this.model = ViewModelProviders.of(this).get(ItemsViewModel.class);

        // Gets the title of the list and set it to the Activity
        String title = intent.getStringExtra("com.carlolonghi.todo.TITLE");
        this.listTitle=title.toUpperCase();
        setTitle(listTitle.toUpperCase());

        myAdapter = new ItemsAdapter(listTitle,model);
        myRecyclerView.setAdapter(myAdapter);

        ItemTouchHelper.Callback callback = new MyItemTouchHelper((MyItemTouchHelper.ItemTouchHelperAdapter)myAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(myRecyclerView);

        // Set the toolbar visible and sets its text as the title of the list
        getSupportActionBar().setTitle(this.listTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.drawable.actionbar_background));
    }


    // The function that regulates the behaviour of the back button that is on top-left of the screen
    /*public boolean onOptionsItemSelected(MenuItem item){
        // The button make the app go back to MainActivity
        finish();
        return true;
    }*/

/*    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("LIST_TITLE", this.listTitle);
        EditText editText=(EditText)findViewById(R.id.reverseAddNewText);
        savedInstanceState.putString("EDITING_TEXT",editText.getText().toString());

        super.onSaveInstanceState(savedInstanceState);
    }
*/

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        this.listTitle = savedInstanceState.getString("LIST_TITLE");
        ((ItemsAdapter)myAdapter).setEditingText(savedInstanceState.getString("EDITING_TEXT"));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.delete_selected_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete:
                ((ItemsAdapter)myAdapter).deleteSelectedItems();
                break;
            default:
                finish();
                break;
        }
        return true;
    }

    @Override
    protected void onPause(){
        super.onPause();

        if(model.hasBookmark(listTitle)){
            model.updateBookmarkItems(listTitle,((ItemsAdapter)myAdapter).getItems());
            model.updateBookmarkItemsOnFile(this.getBaseContext());
        }
        else {
            model.updateItems(listTitle, ((ItemsAdapter) myAdapter).getItems());
            model.updateItemsOnFile(this.getBaseContext());
        }
    }
}


