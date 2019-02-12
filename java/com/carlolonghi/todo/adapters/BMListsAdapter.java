package com.carlolonghi.todo.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.carlolonghi.todo.data.ItemsViewModel;
import com.carlolonghi.todo.others.ListButtonListener;
import com.carlolonghi.todo.R;

import java.util.ArrayList;

public class BMListsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final ItemsViewModel model;
    private Button contextMenuList;
    private final RecyclerView.LayoutManager myLayoutManager;

    private static final int NEWLIST_TYPE = 1;

    private static class ListViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        private final Button myListButton;

        private ListViewHolder(Button listButton) {
            super(listButton);
            myListButton = listButton;
            myListButton.setOnCreateContextMenuListener(this);
        }

        //Creates the context menu when the lists are long-pressed
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(0, v.getId(), 0, "Delete");
            if(((RecyclerView)v.getParent()).getId() == R.id.bookmarklistsView){
                menu.add(0, v.getId(), 0, "Remove Bookmark");

            }
            else {
                menu.add(0, v.getId(), 0, "Bookmark");
            }        }
    }

    public BMListsAdapter(ItemsViewModel model, RecyclerView.LayoutManager layoutManager) {
        this.model = model;
        this.myLayoutManager = layoutManager;
    }

    @Override
    public int getItemViewType(int position) {
        return NEWLIST_TYPE;
    }

    @Override @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        Button newList = (Button) LayoutInflater.from(parent.getContext()).inflate(R.layout.bm_list_layout, parent, false);
        BMListsAdapter.ListViewHolder vh = new BMListsAdapter.ListViewHolder(newList);
        newList.setOnClickListener(new ListButtonListener());
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull  final RecyclerView.ViewHolder holder, int position) {
        //Get element from your dataset at this position
        ArrayList<String> keySet = new ArrayList<>(model.getBookmarkItems().keySet());
        String text = keySet.get(position);
        Button listButton = ((BMListsAdapter.ListViewHolder) holder).myListButton;
        listButton.setText(text);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //setPosition(holder.getPosition());
                setContextMenuList((Button) v);
                return false;
            }
        });
    }

    //Used to set the variable contextMenuList when a list has been longpressed
    private void setContextMenuList(Button button) {
        this.contextMenuList = button;
    }

    //Functions used to get the Button which has been longpressed
    public Button getContextMenuList() {
        return this.contextMenuList;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return model.getBMKeySet().size();
    }
}
