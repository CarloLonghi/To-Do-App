package com.carlolonghi.todo.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.carlolonghi.todo.adapters.TodayItemsAdapter;
import com.carlolonghi.todo.others.MyItemTouchHelper;
import com.carlolonghi.todo.data.ItemsViewModel;
import com.carlolonghi.todo.R;

public class TodaysFragment extends Fragment {

    private ItemsViewModel model;
    private RecyclerView myRecyclerView;
    private RecyclerView.Adapter myAdapter;
    private RecyclerView.LayoutManager myLayoutManager;
    private String editingText="";
    private ViewGroup rootView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(
                R.layout.activity_items, container, false);

        myRecyclerView=(RecyclerView) rootView.findViewById(R.id.myRecyclerView);

        // use this setting to improve performance if you know that changes in content do not change the layout size of the RecyclerView
        myRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        myLayoutManager = new LinearLayoutManager(this.getContext());
        myRecyclerView.setLayoutManager(myLayoutManager);

        //Gets the ViewModel that reads and holds the application data and read the Map of items
        this.model = ViewModelProviders.of(this).get(ItemsViewModel.class);

        // specify an adapter
        myAdapter = new TodayItemsAdapter(model,this.getContext());
        myRecyclerView.setAdapter(myAdapter);

        ItemTouchHelper.Callback callback = new MyItemTouchHelper((MyItemTouchHelper.ItemTouchHelperAdapter)myAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(myRecyclerView);

        //Update the checked and unchecked today items
        ((TodayItemsAdapter)myAdapter).getItems().updateCheckedItems();
        ((TodayItemsAdapter)myAdapter).getItems().updateNonCheckedItems();

        return rootView;
    }


    //This method saves an InstanceState when the activity is destroyed
    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        // Save the user's current state
        EditText editText=(EditText)rootView.findViewById(R.id.addNewText);
        savedInstanceState.putString("EDITING_TEXT",editText.getText().toString());

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    //Restores the activity from the Instance State
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState!=null){
            // Restore views' state from saved instance
            ((TodayItemsAdapter)myAdapter).setEditingText(savedInstanceState.getString("EDITING_TEXT"));
        }
    }

    @Override
    public void onPause(){

        //Save the items state on file using the ViewModel whenever the activity is paused
        model.updateTodaysItems(((TodayItemsAdapter)myAdapter).getItems());
        model.updateTodaysItemsOnFile(this.getActivity().getBaseContext());

        super.onPause();
    }
}