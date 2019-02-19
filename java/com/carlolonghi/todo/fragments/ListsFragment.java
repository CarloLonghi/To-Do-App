package com.carlolonghi.todo.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.carlolonghi.todo.adapters.ListsAdapter;
import com.carlolonghi.todo.data.ItemsViewModel;
import com.carlolonghi.todo.adapters.BMListsAdapter;
import com.carlolonghi.todo.R;


public class ListsFragment extends Fragment implements View.OnClickListener {

    private ItemsViewModel model;
    private RecyclerView listsRecyclerView;
    private RecyclerView bmListsRecyclerView;
    private RecyclerView.Adapter listsAdapter;
    private RecyclerView.Adapter bmListsAdapter;
    private RecyclerView.LayoutManager listsLayoutManager;
    private RecyclerView.LayoutManager bmListsLayoutManager;
    private ViewGroup rootView;

    //The spacing between lists in the recyclerview
    private final int SPACE_BETWEEN_LISTS=40;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView=(ViewGroup) inflater.inflate(R.layout.fragment_menu,container,false);

        listsRecyclerView=(RecyclerView) rootView.findViewById(R.id.listsView);
        registerForContextMenu(listsRecyclerView);

        bmListsRecyclerView =(RecyclerView)rootView.findViewById(R.id.bookmarklistsView);
        registerForContextMenu(bmListsRecyclerView);

        // use this setting to improve performance if you know that changes in content do not change the layout size of the RecyclerView
        listsRecyclerView.setHasFixedSize(true);
        bmListsRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        listsLayoutManager = new LinearLayoutManager(this.getContext());
        listsRecyclerView.setLayoutManager(listsLayoutManager);
        bmListsLayoutManager=new LinearLayoutManager(this.getContext());
        bmListsRecyclerView.setLayoutManager(bmListsLayoutManager);

        //Gets the ViewModel that reads and holds the application data and read the Map of items
        model = ViewModelProviders.of(this).get(ItemsViewModel.class);

        checkIfItemsAreEmpty();

        // specify an adapter (see also next example)
        listsAdapter = new ListsAdapter(model, listsLayoutManager,this.getContext());
        listsRecyclerView.setAdapter(listsAdapter);
        bmListsAdapter=new BMListsAdapter(model,bmListsLayoutManager,this.getContext());
        bmListsRecyclerView.setAdapter(bmListsAdapter);

        // sets a vertical space between the recyclerview items
        class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {
            private final int verticalSpaceHeight;

            private VerticalSpaceItemDecoration(int verticalSpaceHeight) {
                this.verticalSpaceHeight = verticalSpaceHeight;
            }

            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent,
                                       @NonNull RecyclerView.State state) {
                outRect.bottom = verticalSpaceHeight;
            }
        }
        listsRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(SPACE_BETWEEN_LISTS));
        bmListsRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(SPACE_BETWEEN_LISTS));

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
            ((ListsAdapter)listsAdapter).setEditingText(savedInstanceState.getString("EDITING_TEXT"));
    }

    //The listener for the button used to get the dialog to add a new list
    public void onClick(final View addButton) {
        disableAddButton();
        listsRecyclerView.setVisibility(View.VISIBLE);
        ((TextView)rootView.findViewById(R.id.emptyRecyclerViewText)).setVisibility(View.GONE);
        //Add the edittext where the user has to type the title of the new list
        ((ListsAdapter)listsAdapter).setAddNewPresent(true);
        model.addList("addnew");
        listsAdapter.notifyDataSetChanged();
        listsRecyclerView.scrollToPosition(model.getKeySet().size()-1);
    }

    private void enableAddButton(){
        Button addButton=(Button)rootView.findViewById(R.id.newListButton);
        addButton.setClickable(true);
    }

    private void disableAddButton(){
        Button addButton=(Button)rootView.findViewById(R.id.newListButton);
        addButton.setClickable(false);
    }

    //Manages the context menu choices
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Button contextMenuList=((ListsAdapter)listsAdapter).getContextMenuList();
        Button bmContextMenuList=((BMListsAdapter)bmListsAdapter).getContextMenuList();
        if(item.getTitle().equals("Delete")){
            if(contextMenuList==null || contextMenuList.getText().toString().equals(""))
                model.removeBMList(bmContextMenuList.getText().toString().toUpperCase());
            else
                model.removeList(contextMenuList.getText().toString().toUpperCase());
            listsAdapter.notifyDataSetChanged();
            checkIfItemsAreEmpty();
        }
        else if(item.getTitle().equals("Bookmark")){
            model.bookmarkList(contextMenuList.getText().toString().toUpperCase());
            listsAdapter.notifyDataSetChanged();
            bmListsAdapter.notifyDataSetChanged();
            checkIfItemsAreEmpty();
        }
        else if(item.getTitle().equals("Remove Bookmark")){
            model.unbookmarkList(bmContextMenuList.getText().toString().toUpperCase());
            listsAdapter.notifyDataSetChanged();
            bmListsAdapter.notifyDataSetChanged();
            checkIfItemsAreEmpty();
        }
        ((ListsAdapter)listsAdapter).deleteContextMenuList();
        ((BMListsAdapter)bmListsAdapter).deleteContextMenuList();

        return true;
    }

    //Regulates the presence of the "Others" title and of the empty Activity string
    private void checkIfItemsAreEmpty(){
        if(model.itemsIsEmpty() && model.bmItemsIsEmpty()){
            listsRecyclerView.setVisibility(View.GONE);
            bmListsRecyclerView.setVisibility(View.GONE);
            ((TextView)rootView.findViewById(R.id.emptyRecyclerViewText)).setVisibility(View.VISIBLE);
            ((TextView)rootView.findViewById(R.id.bookmarkTextView)).setVisibility(View.GONE);
        }
        if(!model.itemsIsEmpty() && model.bmItemsIsEmpty()){
            listsRecyclerView.setVisibility(View.VISIBLE);
            bmListsRecyclerView.setVisibility(View.GONE);
            ((TextView)rootView.findViewById(R.id.emptyRecyclerViewText)).setVisibility(View.GONE);
            ((TextView)rootView.findViewById(R.id.bookmarkTextView)).setVisibility(View.GONE);
        }
        if(model.itemsIsEmpty() && !model.bmItemsIsEmpty()){
            listsRecyclerView.setVisibility(View.GONE);
            bmListsRecyclerView.setVisibility(View.VISIBLE);
            ((TextView)rootView.findViewById(R.id.emptyRecyclerViewText)).setVisibility(View.GONE);
            ((TextView)rootView.findViewById(R.id.bookmarkTextView)).setVisibility(View.VISIBLE);
        }
        if(!model.itemsIsEmpty() && !model.bmItemsIsEmpty()){
            listsRecyclerView.setVisibility(View.VISIBLE);
            bmListsRecyclerView.setVisibility(View.VISIBLE);
            ((TextView)rootView.findViewById(R.id.emptyRecyclerViewText)).setVisibility(View.GONE);
            ((TextView)rootView.findViewById(R.id.bookmarkTextView)).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPause(){
        super.onPause();

        //Removes the addNew field used to add a new list
        ((ListsAdapter)listsAdapter).removeAddNew();
        ((ListsAdapter)listsAdapter).setAddNewPresent(false);
        enableAddButton();

        //Save the items state on file using the ViewModel whenever the activity is paused
        model.updateItemsOnFile(this.getActivity().getBaseContext());
        model.updateBookmarkItemsOnFile(this.getActivity().getBaseContext());
    }
}