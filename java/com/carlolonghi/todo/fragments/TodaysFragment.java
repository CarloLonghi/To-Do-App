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
    private RecyclerView.Adapter myAdapter;
    private ViewGroup rootView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(
                R.layout.activity_items, container, false);

        RecyclerView myRecyclerView=(RecyclerView) rootView.findViewById(R.id.myRecyclerView);

        // use this setting to improve performance if you know that changes in content do not change the layout size of the RecyclerView
        myRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager myLayoutManager = new LinearLayoutManager(this.getContext());
        myRecyclerView.setLayoutManager(myLayoutManager);

        this.model = ViewModelProviders.of(this).get(ItemsViewModel.class);

        myAdapter = new TodayItemsAdapter(model,this.getContext());
        myRecyclerView.setAdapter(myAdapter);

        ItemTouchHelper.Callback callback = new MyItemTouchHelper((MyItemTouchHelper.ItemTouchHelperAdapter)myAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(myRecyclerView);

        ((TodayItemsAdapter)myAdapter).getItems().updateCheckedItems();
        ((TodayItemsAdapter)myAdapter).getItems().updateNonCheckedItems();

        return rootView;
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        EditText editText=(EditText)rootView.findViewById(R.id.addNewText);
        savedInstanceState.putString("EDITING_TEXT",editText.getText().toString());

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState!=null){
            ((TodayItemsAdapter)myAdapter).setEditingText(savedInstanceState.getString("EDITING_TEXT"));
        }
    }

    @Override
    public void onPause(){

        model.updateTodaysItems(((TodayItemsAdapter)myAdapter).getItems());
        model.updateTodaysItemsOnFile(this.getActivity().getBaseContext());

        super.onPause();
    }
}
