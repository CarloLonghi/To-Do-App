package com.carlolonghi.todo;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.LinkedHashMap;


public class MenuActivity extends Fragment implements View.OnClickListener {

    private LinkedHashMap<String,Items> items;
    private ItemsViewModel model;
    private RecyclerView myRecyclerView;
    private RecyclerView.Adapter myAdapter;
    private RecyclerView.LayoutManager myLayoutManager;
    private ViewGroup rootView;

    //The spacing between lists in the recyclerview
    private final int SPACE_BETWEEN_LISTS=40;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView=(ViewGroup) inflater.inflate(R.layout.fragment_menu,container,false);

        myRecyclerView=(RecyclerView) rootView.findViewById(R.id.listsView);
        registerForContextMenu(myRecyclerView);

        // use this setting to improve performance if you know that changes in content do not change the layout size of the RecyclerView
        myRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        myLayoutManager = new LinearLayoutManager(this.getContext());
        myRecyclerView.setLayoutManager(myLayoutManager);

        //Gets the ViewModel that reads and holds the application data and read the Map of items
        model = ViewModelProviders.of(this).get(ItemsViewModel.class);

        checkIfListIsEmpty();

        // specify an adapter (see also next example)
        myAdapter = new ListsAdapter(model,myLayoutManager);
        myRecyclerView.setAdapter(myAdapter);

        // sets a vertical space between the recyclerview items
        class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {
            private final int verticalSpaceHeight;

            public VerticalSpaceItemDecoration(int verticalSpaceHeight) {
                this.verticalSpaceHeight = verticalSpaceHeight;
            }

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                       RecyclerView.State state) {
                outRect.bottom = verticalSpaceHeight;
            }
        }
        myRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(SPACE_BETWEEN_LISTS));

        //Sets the listener for the button used to add a new list
        Button addNewButton=(Button)rootView.findViewById(R.id.newListButton);
        addNewButton.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();

        //Loads the updated items from file
        this.model.loadItems();

        enableAddButton();
    }

    //Restores the activity from the Instance State
    public void onActivityCreated(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState!=null)
            ((ListsAdapter)myAdapter).setEditingText(savedInstanceState.getString("EDITING_TEXT"));
    }

    public void onClick(final View addButton) {
        disableAddButton();
        myRecyclerView.setVisibility(View.VISIBLE);
        ((TextView)rootView.findViewById(R.id.emptyRecyclerViewText)).setVisibility(View.GONE);
        //Add the edittext where the user has to type the title of the new list
        ((ListsAdapter)myAdapter).setAddNewPresent(true);
        model.addList("addnew");
        myAdapter.notifyDataSetChanged();
        myRecyclerView.scrollToPosition(model.getKeySet().size()-1);
    }

    public void enableAddButton(){
        Button addButton=(Button)rootView.findViewById(R.id.newListButton);
        addButton.setClickable(true);
    }

    public void disableAddButton(){
        Button addButton=(Button)rootView.findViewById(R.id.newListButton);
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

    //If the list is empty it shows the emptylist message
    private void checkIfListIsEmpty(){
        if(model.isEmpty()){
            myRecyclerView.setVisibility(View.GONE);
            ((TextView)rootView.findViewById(R.id.emptyRecyclerViewText)).setVisibility(View.VISIBLE);
        }
        else{
            ((TextView)rootView.findViewById(R.id.emptyRecyclerViewText)).setVisibility(View.GONE);
            myRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPause(){
        super.onPause();

        //Removes the addNew field used to add a new list
        ((ListsAdapter)myAdapter).removeAddNew();
        ((ListsAdapter)myAdapter).setAddNewPresent(false);
        enableAddButton();

        //Save the items state on file using the ViewModel whenever the activity is paused
        model.updateItemsOnFile(this.getActivity().getBaseContext());
    }
}